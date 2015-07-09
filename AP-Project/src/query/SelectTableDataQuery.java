package query;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import query.condition.SimpleCondition;
import model.Table;
import model.TableColumn;

public class SelectTableDataQuery extends TableDataQuery
{
	SimpleCondition condition;
	List<TableColumn> columns;
	
	public SelectTableDataQuery(String name, Vector<String> colVec, String[] condStr)
	{
		table = new Table(name);
		condition = new SimpleCondition(table, condStr);
		columns = new ArrayList<TableColumn>();
		for (int i = 0; i < colVec.size(); ++i)
		{
			TableColumn c = new TableColumn(table, colVec.elementAt(i), null);
			columns.add(c);			
		}
	}

	public SimpleCondition getCondition()
	{
		return condition;
	}
	
	public void setCondition(SimpleCondition condition)
	{
		this.condition = condition;
	}
	
	public List<TableColumn> getColumns()
	{
		return columns;
	}
	
	public void setColumns(List<TableColumn> columns)
	{
		this.columns = columns;
	}

}
