package al.photoBackup.service;

import al.photoBackup.exception.file.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.MediaEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface MediaService {

    MediaEntity saveMedia(MultipartFile multipartFile, String username)
            throws FileIsNotMedia, UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException, IOException, InterruptedException;
    
    ResponseEntity<InputStreamResource> downloadMedia(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException;

    byte[] downloadThumbnail(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException,
            FileDownloadFailedException;

    byte[] downloadThumbnailMid(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException,
            FileDownloadFailedException;

    List<MediaEntity> getMedia(Integer userId);

    void delete(Long id, Integer userId) throws CustomFileNotFoundException, UserIdNotFoundException;
}
