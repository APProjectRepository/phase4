package dos.math.sharif.edu.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import dos.math.sharif.edu.model.RowColumnValue;
import dos.math.sharif.edu.model.Table;
import dos.math.sharif.edu.query.condition.SimpleCondition;

public class UpdateTableDataQuery extends TableDataManipulationQuery
{
	SimpleCondition condition;
	List<RowColumnValue> newValues;
	
	
	public UpdateTableDataQuery(String name, Vector<String> setVec, String[] condStr)
	{
		table = new Table(name);
		condition = new SimpleCondition(table, condStr);
		newValues = new ArrayList<RowColumnValue>();
		for (int i = 0; i < setVec.size(); i = i + 2)
		{
			RowColumnValue r = new RowColumnValue(table, setVec.elementAt(i), setVec.elementAt(i + 1));
			newValues.add(r);
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

	public List<RowColumnValue> getNewValues()
	{
		return newValues;
	}

	public void setNewValues(List<RowColumnValue> newValues)
	{
		this.newValues = newValues;
	}

}
