package query;

import java.util.Vector;

import model.Table;
import model.TableRow;

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
