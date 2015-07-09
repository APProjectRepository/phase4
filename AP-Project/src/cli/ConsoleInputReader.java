package cli;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleInputReader extends AbstractInputReader {
	Scanner cmndScanner;

	public ConsoleInputReader() throws IOException, FileNotFoundException {
		cmndScanner = new Scanner(System.in);
	}

	@Override
	public boolean hasNext() throws IOException {
		return true;
	}

	@Override
	public String next() throws IOException {

		System.out.print("SQL> ");
		return cmndScanner.nextLine();
	}

	@Override
	public void close() throws IOException {
		cmndScanner.close();
	}

}
