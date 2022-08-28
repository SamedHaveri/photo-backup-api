package al.photoBackup.model.constant;

public enum Status {
	ACTIVE(1),
	DISABLED(0),
	CLOSED(-1),
	BUSY(2),
	NOT_FINISHED(2),
	;

	private final int status;

	Status(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}
}
