package dos.math.sharif.edu.cli;

import dos.math.sharif.edu.parser.QueryParser;
import dos.math.sharif.edu.query.Query;
import dos.math.sharif.edu.query.result.QueryResult;
import dos.math.sharif.edu.storage.DataStorage;

public class FileUserInterface
{
	public static void main(String[] args)
	{
		try
		{
			String dsPath = "database/";			// the proper location of the physical storage place(folder/files)
			DataStorage ds = new DataStorage(dsPath);

			QueryParser parser = new QueryParser();

			// Read the input and output file names from the input 'args'.
			String inFileName = args[0];			// input file name
			String outFileName = args[1];			// output file name
			FileInputReader input = new FileInputReader(inFileName);
			FileOutputWriter output = new FileOutputWriter(outFileName);

			// for each input command 
			while (input.hasNext())
			{
				try
				{
					String command = input.next();	// next command
					Query q = parser.parse(command);
					QueryResult res = ds.execute(q);
					output.format(res);				// Write the query result into the output file.
				}
				catch (Exception e)
				{
					output.format(new QueryResult(false, e.getMessage(), "", 0));
				}
			}

			input.close();
			output.close();
			ds.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
