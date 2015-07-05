package dos.math.sharif.edu.query;

import dos.math.sharif.edu.model.Table;
import dos.math.sharif.edu.query.condition.SimpleCondition;

public class DeleteFromTableQuery extends TableDataManipulationQuery
{
	public SimpleCondition condition;
	public boolean cascade;

	public DeleteFromTableQuery(String name, String[] condStr, boolean cascade)
	{
		table = new Table(name);
		condition = new SimpleCondition(table, condStr);
		this.cascade = cascade;
	}
	
	public SimpleCondition getCondition()
	{
		return condition;
	}

	public void setCondition(SimpleCondition condition)
	{
		this.condition = condition;
	}

}
