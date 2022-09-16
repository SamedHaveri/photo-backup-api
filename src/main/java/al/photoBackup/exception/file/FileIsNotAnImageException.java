package al.photoBackup.exception.file;

public class FileIsNotAnImageException extends Exception{
    @Override
    public String getMessage() {
        return "File is not an image!";
    }
}
