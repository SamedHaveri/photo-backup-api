package al.photoBackup.exception.user;

public class UserIdNotFoundException extends Exception {
	@Override
	public String getMessage() {
		return "Përdoruesi me ID e dhënë nuk ekziston !";
	}
}
