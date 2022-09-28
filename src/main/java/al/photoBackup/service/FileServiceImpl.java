package al.photoBackup.service;

import al.photoBackup.exception.file.CustomFileNotFoundException;
import al.photoBackup.exception.file.ErrorCreatingDirectoryException;
import al.photoBackup.exception.file.ErrorCreatingFileException;
import al.photoBackup.exception.file.FileDownloadFailedException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.FileEntity;
import al.photoBackup.repository.FileRepository;
import al.photoBackup.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Value("${file.upload-dir}")
    private String ROOT_FOLDER;
    private final FileUploadUtil fileUploadUtil;
    private final UserService userService;
    private final FileRepository fileRepository;

    public FileServiceImpl(FileUploadUtil fileUploadUtil, UserService userService, FileRepository fileRepository) {
        this.fileUploadUtil = fileUploadUtil;
        this.userService = userService;
        this.fileRepository = fileRepository;
    }

    @Override
    public FileEntity saveFile(MultipartFile multipartFile, String username)
            throws ErrorCreatingFileException, ErrorCreatingDirectoryException, UserNameNotFoundException {
        var user = userService.getByUsername(username);
        String filePath = fileUploadUtil.saveFile(multipartFile, user.getUniqueFolder(), "files");
        FileEntity fileEntity = new FileEntity();
        fileEntity.setUserEntity(user);
        Path pathToAFile = Paths.get(filePath);
        fileEntity.setName(multipartFile.getOriginalFilename());
        fileEntity.setFileName(pathToAFile.getFileName().toString());
        fileEntity.setSize(multipartFile.getSize());
        return fileRepository.save(fileEntity);
    }

    @Override
    public byte[] downloadFile(Long fileId, Integer userId) throws UserIdNotFoundException, FileDownloadFailedException, CustomFileNotFoundException {
        var user = userService.getById(userId);
        var fileEntity = fileRepository.getByIdAndUsername(fileId, userId);
        if (fileEntity == null)
            throw new CustomFileNotFoundException();
        String filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + "files" + "/" + fileEntity.getFileName();
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new FileDownloadFailedException();
        }
    }

    @Override
    public List<FileEntity> getFiles(Integer userId) {
        return fileRepository.getByUserEntity_Id(userId);
    }

}
