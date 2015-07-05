package dos.math.sharif.edu.cli;

import java.io.FileNotFoundException;
import java.io.IOException;

import dos.math.sharif.edu.query.result.QueryResult;

public class ConsoleOutputWriter extends AbstractOutputWriter
{
	public ConsoleOutputWriter() throws IOException, FileNotFoundException
	{
	}

	@Override
	public void close() throws IOException
	{
		return;
	}

	@Override
	public void format(QueryResult res) throws IOException
	{
		int ID = res.ID;
		String name = res.temp;
		if (name.length() >= 2)
			name = name.substring(1, name.length() - 1);

		if (ID == 0)
			System.out.println(res.message);
		else if (ID == 1)
			System.out.println("User " + name + " created succesfully!");
		else if (ID == 2)
			System.out.println("Connection granted to " + name + " successfully!");
		else if (ID == 3)
			System.out.println("Connection revoked from " + name + " successfully!");
		else if (ID == 4)
			System.out.println("DBA granted to " + name + " successfully!");
		else if (ID == 5)
			System.out.println("DBA revoked from " + name + " successfully!");
		else if (ID == 6)
			System.out.println("Password changed for user " + name + " successfully!");
		else if (ID == 7)
			System.out.println("User " + name + " dropped succesfully!");
	}

}
