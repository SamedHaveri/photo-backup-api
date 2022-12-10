package al.photoBackup.util;

import al.photoBackup.exception.file.ErrorCreatingDirectoryException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageThumbnailGenerator {
    @Value("${file.upload-dir}")
    private String FOLDER_PATH;
    public String createThumbnail(MultipartFile original, String path, String mimeType, FileSize FileSize ) throws
            IOException, ErrorCreatingDirectoryException {
        var thumbWidth = FileSize.getSize();
        BufferedImage thumbImg = null;
        var img = ImageIO.read(original.getInputStream());
        thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, thumbWidth, Scalr.OP_ANTIALIAS);
        var directory = new File(FOLDER_PATH+"/"+path+"/");
        if(!directory.exists()){
            if(!directory.mkdirs())
                throw new ErrorCreatingDirectoryException();
        }
        var fileName = UUID.randomUUID()+"."+mimeType;
        var file = new File(FOLDER_PATH+"/"+path+"/"+fileName);
        ImageIO.write(thumbImg, mimeType, file);
        return fileName;
    }
}
