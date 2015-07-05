package dos.math.sharif.edu.cli;

import dos.math.sharif.edu.net.ClientNetworkInterface;
import dos.math.sharif.edu.query.result.QueryResult;

public class ClientUserInterface
{
	public static void main(String[] args)
	{
		// Establish connection to the server
		String serverAddress = "";		// received from GUI
		int serverPort = 0;				// received from GUI
		String user = "";				// received from GUI
		String password = "";			// received from GUI
		
		ClientNetworkInterface connection = null;
		ClientGraphics g = new ClientGraphics();
		InputReader input = new GUIInputReader(g);
		OutputWriter output = new GUIOutputWriter(g);

		while (true)
		{
			try
			{
				while (!g.connected)
				{
					Thread.sleep(250);
					if (g.connectPressed)
					{
						g.connectPressed = false;
						serverAddress = g.address;
						serverPort = Integer.valueOf(g.port);
						user = g.username;
						password = g.password;
						
						try
						{
							connection = new ClientNetworkInterface(serverAddress, serverPort, user, password);
							g.connected = true;
							g.lblStatus.setText("status: connected");
						}
						catch (Exception e)
						{
							output.format(new QueryResult(false, e.getMessage(), "", 0));
						}
					}
				}
				
				output.format(new QueryResult(true, "Successfully connected to server!", "", 0));
	
				try
				{
					while (input.hasNext())
					{
						Thread.sleep(250);	
						String command = input.next();								// next command
						if (!command.isEmpty())
						{
							String result = connection.execute(command, user);
							output.format(new QueryResult(true, result, "", 0));	// Display the query result in GUI.
						}
					}
					output.format(new QueryResult(true, "Connection closed successfully!", "", 0));
				}
				catch (Exception e)
				{
					output.format(new QueryResult(false, e.getMessage(), "", 0));
				}
				
				g.connected = false;
				g.lblStatus.setText("status: disconnected");
				g.browseCommands.clear();
				input.close();
				output.close();
				connection.disconnect();
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
