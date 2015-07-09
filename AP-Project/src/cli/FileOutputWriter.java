package cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import query.result.QueryResult;

public class FileOutputWriter {
	private File outputFile;
	private PrintStream pFile;

	public FileOutputWriter(String outFileName) throws IOException,
			FileNotFoundException {
		outputFile = new File(outFileName);
		pFile = new PrintStream(outputFile);
	}

	public void close() throws IOException {
		pFile.close();
	}

	public void format(QueryResult res) throws IOException {
		pFile.println(res.message);
	}

}
