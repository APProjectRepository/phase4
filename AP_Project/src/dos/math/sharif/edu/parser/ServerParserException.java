package dos.math.sharif.edu.parser;

public class ServerParserException extends Exception 
{
	private static final long serialVersionUID = 1L;
	private String message;


	public ServerParserException(int ID)
	{
		if (ID == 1)
			message = "ERROR: Syntax Error";
		else
			message = "ERROR: Invalid Password";
	}

	public String getMessage()
	{
		return message;
	}

}
