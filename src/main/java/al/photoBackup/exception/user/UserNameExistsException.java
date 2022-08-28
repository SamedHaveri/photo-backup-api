package al.photoBackup.exception.user;

public class UserNameExistsException extends Exception {
	@Override
	public String getMessage() {
		return "Emërtimi është i zënë";
	}
}
