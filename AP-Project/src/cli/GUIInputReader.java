package cli;


public class GUIInputReader extends AbstractInputReader
{
	private ClientGraphics g;

	public GUIInputReader(ClientGraphics g)
	{
		this.g = g;
	}

	@Override
	public boolean hasNext()
	{
		if (g.disconnectPressed)
		{
			g.disconnectPressed = false;
			return false;
		}
		return true;
	}

	@Override
	public String next()
	{
		String str;
	
		// Check if the user has selected a file.
		if (!g.browseCommands.isEmpty())
		{
			str = g.browseCommands.elementAt(0);
			g.browseCommands.remove(0);
			return str;
		}
		
		// Check if the user has pressed the 'send' button.
		if (g.commandReceived)
		{
			g.commandReceived = false;
			return g.command;
		}
		
		return "";

	}

	@Override
	public void close()
	{
		return;
	}

}
