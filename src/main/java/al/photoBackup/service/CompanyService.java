package al.photoBackup.service;

import al.photoBackup.exception.company.CompanyNameExistsException;
import al.photoBackup.exception.company.CompanyNotFoundException;
import al.photoBackup.model.dto.company.CompanyInsertDTO;
import al.photoBackup.model.dto.company.CompanyUpdateDTO;
import al.photoBackup.model.entity.CompanyEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {

	CompanyEntity getById(int id);

	CompanyEntity getByName(String name);

	List<CompanyEntity> getAll();

	List<CompanyEntity> getExpired();

	List<CompanyEntity> getActive();

	CompanyEntity save(CompanyInsertDTO dto) throws CompanyNameExistsException;

	CompanyEntity update(CompanyUpdateDTO dto) throws CompanyNotFoundException, CompanyNameExistsException;

	void delete(Integer id) throws CompanyNotFoundException;

	Integer getId(String name);

}
