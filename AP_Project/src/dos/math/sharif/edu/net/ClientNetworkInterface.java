package dos.math.sharif.edu.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientNetworkInterface
{
	private Socket client;
	private Scanner s ;
	private PrintWriter pw;

	public ClientNetworkInterface(String serverAddress, int serverPort,
			String user, String password) throws NetworkException, AuthenticationException
	{
		// After connecting to the server at serverAddress:serverPort, client
		// sends this message to the server for authentication: HELLO FROM <user>:<password>
		// If the pair of the user and the password is valid and the client has connection permission, server returns: HELLO <user> 
		// The the client can run queries on server through execute method and the established connection. 
		// Otherwise, server returns: ERROR <message>
		// and server closes the connection. Then, based on the error message, proper exception is thrown.

			try
			{
				String str;
				client = new Socket(serverAddress, serverPort);
				s = new Scanner(client.getInputStream());
				pw = new PrintWriter(client.getOutputStream());

				// Send the authentication information 
				pw.println("HELLO FROM " + user + ":" + password); ;
				pw.flush();

				// Get the authentication result.
				str = s.nextLine();
				if (str.contains("ERROR"))
					throw new AuthenticationException(str);
			}
			catch (IOException e) 
			{
				throw new NetworkException();
			}
	}

	public void disconnect()
	{
		// Client sends this message to the server and closes the connection: BYE
		pw.println("BYE");
		pw.flush();
	}

	public String execute(String command, String username) throws AuthenticationException
	{
		// Client sends directly to the server the command string terminated by a semicolon character at the end.
		// Server returns the result as a string terminated by semicolon character at the end.
		// Note that if the query result itself contains semicolon character, it will be escaped by repeating it twice.

		String str;

		// Send the command.
		pw.println(command + ";");
		pw.flush();

		// Get the result.
		str = s.nextLine();

		if (str.equals("ERROR: Invalid username")			// The user has been deleted by the server during the connection.
		|| str.equals("ERROR: Invalid password")			// The user's password has been changed by the server during the connection.
		|| str.equals("ERROR: Connection not permitted")	// The user's connection permission has been removed by the server during the connection.
		|| str.equals("ERROR: Invalid protocol"))			// The client-server protocol is not valid.
			throw new AuthenticationException(str);
		
		str = modify(str, username);
		str = str.replaceAll("#", "\n");
		return str;
	}

	private String modify(String str, String username)
	{
		int i = str.indexOf(username.concat("."));
		if (i != -1)
			str = str.substring(0, i).concat(str.substring(username.length() + i + 1));
		return str;
	}

}
