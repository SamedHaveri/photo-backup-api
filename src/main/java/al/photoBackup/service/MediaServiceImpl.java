package al.photoBackup.service;

import al.photoBackup.exception.file.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.MediaEntity;
import al.photoBackup.repository.ImageRepository;
import al.photoBackup.util.FileUploadUtil;
import al.photoBackup.util.ImageThumbnailGenerator;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MediaServiceImpl implements MediaService {
    @Value("${file.upload-dir}")
    private String ROOT_FOLDER;
    private final FileUploadUtil fileUploadUtil;
    private final UserService userService;
    private final ImageRepository imageRepository;
    private final ImageThumbnailGenerator imageThumbnailGenerator;

    public MediaServiceImpl(FileUploadUtil fileUploadUtil, UserService userService, ImageRepository imageRepository,
                            ImageThumbnailGenerator imageThumbnailGenerator) {
        this.fileUploadUtil = fileUploadUtil;
        this.userService = userService;
        this.imageRepository = imageRepository;
        this.imageThumbnailGenerator = imageThumbnailGenerator;
    }

    @Override
    public MediaEntity saveImage(MultipartFile multipartFile, String username)
            throws FileIsNotMedia, UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException {
//        File f = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//        String mimeType = new MimetypesFileTypeMap().getContentType(f);
//        String type = mimeType.split("/")[0];
//        System.out.println(mimeType);
//        if (!(type.equals("image") || type.equals("video")))
//            throw new FileIsNotAnImageException();
        var tika = new Tika();
        String mediaType, mimeType;
        try {
            String mime = tika.detect(multipartFile.getInputStream());
            mediaType = mime.split("/")[0];
            mimeType = mime.split("/")[1];
            if(!Objects.equals(mediaType, "image") && !Objects.equals(mediaType, "video")){
                throw new FileIsNotMedia();
            }
        } catch (IOException e) {
            //todo catch a proper exception
            throw new RuntimeException(e);
        }
        var user = userService.getByUsername(username);
        String filePath = fileUploadUtil.saveFile(multipartFile, user.getUniqueFolder(), "media");
        String thumbnailName;
        if(mediaType.equals("image")){
            try {
                thumbnailName = imageThumbnailGenerator.createThumbnail(multipartFile, user.getUniqueFolder()+"/media/thumbnails", mimeType);
            } catch (IOException e) {
                //todo catch a proper exception
                throw new RuntimeException(e);
            }
        }else if (mediaType.equals("video")){
            //do video thumbnail
            thumbnailName = "vid";
        }else throw new FileIsNotMedia();

        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setUserEntity(user);
        Path pathToAFile = Paths.get(filePath);
        mediaEntity.setName(multipartFile.getOriginalFilename());
        mediaEntity.setFileName(pathToAFile.getFileName().toString());
        mediaEntity.setThumbanilName(thumbnailName);
        mediaEntity.setSize(multipartFile.getSize());
        mediaEntity.setMediaType(mediaType);
        mediaEntity.setMimeType(mimeType);
        mediaEntity.setAddedAt(LocalDateTime.now());
        return imageRepository.save(mediaEntity);
    }

    @Override
    public byte[] downloadFile(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException {
        var user = userService.getById(userId);
        var imageEntity = imageRepository.getByIdAndUsername(imageId, userId);
        if (imageEntity == null)
            throw new CustomFileNotFoundException();
        String filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + "media" + "/" + imageEntity.getFileName();
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new FileDownloadFailedException();
        }
    }

    @Override
    public byte[] downloadThumbnail(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException,
            FileDownloadFailedException {
        var user = userService.getById(userId);
        var imageEntity = imageRepository.getByIdAndUsername(imageId, userId);
        if (imageEntity == null)
            throw new CustomFileNotFoundException();
        String filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/media/thumbnails/"+ imageEntity.getThumbanilName();
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new FileDownloadFailedException();
        }
    }

    @Override
    public List<MediaEntity> getMedia(Integer userId) {
        return imageRepository.getByUserEntity_Id(userId);
    }

}
