package net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import parser.QueryParser;
import query.PermissionQuery;
import query.Query;
import query.SingleTableQuery;
import query.result.QueryResult;
import storage.DataStorage;

public class ClientService extends Thread {
	private DataStorage ds;
	private Socket client;

	public ClientService(DataStorage ds, Socket client) {
		this.ds = ds;
		this.client = client;
	}

	@Override
	public void run() {
		Scanner s = null;
		PrintWriter pw = null;
		String username, password, command = "", strTemp;
		String[] strArray;
		QueryParser parser = new QueryParser();
		String[] userInfo;
		int index, l;
		try {
			s = new Scanner(client.getInputStream());
			pw = new PrintWriter(client.getOutputStream());

			// Get the user's name and password.
			strTemp = s.nextLine();
			strArray = strTemp.split(" ");

			if ((strArray.length == 3) && (strArray[0].equals("HELLO"))
					&& (strArray[1].equals("FROM"))
					&& ((index = strArray[2].indexOf(":")) > 0)) {
				username = strArray[2].substring(0, index);
				username = "\"" + username + "\"";
				password = strArray[2].substring(index + 1);
				password = "\"" + password + "\"";

				// Check the user's information.
				userInfo = ds.userInfo(username);

				if (!username.equalsIgnoreCase(userInfo[0])) {
					pw.println("ERROR: Invalid username");
					pw.flush();
				} else if (!password.equalsIgnoreCase(userInfo[1])) {
					pw.println("ERROR: Invalid password");
					pw.flush();
				} else if (!userInfo[2].equalsIgnoreCase("true")) {
					pw.println("ERROR: Connection not permitted");
					pw.flush();
				} else {
					pw.println("HELLO " + username);
					pw.flush();

					while (true) {
						try {
							// Get the command.
							command = s.nextLine();

							// Check if the client wants to close the
							// connection.
							if (command.equals("BYE"))
								break;

							// Check the username, password, and connection
							// permission for each received command.
							userInfo = ds.userInfo(username);
							if (!username.equalsIgnoreCase(userInfo[0])) {
								pw.println("ERROR: Invalid username");
								pw.flush();
								break;
							}
							if (!password.equalsIgnoreCase(userInfo[1])) {
								pw.println("ERROR: Invalid password");
								pw.flush();
								break;
							}
							if (!userInfo[2].equalsIgnoreCase("true")) {
								pw.println("ERROR: Connection not permitted");
								pw.flush();
								break;
							}

							l = command.length();
							if ((l < 1) || command.charAt(l - 1) != ';') {
								pw.println("ERROR: Invalid protocol");
								pw.flush();
								break;
							}
							command = command.substring(0, l - 1);

							Query q = parser.parse(command);
							if (!(q instanceof PermissionQuery)) {
								SingleTableQuery qTemp = (SingleTableQuery) q;
								if (qTemp.table.name.contains("."))
								// The user wants to manipulate other users'
								// tables.
								{
									if (!ds.userPermitted(username, qTemp)) {
										pw.println("ERROR: DBA not permitted");
										pw.flush();
										continue;
									}
								} else
								// The user wants to manipulate their own
								// tables.
								{
									if (!userInfo[3].equals("true")) {
										pw.println("ERROR: DBA not permitted");
										pw.flush();
										continue;
									} else
										qTemp.setUser(username);
								}
							} else {
								PermissionQuery qTemp = (PermissionQuery) q;
								qTemp.setOwner(username);
							}
							QueryResult res = ds.execute(q);
							// Send the result
							strTemp = res.message;
							strTemp = strTemp.replaceAll("\n", "#");
							pw.println(strTemp);
							pw.flush();
						} catch (Exception e) {
							pw = new PrintWriter(client.getOutputStream());
							pw.println(new QueryResult(false, e.getMessage(),
									"", 0).message);
							pw.flush();
						}
					}
				}
			} else {
				pw.println("ERROR: Invalid protocol");
				pw.flush();
			}

			s.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
