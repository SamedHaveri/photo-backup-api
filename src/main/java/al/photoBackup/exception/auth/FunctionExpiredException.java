package al.photoBackup.exception.auth;

public class FunctionExpiredException extends Exception {
	@Override
	public String getMessage() {
		return "Funksioni i kërkuar nuk është më i disponueshëm!";
	}
}
