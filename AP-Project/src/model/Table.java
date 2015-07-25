package model;

import java.util.ArrayList;
import java.util.List;

public class Table
{
	public String name;
	public List<TableColumn> columns;

	public Table(String name)
	{
		columns = new ArrayList<TableColumn>();
		this.name = name;
	}
	
	public void add(TableColumn c)
	{
		columns.add(c);
	}
	
	public int colNum()
	{
		return columns.size(); 
	}
	
	public String colData()
	{
		String str = "";

		for (int i = 0; i < columns.size(); ++i)
		{
			str = str.concat(columns.get(i).name + " ");		
			str = str.concat(columns.get(i).type + " ");
		}
		
		return str;
	}
}
