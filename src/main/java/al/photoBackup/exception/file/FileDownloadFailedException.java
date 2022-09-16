package al.photoBackup.exception.file;

public class FileDownloadFailedException extends Exception{
    @Override
    public String getMessage() {
        return "File Download Failed!";
    }
}
