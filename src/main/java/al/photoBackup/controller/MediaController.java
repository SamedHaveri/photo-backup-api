package al.photoBackup.controller;

import al.photoBackup.exception.file.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.image.MediaResponseDTO;
import al.photoBackup.model.dto.user.SecurityUserDetails;
import al.photoBackup.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam MultipartFile file, Authentication auth)
            throws UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException, FileIsNotMedia,
            IOException, InterruptedException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = mediaService.saveImage(file, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/download/id/{id}")
    public ResponseEntity<?> download(@PathVariable Long id, Authentication auth)
            throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = mediaService.downloadFile(id, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/download/thumbnail/id/{id}")
    public ResponseEntity<?> downloadThumbnail(@PathVariable Long id, Authentication auth)
            throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = mediaService.downloadThumbnail(id, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/download/thumbnail-mid/id/{id}")
    public ResponseEntity<?> downloadThumbnailMid(@PathVariable Long id, Authentication auth)
            throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = mediaService.downloadThumbnail(id, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MediaResponseDTO>> getMedia(Authentication auth) {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = mediaService.getMedia(userDetails.getId())
                .stream()
                .map(MediaResponseDTO::new)
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
