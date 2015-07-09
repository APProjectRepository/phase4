package query;

import query.condition.SimpleCondition;
import model.Table;

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
