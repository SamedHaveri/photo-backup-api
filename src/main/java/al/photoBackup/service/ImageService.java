package al.photoBackup.service;

import al.photoBackup.exception.files.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.ImageEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ImageService {

    ImageEntity saveImage(MultipartFile multipartFile, String username)
            throws FileIsNotAnImageException, UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException;
    
    byte[] downloadFile(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException;

    List<ImageEntity> getImages(Integer userId);
}
