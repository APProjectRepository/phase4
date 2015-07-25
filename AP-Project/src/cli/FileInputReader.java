package cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileInputReader
{
	private File inputFile;
	private Scanner sFile;


	public FileInputReader(String inFileName) throws IOException, FileNotFoundException
	{
		inputFile = new File(inFileName);
		sFile = new Scanner(inputFile);
	}

	public boolean hasNext() throws IOException
	{
		return sFile.hasNext();
	}

	public String next() throws IOException
	{
		return sFile.nextLine();
	}

	public void close() throws IOException
	{
		sFile.close();
	}
}
