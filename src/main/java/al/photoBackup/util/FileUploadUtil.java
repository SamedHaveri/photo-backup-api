package al.photoBackup.util;

import al.photoBackup.exception.file.ErrorCreatingDirectoryException;
import al.photoBackup.exception.file.ErrorCreatingFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class FileUploadUtil {
    @Value("${file.upload-dir}")
    private String FOLDER_PATH;

    public String saveFile(MultipartFile multipartFile, String uniqueFolderName, String subfolder)
            throws ErrorCreatingDirectoryException, ErrorCreatingFileException {
        String uploadPath = FOLDER_PATH + "/" + uniqueFolderName +"/"+ subfolder+"/";
        File directory = new File(uploadPath);
        String filePath = uploadPath + UUID.randomUUID() +"-"+ LocalDateTime.now();
        File file = new File(filePath);
        if(!directory.exists()){
            if(!directory.mkdirs())
                throw new ErrorCreatingDirectoryException();
        }
        try {
            file.createNewFile();
            multipartFile.transferTo(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ErrorCreatingFileException();
        }
        return filePath;
    }
}
