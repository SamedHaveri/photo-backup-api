package al.photoBackup.service;

import al.photoBackup.exception.auth.WrongPasswordException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.exception.user.UserRoleNameNotFoundException;
import al.photoBackup.model.constant.Role;
import al.photoBackup.model.dto.user.UserInsertDTO;
import al.photoBackup.model.dto.user.UserPageResponseDTO;
import al.photoBackup.model.dto.user.UserPasswordDTO;
import al.photoBackup.model.dto.user.UserUpdateDTO;
import al.photoBackup.model.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends UserDetailsService {

    UserEntity getById(Integer id, Integer companyId);

    UserEntity getActiveByUsername(String username) throws UserNameNotFoundException;

    List<UserEntity> getAllActive();

    UserPageResponseDTO searchInCompany(String name, Integer pageNumber, Integer pageSize, String sortColumn,
                                        String sortOrder, Integer companyId);

    UserEntity save(UserInsertDTO dto, Integer companyId) throws UserNameExistsException, UserRoleNameNotFoundException;

    UserEntity save(UserEntity userEntity) throws UserNameExistsException;

    UserEntity updatePassword(String currentLoggedUsername, UserPasswordDTO dto) throws WrongPasswordException;

    void disableUser(Integer id, Integer companyId) throws UserNameNotFoundException;

    UserEntity update(UserUpdateDTO updatedUser, Integer companyId)
            throws UserIdNotFoundException, UserRoleNameNotFoundException, UserNameExistsException;

    Integer getId(String username);

    List<UserEntity> getByCompanyId(Integer companyId);

    List<Role> getAllRoles();

    List<String> getNonSystemRoles();

    Role getRole(String role) throws UserRoleNameNotFoundException;

    Role getNonSystemRole(String role) throws UserRoleNameNotFoundException;
}
