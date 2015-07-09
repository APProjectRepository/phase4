package model;

public class RowColumnValue {
	public TableColumn column;
	public String value;

	public RowColumnValue(Table table, String colName, String value) {
		this.value = value;
		column = new TableColumn(table, colName, null);
	}

	public String getColName() {
		return column.name;
	}

}
