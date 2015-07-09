package model;

public class TableColumn {
	public Table table;
	public String name;
	public String type;

	public TableColumn(Table table, String name, String type) {
		this.table = table;
		this.name = name;
		this.type = type;
	}
}
