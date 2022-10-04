package al.photoBackup.service;

import al.photoBackup.exception.file.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.MediaEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface MediaService {

    MediaEntity saveImage(MultipartFile multipartFile, String username)
            throws FileIsNotMedia, UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException;
    
    byte[] downloadFile(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException;

    byte[] downloadThumbnail(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException,
            FileDownloadFailedException;

    List<MediaEntity> getMedia(Integer userId);
}
