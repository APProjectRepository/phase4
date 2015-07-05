package dos.math.sharif.edu.storage;

public class QueryException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message;
	
	public QueryException(int ID, String str)
	{
		switch (ID)
		{
			case 1:
				message = "ERROR: Table " + str + " already exists";
				break;
			case 2:
				message = "ERROR: Invalid table name " + str;
				break;
			case 3:
				message = "ERROR: Invalid column name " + str;
				break;
			case 4:
				message = "ERROR: Invalid data type for value of column " + str;
				break;
			case 5:
				message = "ERROR: User " + str.substring(1, str.length() - 1) + " already exists";
				break;
			case 6:
				message = "ERROR: User " + str.substring(1, str.length() - 1) + " not found";
				break;
			case 7:
				message = "ERROR: User " + str.substring(1, str.length() - 1) + " already granted connection";
				break;
			case 8:
				message = "ERROR: User " + str.substring(1, str.length() - 1) + " already revoked connection";
				break;
			case 9:
				message = "ERROR: User " + str.substring(1, str.length() - 1) + " already granted DBA";
				break;
			case 10:
				message = "ERROR: User " + str.substring(1, str.length() - 1) + " already revoked DBA";
				break;
			case 11:
				message = "ERROR: User " + str.substring(1, str.length() - 1) + " has data";
				break;
			case 12:
				message = "ERROR: Table " + str.substring(1, str.length() - 1) + " not found";
				break;
		}
	}
	
	public String getMessage()
	{
		return message;
	}

}
