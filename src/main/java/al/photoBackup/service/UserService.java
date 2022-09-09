package al.photoBackup.service;

import al.photoBackup.exception.auth.WrongPasswordException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.user.UserPasswordDTO;
import al.photoBackup.model.dto.user.UserUpdateDTO;
import al.photoBackup.model.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {

    UserEntity getByUsername(String username) throws UserNameNotFoundException;
    UserEntity getById(Integer id) throws UserIdNotFoundException;

    UserEntity updatePassword(String currentLoggedUsername, UserPasswordDTO dto) throws WrongPasswordException, UserNameNotFoundException;

    void disableUser(Integer id) throws UserNameNotFoundException;

    UserEntity save(UserEntity userEntity) throws UserNameExistsException;

    UserEntity update(UserUpdateDTO updatedUser, Integer companyId)
            throws UserIdNotFoundException, UserNameExistsException;

}
