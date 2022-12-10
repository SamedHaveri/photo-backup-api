package al.photoBackup.util;

import al.photoBackup.exception.file.ErrorCreatingDirectoryException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class VideoThumbnailGenerator {

    @Value("${file.upload-dir}")
    private String FOLDER_PATH;

    public String makeThumbnail(File videoFile, String path, FileSize FileSize) throws FFmpegFrameGrabber.Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFile.toString());
        frameGrabber.start();
        Java2DFrameConverter aa = new Java2DFrameConverter();
        try {
            //  for (int i = 0; i < 1000; i++) {
            Frame f = frameGrabber.grabKeyFrame() ;
            BufferedImage keyImage;
            int tryKeyFrame = 4;
            int i = 1;
            keyImage = aa.convert(f);
            while (i < tryKeyFrame)
            {
                f = frameGrabber.grabKeyFrame();
                if(aa.convert(f) == null)
                    break;
                keyImage = aa.convert(f);
                i++;
            }
            var directory = new File(FOLDER_PATH+"/"+path+"/");
            if(!directory.exists()){
                if(!directory.mkdirs())
                    throw new ErrorCreatingDirectoryException();
            }
            var fileName = UUID.randomUUID()+"."+".png";
            var file = new File(FOLDER_PATH+"/"+path+"/"+fileName);
            var thumbWidth = FileSize.getSize();
            keyImage = Scalr.resize(keyImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, thumbWidth, Scalr.OP_ANTIALIAS);
            ImageIO.write(keyImage, "png", file);
            frameGrabber.stop();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void makeThumbnailOld(File videoFile) throws IOException, InterruptedException {

        Demuxer demuxer = Demuxer.make();
        demuxer.open(videoFile.toString(), null, false, true, null, null);
        final int numStreams = demuxer.getNumStreams();
        int streamIndex = -1;
        Decoder videoDecoder = null;
        String rotate = null;
        for (int i = 0; i < numStreams; ++i) {
            final DemuxerStream stream = demuxer.getStream(i);
            final KeyValueBag metaData = stream.getMetaData();
            final Decoder decoder = stream.getDecoder();
            if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
                videoDecoder = decoder;
                streamIndex = i;
                rotate = metaData.getValue("rotate", KeyValueBag.Flags.KVB_NONE);
                break;
            }
        }
        if (videoDecoder == null) {
            throw new IOException("Not a valid video file");
        }
        videoDecoder.open(null, null);

//        final MediaPicture picture = MediaPicture.make(
//                videoDecoder.getWidth(),
//                videoDecoder.getHeight(),
//                videoDecoder.getPixelFormat());
        final MediaPicture picture = MediaPicture.make(
                videoDecoder.getWidth(),
                videoDecoder.getHeight(),
                videoDecoder.getPixelFormat());

        final MediaPictureConverter converter =
                MediaPictureConverterFactory.createConverter(
                        MediaPictureConverterFactory.HUMBLE_BGR_24,
                        picture);

        final MediaPacket packet = MediaPacket.make();
        BufferedImage image = null;
        MUX:
        while (demuxer.read(packet) >= 0) {
            if (packet.getStreamIndex() != streamIndex) {
                continue;
            }
            int offset = 0;
            int bytesRead = 0;
            videoDecoder.decodeVideo(picture, packet, offset);
            do {
                bytesRead += videoDecoder.decode(picture, packet, offset);
                if (picture.isComplete()) {
                    image = converter.toImage(null, picture);
                    break MUX;
                }
                offset += bytesRead;

            } while (offset < packet.getSize());
        }
        if (image == null) {
            throw new IOException("Unable to find a complete video frame");
        }
        if (rotate != null) {
            final AffineTransform transform = new AffineTransform();
            transform.translate(0.5 * image.getHeight(), 0.5 * image.getWidth());
            transform.rotate(Math.toRadians(Double.parseDouble(rotate)));
            transform.translate(-0.5 * image.getWidth(), -0.5 * image.getHeight());
            final AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            image = op.filter(image, null);
        }

//        final Path target = videoFile.getParent().resolve(videoFile.getName() + ".thumb.jpg");
        final Path target = Paths.get(FOLDER_PATH + "test.thumb.jpg");

        final double mul;
        if (image.getWidth() > image.getHeight()) {
            mul = 216 / (double) image.getWidth();
        } else {
            mul = 216 / (double) image.getHeight();
        }

        final int newW = (int) (image.getWidth() * mul);
        final int newH = (int) (image.getHeight() * mul);
        final Image thumbnailImage = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        image = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_BGR);

        final Graphics2D g2d = image.createGraphics();
        g2d.drawImage(thumbnailImage, 0, 0, null);
        g2d.dispose();

        ImageIO.write(image, "jpeg", target.toFile());
//        return target.toAbsolutePath();
    }
}
