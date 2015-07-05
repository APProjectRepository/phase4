package dos.math.sharif.edu.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import dos.math.sharif.edu.storage.DataStorage;

public class ServerNetworkInterface extends Thread
{
	private DataStorage ds;
	private ServerSocket serverSocket;
	
	public ServerNetworkInterface(DataStorage ds, int port)
	{
		this.ds = ds;
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				Socket client = serverSocket.accept();
				new ClientService(ds, client).start();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
