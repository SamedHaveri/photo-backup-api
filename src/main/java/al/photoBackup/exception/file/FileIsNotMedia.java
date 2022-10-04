package al.photoBackup.exception.file;

public class FileIsNotMedia extends Exception{
    @Override
    public String getMessage() {
        return "File is not an image or video!";
    }
}
