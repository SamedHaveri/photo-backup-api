package al.photoBackup.exception.file;

public class CustomFileNotFoundException extends Exception{
    @Override
    public String getMessage() {
        return "File Not Found";
    }
}
