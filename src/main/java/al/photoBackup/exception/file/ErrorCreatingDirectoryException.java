package al.photoBackup.exception.file;

public class ErrorCreatingDirectoryException extends Exception{
    @Override
    public String getMessage() {
        return "Error creating directory";
    }
}
