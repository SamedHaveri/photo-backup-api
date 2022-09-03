package al.photoBackup.service;

import al.photoBackup.exception.files.ErrorCreatingDirectoryException;
import al.photoBackup.exception.files.ErrorCreatingFileException;
import al.photoBackup.exception.files.FileIsNotAnImageException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.ImageEntity;
import al.photoBackup.repository.ImageRepository;
import al.photoBackup.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.Objects;

@Service
public class ImageServiceImpl implements ImageService {
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
        if(!type.equals("image"))
            throw new FileIsNotAnImageException();
        var user = userService.getByUsername(username);
        String filePath = fileUploadUtil.saveFile(multipartFile, user.getUniqueFolder(), "images");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUserEntity(user);
        imageEntity.setName(multipartFile.getOriginalFilename());
        imageEntity.setPath(filePath);
        imageEntity.setSize(multipartFile.getSize());
        return imageRepository.save(imageEntity);
    }
}
