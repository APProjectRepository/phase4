package storage;

public class StorageException extends Exception {
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "ERROR: Database root folder does not exist";
	}
}
