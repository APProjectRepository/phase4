package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class TableRow
{
	public List<RowColumnValue> columnValues;
	
	public TableRow(Table table, Vector<String> vec, int argsNum)
	{
		columnValues = new ArrayList<RowColumnValue>();
		
		for (int i = 0; i < argsNum; ++i)
		{
			RowColumnValue r = new RowColumnValue(table, vec.elementAt(i), vec.elementAt(i + argsNum));
			columnValues.add(r);
		}
	}

	public int colNum()
	{
		return columnValues.size();
	}
	
	public String getColName(int index)
	{
		RowColumnValue r = columnValues.get(index);
		return r.column.name;		
	}

	public String getColValue(int index)
	{
		RowColumnValue r = columnValues.get(index);
		return r.value;		
	}
}
