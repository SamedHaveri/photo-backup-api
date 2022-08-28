package al.photoBackup.exception.company;

public class CompanyNotFoundException extends Exception {
	@Override
	public String getMessage() {
		return "Kompania nuk u gjet!";
	}
}
