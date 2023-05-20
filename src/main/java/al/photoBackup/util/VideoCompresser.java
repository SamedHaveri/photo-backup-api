package al.photoBackup.util;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class VideoCompresser implements Callable<String> {
    private final String filePath;
    private final String compressedDirAbsolutePath;
    private final String COMPRESSED_MEDIA_MIME;
    private final CountDownLatch latch;

    public VideoCompresser(String filePath, String compressedDirAbsolutePath, String compressed_media_mime, CountDownLatch latch) {
        this.filePath = filePath;
        this.compressedDirAbsolutePath = compressedDirAbsolutePath;
        COMPRESSED_MEDIA_MIME = compressed_media_mime;
        this.latch = latch;
    }


    @Override
    public String call() throws IOException {
        //if this throws error be sure you installed ffmpeg in you machine
        FFmpeg ffmpeg = new FFmpeg();
        FFprobe ffprobe = new FFprobe();
        var input = ffprobe.probe(filePath);

        //vefiry directory is created
        var dir = new File(compressedDirAbsolutePath);
        if(!dir.exists())
            dir.mkdirs();

        String fileName = UUID.randomUUID() + "." + COMPRESSED_MEDIA_MIME;
        String outputPath = dir.getPath() +  "/" + fileName;
        //todo make audio better ... it sucks compressed
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input).overrideOutputFiles(true)

                .addOutput(outputPath)
                .setFormat(COMPRESSED_MEDIA_MIME)
                .disableSubtitle()

//                config audio
                .setAudioChannels(1)
                .setAudioCodec("aac")
                .setAudioSampleRate(44_100)
                .setAudioBitRate(128_000)

//                config video
                .setConstantRateFactor(28)
                .setVideoCodec("libx264")
                .setVideoFrameRate(29, 1)
                .setVideoResolution(1280, 720)

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
        return fileName;
    }
}
