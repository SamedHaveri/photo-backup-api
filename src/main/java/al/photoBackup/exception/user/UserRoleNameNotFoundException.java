package al.photoBackup.exception.user;

public class UserRoleNameNotFoundException extends Exception {
	@Override
	public String getMessage() {
		return "Roli me emrin e kërkuar nuk u gjet !";
	}
}
