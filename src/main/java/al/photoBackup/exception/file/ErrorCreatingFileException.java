package al.photoBackup.exception.file;

public class ErrorCreatingFileException extends Exception{
    @Override
    public String getMessage() {
        return "Error creating file";
    }
}
