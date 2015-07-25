package net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TemporaryClient {
	
	private ClientNetworkInterface client ;
	
	public TemporaryClient(String IP, int Port) throws NetworkException, AuthenticationException {
		client = new ClientNetworkInterface(IP, Port, "guest", "");
	}
	public String execute(String command) throws AuthenticationException{
		return client.execute(command, "guest");
	}
	public void disconnect() {
		client.disconnect();
	}
	

}
