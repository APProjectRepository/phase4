package dos.math.sharif.edu.query;

import dos.math.sharif.edu.model.Table;


public abstract class AlterTableQuery extends SingleTableQuery
{
	public AlterTableQuery(String str)
	{
		table = new Table(str);
	}
}
