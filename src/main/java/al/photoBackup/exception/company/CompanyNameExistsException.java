package al.photoBackup.exception.company;

public class CompanyNameExistsException extends Exception {
	@Override
	public String getMessage() {
		return "Ekziston kompania me të njëjtin emër!";
	}
}
