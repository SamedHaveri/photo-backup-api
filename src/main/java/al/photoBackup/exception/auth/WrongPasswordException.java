package al.photoBackup.exception.auth;

public class WrongPasswordException extends Exception {
	@Override
	public String getMessage() {
		return "Fjalëkalimi nuk është i saktë!";
	}
}
