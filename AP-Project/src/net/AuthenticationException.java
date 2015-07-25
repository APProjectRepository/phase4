package net;

public class AuthenticationException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String message;

	public AuthenticationException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
}
