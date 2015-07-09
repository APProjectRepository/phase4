package orm;

import java.lang.reflect.Method;
import java.util.Map;

public class ORMHandler {

	private String command;
	private ORM orm;
	private String className;
	private boolean isCraete;
	private String SelectQuery;

	// Syntax for creating object:
	// ORM.create(objectName)
	// syntax for viewing object:
	// ORM.view(objectName)y uo
	public ORMHandler(String com) throws InvalidORClass, Exception {
		command = com;

		if (command.toLowerCase().matches("orm.create(\\w+)")) {
			setCraete(true);
			className = "os.math.sharif.edu.orm"
					+ command.substring(command.indexOf("(") + 1,
							command.indexOf(")"));
			orm = new ORM(Class.forName(className));
			Object obj = orm.createObject();

		} else if (command.toLowerCase().matches("orm.view(\\w+)")) {

			setCraete(false);
			className = "dos.math.sharif.edu.orm."
					+ command.substring(command.indexOf("(") + 1,
							command.indexOf(")"));
			orm = new ORM(Class.forName(className));
			selectQueryBuilder();
			Object obj = orm.createObject();

		} else
			throw new Exception("Invalid command");

	}

	public String getSelectQuery() {
		return SelectQuery;
	}

	public boolean isCraete() {
		return isCraete;
	}

	public void setCraete(boolean isCraete) {
		this.isCraete = isCraete;
	}

	private void selectQueryBuilder() {

		SelectQuery = "SELECT ";
		for (Method m : orm.getGetterMethods()) {
			SelectQuery += m.getAnnotation(Column.class).name() + ",";
		}
		SelectQuery = SelectQuery.substring(0, SelectQuery.length() - 2);
		SelectQuery += " FROM " + orm.getORMTableName();

	}

}
