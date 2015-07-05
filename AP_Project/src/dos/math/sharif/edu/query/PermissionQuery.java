package dos.math.sharif.edu.query;

import java.util.Vector;

import dos.math.sharif.edu.model.Table;

public class PermissionQuery extends SingleTableQuery
{
	String owner;
	Vector<String> args;

	public PermissionQuery(Vector<String> vec, int argsNum)
	{
		table = new Table("permissions");
		args = new Vector<String>();
		args = vec;		
		// args[0] to args[n - 1] = the column names
		// args[n] to args[2 * n - 1] = the column values
	}

	public Vector<String> getArgs()
	{
		return args;
	}
	
	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getOwner()
	{
		return owner;
	}
}
