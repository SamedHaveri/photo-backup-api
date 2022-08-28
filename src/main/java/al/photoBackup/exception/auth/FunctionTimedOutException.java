package al.photoBackup.exception.auth;

public class FunctionTimedOutException extends Exception {
	@Override
	public String getMessage() {
		return "Ka mbaruar afati i përdorimit për funksionin!";
	}
}
