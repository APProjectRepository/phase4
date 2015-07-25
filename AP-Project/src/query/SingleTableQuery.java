package query;

import model.Table;

public abstract class SingleTableQuery extends Query
{
	public Table table;

	public Table getTable()
	{
		return table;
	}

	public void setTable(Table table)
	{
		this.table = table;
	}
	
	public void setUser(String username)
	{
		table.name = username.substring(1, username.length() - 1).concat(".").concat(table.name);
	}
}
