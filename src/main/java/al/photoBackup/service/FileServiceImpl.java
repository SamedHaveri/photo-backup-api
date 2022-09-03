package al.photoBackup.service;

import al.photoBackup.exception.files.ErrorCreatingDirectoryException;
import al.photoBackup.exception.files.ErrorCreatingFileException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.entity.FileEntity;
import al.photoBackup.repository.FileRepository;
import al.photoBackup.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

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
        fileEntity.setName(multipartFile.getOriginalFilename());
        fileEntity.setPath(filePath);
        fileEntity.setSize(multipartFile.getSize());
        return fileRepository.save(fileEntity);
    }
}
