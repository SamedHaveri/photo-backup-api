package al.photoBackup.exception.auth;

public class FunctionNotAuthorisedException extends Exception {
	@Override
	public String getMessage() {
		return "Nuk keni autorizim për këtë funksion!";
	}
}
