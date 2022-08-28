package al.photoBackup.controller;

import al.photoBackup.exception.company.CompanyNameExistsException;
import al.photoBackup.exception.company.CompanyNotFoundException;
import al.photoBackup.model.dto.company.CompanyInsertDTO;
import al.photoBackup.model.dto.company.CompanyResponseDTO;
import al.photoBackup.model.dto.company.CompanyUpdateDTO;
import al.photoBackup.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/company")
public class CompanyController {

	private final CompanyService companyService;

	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@GetMapping("/id/{id}")
	public ResponseEntity<CompanyResponseDTO> getById(@PathVariable @Min(1) Integer id) throws CompanyNotFoundException {
		final var company = companyService.getById(id);
		if (company == null)
			throw new CompanyNotFoundException();
		return new ResponseEntity<>(new CompanyResponseDTO(company), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@GetMapping("/")
	public ResponseEntity<List<CompanyResponseDTO>> getAll() {
		final var companiesFound = companyService.getAll()
				.stream()
				.map(CompanyResponseDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(companiesFound, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@GetMapping("/active")
	public ResponseEntity<List<CompanyResponseDTO>> getAllActive() {
		final var companiesFound = companyService.getActive()
				.stream()
				.map(CompanyResponseDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(companiesFound, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@GetMapping("/expired")
	public ResponseEntity<List<CompanyResponseDTO>> getAllExpired() {
		final var companiesFound = companyService.getExpired()
				.stream()
				.map(CompanyResponseDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(companiesFound, HttpStatus.OK);
	}

	@PostMapping("/")
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	public ResponseEntity<CompanyResponseDTO> save(@Valid @RequestBody CompanyInsertDTO dto)
			throws CompanyNameExistsException {
		final var company = companyService.save(dto);
		return new ResponseEntity<>(new CompanyResponseDTO(company), HttpStatus.OK);
	}

	@PutMapping("/")
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	public ResponseEntity<CompanyResponseDTO> update(@Valid @RequestBody CompanyUpdateDTO dto)
			throws CompanyNotFoundException, CompanyNameExistsException {
		final var company = companyService.update(dto);
		return new ResponseEntity<>(new CompanyResponseDTO(company), HttpStatus.OK);
	}

}
