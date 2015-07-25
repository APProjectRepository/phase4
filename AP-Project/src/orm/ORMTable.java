package orm;

import java.util.List;

import model.Table;

public class ORMTable extends Table {

	private String title;

	public ORMTable(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ORMTableColumn> getColumns() {
		return null;
	}
}
