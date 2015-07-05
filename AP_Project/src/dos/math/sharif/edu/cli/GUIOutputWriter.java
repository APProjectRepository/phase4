package dos.math.sharif.edu.cli;

import dos.math.sharif.edu.query.result.QueryResult;

public class GUIOutputWriter extends AbstractOutputWriter
{
	private ClientGraphics g;

	public GUIOutputWriter(ClientGraphics g)
	{
		this.g = g;
	}

	@Override
	public void close()
	{
		return;
	}

	@Override
	public void format(QueryResult res)
	{
		g.taResult.append(res.message + "\n");
	}

}
