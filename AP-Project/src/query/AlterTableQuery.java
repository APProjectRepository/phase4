package query;

import model.Table;


public abstract class AlterTableQuery extends SingleTableQuery
{
	public AlterTableQuery(String str)
	{
		table = new Table(str);
	}
}
