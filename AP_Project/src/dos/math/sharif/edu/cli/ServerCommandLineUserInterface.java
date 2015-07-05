package dos.math.sharif.edu.cli;

import java.util.Vector;

import dos.math.sharif.edu.net.ServerNetworkInterface;
import dos.math.sharif.edu.parser.ServerParser;
import dos.math.sharif.edu.query.CreateTableQuery;
import dos.math.sharif.edu.query.SingleTableQuery;
import dos.math.sharif.edu.query.result.QueryResult;
import dos.math.sharif.edu.storage.DataStorage;
import dos.math.sharif.edu.storage.QueryException;

public class ServerCommandLineUserInterface
{
	public static void main(String[] args)
	{
		try
		{
			String dsPath = args[0];
			int port = Integer.valueOf(args[1]);

			// Get commands from the user and run them on the storage.
			DataStorage ds = new DataStorage(dsPath);
			ServerParser parser = new ServerParser();
			InputReader input = new ConsoleInputReader();
			OutputWriter output = new ConsoleOutputWriter();
			createUserInfoTables(ds);

			// Instantiate the network interface.
			new ServerNetworkInterface(ds, port).start();
			
			output.format(new QueryResult (true, "Server started successfully on repository " + dsPath + " and port " + port, "", 0));

			// for each input command
			while (input.hasNext())
			{
				try
				{
					String command = input.next();				// next command
					SingleTableQuery q = parser.parse(command);
					QueryResult res = ds.execute(q);
					output.format(res);							// write the query result into server console.
				}
				catch(Exception e)
				{
					output.format(new QueryResult(false, e.getMessage(), "", 0));
				}
			}

			input.close();
			output.close();
			ds.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// Create 'users' and 'permissions' tables for storing users' data and permissions.
	public static void createUserInfoTables(DataStorage ds) throws QueryException
	{
		// Create a table for storing the users' data.
		if (!ds.tableExists("users"))
		{
			Vector<String> vec = new Vector<String>();
			vec.addElement("users");
			vec.addElement("username");
			vec.addElement("string");
			vec.addElement("password");
			vec.addElement("string");
			vec.addElement("connect");
			vec.addElement("boolean");
			vec.addElement("DBA");
			vec.addElement("boolean");
			CreateTableQuery q1 = new CreateTableQuery(vec);
			ds.execute(q1);
		}
		
		// Create a table for storing the users' permissions.
		if (!ds.tableExists("permissions"))
		{
			Vector<String> vec = new Vector<String>();
			vec.addElement("permissions");
			vec.addElement("user.owner.table");
			vec.addElement("string");
			vec.addElement("insert");
			vec.addElement("boolean");
			vec.addElement("delete");
			vec.addElement("boolean");
			vec.addElement("update");
			vec.addElement("boolean");
			vec.addElement("select");
			vec.addElement("boolean");
			CreateTableQuery q1 = new CreateTableQuery(vec);
			ds.execute(q1);
		}		
	}
	
}
