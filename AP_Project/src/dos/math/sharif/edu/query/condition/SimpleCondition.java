package dos.math.sharif.edu.query.condition;

import dos.math.sharif.edu.model.Table;
import dos.math.sharif.edu.model.TableColumn;

public class SimpleCondition extends Condition
{
	public TableColumn column;
	public String value;
	
	public SimpleCondition(Table table, String[] condStr)
	{
		if (condStr[0] != null)
		{
			column = new TableColumn(table, condStr[0], null);
			value = condStr[1];
		}
	}

	public String getColName()
	{
		return column.name;
	}

}
