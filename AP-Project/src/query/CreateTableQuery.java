package query;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import model.TableColumn;

public class CreateTableQuery extends AlterTableQuery
{
	public Set<TableColumn> columns;

	// Assign the name of the table, and create appropriate columns.
	public CreateTableQuery(Vector<String> vec)
	{
		super(vec.elementAt(0));
		columns = new HashSet<TableColumn>();

		for (int i = 1; i < vec.size(); i = i + 2)
		{	
			TableColumn c = new TableColumn(table, vec.elementAt(i), vec.elementAt(i + 1));
			columns.add(c);
			table.add(c);
		}		
	}
}
