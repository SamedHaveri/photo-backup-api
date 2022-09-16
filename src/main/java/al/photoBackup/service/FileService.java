package al.photoBackup.service;

import al.photoBackup.exception.file.CustomFileNotFoundException;
import al.photoBackup.exception.file.ErrorCreatingDirectoryException;
import al.photoBackup.exception.file.ErrorCreatingFileException;
import al.photoBackup.exception.file.FileDownloadFailedException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.FileEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService {
    FileEntity saveFile(MultipartFile multipartFile, String username)
            throws ErrorCreatingFileException, ErrorCreatingDirectoryException, UserNameNotFoundException;
    byte[] downloadFile(Long fileId, Integer userId) throws UserIdNotFoundException, FileDownloadFailedException, CustomFileNotFoundException;

    List<FileEntity> getFiles(Integer userId);
}
