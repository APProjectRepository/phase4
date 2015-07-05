package dos.math.sharif.edu.cli;

import java.io.IOException;

import dos.math.sharif.edu.query.result.QueryResult;

public interface OutputWriter {

	void close() throws IOException;

	void format(QueryResult res) throws IOException;

}
