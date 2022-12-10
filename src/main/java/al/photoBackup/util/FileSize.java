package al.photoBackup.util;

public enum FileSize {
    LOW(500),
    MID(1000);

    private final int size;
    FileSize(int i) {
        this.size = i;
    }
    public int getSize(){
        return this.size;
    }
}
