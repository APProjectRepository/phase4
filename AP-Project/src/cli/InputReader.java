package cli;

import java.io.IOException;

public interface InputReader {
	boolean hasNext() throws IOException;

	String next() throws IOException;

	void close() throws IOException;

}
