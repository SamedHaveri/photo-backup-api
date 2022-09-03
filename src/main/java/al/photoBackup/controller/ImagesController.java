package al.photoBackup.controller;

import al.photoBackup.exception.files.ErrorCreatingDirectoryException;
import al.photoBackup.exception.files.ErrorCreatingFileException;
import al.photoBackup.exception.files.FileIsNotAnImageException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.user.SecurityUserDetails;
import al.photoBackup.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}
