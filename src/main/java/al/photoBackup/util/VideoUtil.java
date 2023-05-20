package al.photoBackup.util;

import al.photoBackup.exception.file.ErrorCreatingDirectoryException;
import al.photoBackup.model.constant.FileSize;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
public class VideoUtil {

    @Value("${file.upload-dir}")
    private String FOLDER_PATH;

    @Value("${media.compressed-dir}")
    private String COMPRESSED_DIR;

    @Value("${media.upload-dir}")
    private String MEDIA_PATH;

    @Value("${media.compressed.mime-type}")
    private String COMPRESSED_MEDIA_MIME;

    public String makeThumbnail(File videoFile, String pathToCreate, FileSize FileSize) throws IOException {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFile.toString());
        frameGrabber.start();
        var rotation = frameGrabber.getDisplayRotation();
        Java2DFrameConverter aa = new Java2DFrameConverter();
        try {
            //  for (int i = 0; i < 1000; i++) {
            Frame f = frameGrabber.grabKeyFrame();
            BufferedImage keyImage;
            int tryKeyFrame = 4;
            int i = 1;
            keyImage = aa.convert(f);
            while (i < tryKeyFrame) {
                f = frameGrabber.grabKeyFrame();
                if (aa.convert(f) == null)
                    break;
                keyImage = aa.convert(f);
                i++;
            }
            var directory = new File(FOLDER_PATH + "/" + pathToCreate + "/");
            if (!directory.exists()) {
                if (!directory.mkdirs())
                    throw new ErrorCreatingDirectoryException();
            }
            var fileName = UUID.randomUUID() + "." + ".png";
            var file = new File(FOLDER_PATH + "/" + pathToCreate + "/" + fileName);
            var thumbWidth = FileSize.getSize();
            keyImage = Scalr.resize(keyImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, thumbWidth, Scalr.OP_ANTIALIAS);
            if(rotation == -90)
                keyImage = Scalr.rotate(keyImage, Scalr.Rotation.CW_90, Scalr.OP_ANTIALIAS);
            //todo test with other rotations
            if(rotation == -180)
                keyImage = Scalr.rotate(keyImage, Scalr.Rotation.CW_180, Scalr.OP_ANTIALIAS);
            if(rotation == -270)
                keyImage = Scalr.rotate(keyImage, Scalr.Rotation.CW_270, Scalr.OP_ANTIALIAS);
            ImageIO.write(keyImage, "png", file);
            frameGrabber.stop();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createCompressedVideo(String filePath, String userFolder) throws IOException {
        var compressedDirAbsolutePath = FOLDER_PATH + "/" + userFolder + "/" + MEDIA_PATH + "/" + COMPRESSED_DIR;
        var latch = new CountDownLatch(2);
        var videoCompresser = new VideoCompresser(filePath, compressedDirAbsolutePath, COMPRESSED_MEDIA_MIME, latch);
        return videoCompresser.call();
    }
}
