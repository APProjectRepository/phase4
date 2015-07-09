package cli;

import java.io.IOException;

import query.result.QueryResult;

public interface OutputWriter {

	void close() throws IOException;

	void format(QueryResult res) throws IOException;

}
