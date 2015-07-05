package dos.math.sharif.edu.query;

import java.util.Vector;

import dos.math.sharif.edu.model.Table;
import dos.math.sharif.edu.model.TableRow;

public class InsertIntoQuery extends TableDataManipulationQuery
{
	TableRow rowData;

	
	public InsertIntoQuery(String name, Vector<String> vec, int argsNum)
	{
		table = new Table(name);
		rowData = new TableRow(table, vec, argsNum);
	}
	
	public TableRow getRowData()
	{
		return rowData;
	}

	public void setRowData(TableRow rowData)
	{
		this.rowData = rowData;
	}

}
