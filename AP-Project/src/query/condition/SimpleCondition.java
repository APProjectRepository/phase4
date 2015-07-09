package query.condition;

import model.Table;
import model.TableColumn;

public class SimpleCondition extends Condition {
	public TableColumn column;
	public String value;

	public SimpleCondition(Table table, String[] condStr) {
		if (condStr[0] != null) {
			column = new TableColumn(table, condStr[0], null);
			value = condStr[1];
		}
	}

	public String getColName() {
		return column.name;
	}

}
