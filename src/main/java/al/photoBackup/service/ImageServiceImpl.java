package al.photoBackup.service;

import al.photoBackup.exception.files.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.ImageEntity;
import al.photoBackup.repository.ImageRepository;
import al.photoBackup.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${file.upload-dir}")
    private String ROOT_FOLDER;
    private final FileUploadUtil fileUploadUtil;
    private final UserService userService;
    private final ImageRepository imageRepository;

    public ImageServiceImpl(FileUploadUtil fileUploadUtil, UserService userService, ImageRepository imageRepository) {
        this.fileUploadUtil = fileUploadUtil;
        this.userService = userService;
        this.imageRepository = imageRepository;
    }

    @Override
    public ImageEntity saveImage(MultipartFile multipartFile, String username)
            throws FileIsNotAnImageException, UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException {
        File f = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String mimeType = new MimetypesFileTypeMap().getContentType(f);
        String type = mimeType.split("/")[0];
        if (!type.equals("image"))
            throw new FileIsNotAnImageException();
        var user = userService.getByUsername(username);
        String filePath = fileUploadUtil.saveFile(multipartFile, user.getUniqueFolder(), "images");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUserEntity(user);
        Path pathToAFile = Paths.get(filePath);
        imageEntity.setName(pathToAFile.getFileName().toString());
        imageEntity.setSize(multipartFile.getSize());
        return imageRepository.save(imageEntity);
    }

    @Override
    public byte[] downloadFile(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException {
        var user = userService.getById(userId);
        var imageEntity = imageRepository.getByIdAndUsername(imageId, userId);
        if (imageEntity == null)
            throw new CustomFileNotFoundException();
        String filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + "images" + "/" + imageEntity.getName();
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new FileDownloadFailedException();
        }
    }

    @Override
    public List<ImageEntity> getImages(Integer userId) {
        return imageRepository.getByUserEntity_Id(userId);
    }

}
