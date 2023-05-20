package al.photoBackup.service;

import al.photoBackup.exception.file.*;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.constant.MediaType;
import al.photoBackup.model.entity.MediaEntity;
import al.photoBackup.repository.MediaRepository;
import al.photoBackup.model.constant.FileSize;
import al.photoBackup.util.FileUploadUtil;
import al.photoBackup.util.ImageThumbnailGenerator;
import al.photoBackup.util.VideoUtil;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
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

    @Value("${media.upload-dir}")
    private String MEDIA_DIR;

    @Value("${media.thumbnail-upload-dir}")
    private String MEDIA_THUMBANIL_DIR;

    @Value("${media.thumbnail-mid-upload-dir}")
    private String MEDIA_THUMBANIL_MID_DIR;

    @Value("${media.compressed-dir}")
    private String COMPRESSED_DIR;

    private final FileUploadUtil fileUploadUtil;
    private final UserService userService;
    private final MediaRepository mediaRepository;
    private final ImageThumbnailGenerator imageThumbnailGenerator;
    private final VideoUtil videoUtil;

    public MediaServiceImpl(FileUploadUtil fileUploadUtil, UserService userService, MediaRepository mediaRepository,
                            ImageThumbnailGenerator imageThumbnailGenerator, VideoUtil videoUtil) {
        this.fileUploadUtil = fileUploadUtil;
        this.userService = userService;
        this.mediaRepository = mediaRepository;
        this.imageThumbnailGenerator = imageThumbnailGenerator;
        this.videoUtil = videoUtil;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MediaEntity saveMedia(MultipartFile multipartFile, String username)
            throws FileIsNotMedia, UserNameNotFoundException, ErrorCreatingFileException, ErrorCreatingDirectoryException, IOException {
        var tika = new Tika();
        String mediaType, mimeType;
        String compressedVideoName = null;

        try {
            String mime = tika.detect(multipartFile.getInputStream());
            mediaType = mime.split("/")[0];
            mimeType = mime.split("/")[1];
            if (!Objects.equals(mediaType, MediaType.IMAGE.getMediaType()) && !Objects.equals(mediaType, MediaType.VIDEO.getMediaType())) {
                throw new FileIsNotMedia();
            }
        } catch (IOException e) {
            //todo catch a proper exception
            throw new RuntimeException(e);
        }
        var user = userService.getByUsername(username);
        String filePath = fileUploadUtil.saveFile(multipartFile, user.getUniqueFolder(), MEDIA_DIR);
        String thumbnailName;
        String thumbnailMidName;
        if (mediaType.equals(MediaType.IMAGE.getMediaType())) {
            try {
                thumbnailName = imageThumbnailGenerator.createThumbnail(multipartFile,
                        user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_DIR, mimeType, FileSize.LOW);
                thumbnailMidName = imageThumbnailGenerator.createThumbnail(multipartFile,
                        user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_MID_DIR, mimeType, FileSize.MID);
            } catch (IOException e) {
                //todo catch a proper exception
                throw new RuntimeException(e);
            }
        } else if (mediaType.equals(MediaType.VIDEO.getMediaType())) {
            thumbnailName = videoUtil.makeThumbnail(new File(filePath),
                    user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_DIR, FileSize.LOW);
            thumbnailMidName = videoUtil.makeThumbnail(new File(filePath),
                    user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_MID_DIR, FileSize.MID);
            compressedVideoName = videoUtil.createCompressedVideo(filePath, user.getUniqueFolder());
        } else throw new FileIsNotMedia();

        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setUserEntity(user);
        Path pathToAFile = Paths.get(filePath);
        mediaEntity.setName(multipartFile.getOriginalFilename());
        mediaEntity.setFileName(pathToAFile.getFileName().toString());
        mediaEntity.setThumbnailName(thumbnailName);
        mediaEntity.setThumbnailMidName(thumbnailMidName);
        mediaEntity.setSize(multipartFile.getSize());
        mediaEntity.setMediaType(mediaType);
        mediaEntity.setMimeType(mimeType);
        mediaEntity.setAddedAt(LocalDateTime.now());
        mediaEntity.setCompressedVideoName(compressedVideoName);
        return mediaRepository.save(mediaEntity);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadMedia(Long mediaId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException, FileDownloadFailedException {
        var user = userService.getById(userId);
        var mediaEntity = mediaRepository.getByIdAndUserId(mediaId, userId);
        if (mediaEntity == null)
            throw new CustomFileNotFoundException();
        String filePath = null;
        //get Compressed version of video file
        if(Objects.equals(mediaEntity.getMediaType(), MediaType.VIDEO.getMediaType()))
            filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + COMPRESSED_DIR + "/" + mediaEntity.getCompressedVideoName();
        else if (Objects.equals(mediaEntity.getMediaType(), MediaType.IMAGE.getMediaType()))
            filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + mediaEntity.getFileName();

        try {
            assert filePath != null;
            var httpHeaders = new HttpHeaders();
            httpHeaders.setContentLength(Files.size(Path.of(filePath)));
            var inputStreamResource = new InputStreamResource(new FileInputStream(filePath));
            return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            throw new FileDownloadFailedException();
        }
    }

    @Override
    public byte[] downloadThumbnail(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException,
            FileDownloadFailedException {
        var user = userService.getById(userId);
        var imageEntity = mediaRepository.getByIdAndUserId(imageId, userId);
        if (imageEntity == null)
            throw new CustomFileNotFoundException();
        String filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_DIR + "/" + imageEntity.getThumbnailName();
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new FileDownloadFailedException();
        }
    }

    @Override
    public byte[] downloadThumbnailMid(Long imageId, Integer userId) throws UserIdNotFoundException, CustomFileNotFoundException,
            FileDownloadFailedException {
        var user = userService.getById(userId);
        var imageEntity = mediaRepository.getByIdAndUserId(imageId, userId);
        if (imageEntity == null)
            throw new CustomFileNotFoundException();
        String filePath = ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_MID_DIR + "/" + imageEntity.getThumbnailMidName();
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new FileDownloadFailedException();
        }
    }

    @Override
    public List<MediaEntity> getMedia(Integer userId) {
        return mediaRepository.getByUserEntity_Id(userId);
    }

    @Override
    public void delete(Long id, Integer userId) throws CustomFileNotFoundException, UserIdNotFoundException {
        var user = userService.getById(userId);
        var media = mediaRepository.getByIdAndUserId(id, userId);
        if (media == null) throw new CustomFileNotFoundException();
        var mediaFile = new File(ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + media.getFileName());
        var mediaThumbnail = new File(ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_DIR + "/" + media.getThumbnailName());
        var compressedVideo = new File(ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + COMPRESSED_DIR + "/" + media.getCompressedVideoName());
        var mediaMidThumbnail = new File(ROOT_FOLDER + "/" + user.getUniqueFolder() + "/" + MEDIA_DIR + "/" + MEDIA_THUMBANIL_MID_DIR + "/" + media.getThumbnailMidName());
        mediaFile.delete();
        mediaThumbnail.delete();
        compressedVideo.delete();
        mediaMidThumbnail.delete();
        mediaRepository.delete(media);
    }

}
