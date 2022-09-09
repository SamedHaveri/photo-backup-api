package al.photoBackup.controller;

import al.photoBackup.exception.files.CustomFileNotFoundException;
import al.photoBackup.exception.files.ErrorCreatingDirectoryException;
import al.photoBackup.exception.files.ErrorCreatingFileException;
import al.photoBackup.exception.files.FileDownloadFailedException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.file.FileResponseDTO;
import al.photoBackup.model.dto.user.SecurityUserDetails;
import al.photoBackup.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/download/id/{id}")
    public ResponseEntity<?> download(@PathVariable Long id, Authentication auth)
            throws UserIdNotFoundException, FileDownloadFailedException, CustomFileNotFoundException {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = fileService.downloadFile(id, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("application/octet-stream"))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<FileResponseDTO>> getFiles(Authentication auth) {
        var userDetails = (SecurityUserDetails) auth.getPrincipal();
        var response = fileService.getFiles(userDetails.getId())
                .stream()
                .map(FileResponseDTO::new)
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
