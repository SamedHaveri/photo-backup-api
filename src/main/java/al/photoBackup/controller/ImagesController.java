package al.photoBackup.controller;

import al.photoBackup.exception.files.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.image.ImageResponseDTO;
import al.photoBackup.model.dto.user.SecurityUserDetails;
import al.photoBackup.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/images")
public class ImagesController {
    private final ImageService imageService;

    public ImagesController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam MultipartFile file, Authentication auth)
            throws UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException, FileIsNotAnImageException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = imageService.saveImage(file, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/download/id/{id}")
    public ResponseEntity<?> download(@PathVariable Long id, Authentication auth)
            throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = imageService.downloadFile(id, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ImageResponseDTO>> getImages(Authentication auth) {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = imageService.getImages(userDetails.getId())
                .stream()
                .map(ImageResponseDTO::new)
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
