package al.photoBackup.exception.auth;

public class FunctionNotAllowedForUserException extends Exception {
	@Override
	public String getMessage() {
		return "Nuk jeni të autorizuar për këtë funksion!";
	}
}
