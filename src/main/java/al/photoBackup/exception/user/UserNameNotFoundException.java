package al.photoBackup.exception.user;

public class UserNameNotFoundException extends Exception {
	@Override
	public String getMessage() {
		return "Përdoruesi me emrin e kërkuar nuk u gjet!";
	}
}
