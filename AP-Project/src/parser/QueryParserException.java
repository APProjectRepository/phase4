package parser;

public class QueryParserException extends Exception
{
	private static final long serialVersionUID = 1L;
	String message;

	public QueryParserException(int ID, String str)
	{
		switch (ID)
		{
			case 1:
				message = "ERROR: Invalid command";
				break;
			case 2:
				message = "ERROR: Duplicate column name " + str;
				break;
			case 3:
				message = "ERROR: Invalid data type " + str;
				break;
			case 4:
				message = "ERROR: Unequal number of columns and values";
		}
	}

	public String getMessage()
	{
		return message;
	}
}
