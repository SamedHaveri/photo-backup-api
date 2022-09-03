package al.photoBackup.controller;

import al.photoBackup.exception.files.ErrorCreatingDirectoryException;
import al.photoBackup.exception.files.ErrorCreatingFileException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.user.SecurityUserDetails;
import al.photoBackup.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam MultipartFile file, Authentication auth)
            throws UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = fileService.saveFile(file, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
