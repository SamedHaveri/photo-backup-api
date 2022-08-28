package al.photoBackup.service;

import al.photoBackup.exception.company.CompanyNameExistsException;
import al.photoBackup.exception.company.CompanyNotFoundException;
import al.photoBackup.model.dto.company.CompanyInsertDTO;
import al.photoBackup.model.dto.company.CompanyUpdateDTO;
import al.photoBackup.model.entity.CompanyEntity;
import al.photoBackup.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

	private final CompanyRepository companyRepository;

	public CompanyServiceImpl(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	@Override
	public CompanyEntity getById(int id) {
		return companyRepository.findById(id).orElse(null);
	}

	@Override
	public CompanyEntity getByName(String name) {
		return companyRepository.findByName(name);
	}

	@Override
	public List<CompanyEntity> getAll() {
		return companyRepository.getAll();
	}

	@Override
	public List<CompanyEntity> getExpired() {
		return companyRepository.getExpired();
	}

	@Override
	public List<CompanyEntity> getActive() {
		return companyRepository.getActive();
	}

	@Override
	public CompanyEntity save(CompanyInsertDTO dto) throws CompanyNameExistsException {
		final var companyFoundByName = companyRepository.findByName(dto.getName());
		if (companyFoundByName != null)
			throw new CompanyNameExistsException();
		var company = new CompanyEntity();
		company.setName(dto.getName());
		company.setPaymentDeadline(dto.getPaymentDeadline());
		company.setMaxUsers(dto.getMaxUsers());
		return companyRepository.save(company);
	}

	@Override
	public CompanyEntity update(CompanyUpdateDTO dto) throws CompanyNotFoundException, CompanyNameExistsException {
		final var companyFound = companyRepository
				.findById(dto.getId())
				.orElseThrow(CompanyNotFoundException::new);

		//check if another company with same name exists
		final var idFromName = companyRepository.getId(dto.getName());
		if (idFromName != null && !idFromName.equals(companyFound.getId()))
			throw new CompanyNameExistsException();

		companyFound.setName(dto.getName());
		companyFound.setMaxUsers(dto.getMaxUsers());
		companyFound.setPaymentDeadline(dto.getPaymentDeadline());
		return companyRepository.save(companyFound);
	}

	@Override
	public void delete(Integer id) throws CompanyNotFoundException {
		final var company = companyRepository
				.findById(id)
				.orElseThrow(CompanyNotFoundException::new);
		companyRepository.deleteById(id);
		companyRepository.save(company);
	}

	@Override
	public Integer getId(String name) {
		return companyRepository.getId(name);
	}

}
