package al.photoBackup.model.constant;

public enum FileSize {
    LOW(400),
    MID(700);

    private final int size;
    FileSize(int i) {
        this.size = i;
    }
    public int getSize(){
        return this.size;
    }
}
