package net;

public class NetworkException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public String getMessage()
	{
		return "ERROR: Unknown host";
	}
}
