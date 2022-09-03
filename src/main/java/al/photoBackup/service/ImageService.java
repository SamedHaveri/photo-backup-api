package al.photoBackup.service;

import al.photoBackup.exception.files.ErrorCreatingDirectoryException;
import al.photoBackup.exception.files.ErrorCreatingFileException;
import al.photoBackup.exception.files.FileIsNotAnImageException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.ImageEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {

    ImageEntity saveImage(MultipartFile multipartFile, String username)
            throws FileIsNotAnImageException, UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException;
}
