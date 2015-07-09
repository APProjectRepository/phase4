package orm;

public class InvalidORClass extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidORClass() {
		super("You can't create any object from this class");
	}
}
