package al.photoBackup.model.constant;

public enum MediaType {
    IMAGE("image"),
    VIDEO("video");

    private final String mediaType;
    MediaType(String i) {
        this.mediaType = i;
    }
    public String getMediaType(){
        return this.mediaType;
    }
}
