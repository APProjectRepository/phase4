package orm;

import model.Table;
import model.TableColumn;

public class ORMTableColumn extends TableColumn {

	private String title;
	private boolean isKey;
	
	public ORMTableColumn(Table table, String name, String type) {
		super(table, name, type);
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	
	
	

}
