package al.photoBackup.service;

import al.photoBackup.exception.files.ErrorCreatingDirectoryException;
import al.photoBackup.exception.files.ErrorCreatingFileException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.FileEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileService {
    FileEntity saveFile(MultipartFile multipartFile, String username)
            throws ErrorCreatingFileException, ErrorCreatingDirectoryException, UserNameNotFoundException;
}
