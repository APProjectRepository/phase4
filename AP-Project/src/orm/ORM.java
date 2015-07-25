package orm;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ORM {
	private String ORMTableName;
	private ArrayList<Method> setterMethods;
	private ArrayList<Method> getterMethods;
	private Class<?> clazz;

	public ORM(Class<?> c) throws InvalidORClass  {
		clazz = c;
		setterMethods = new ArrayList<>();
		getterMethods = new ArrayList<>();
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new InvalidORClass();
		}
		ORMTableName = clazz.getDeclaredAnnotation(Table.class).name();
		for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
			Method method = clazz.getDeclaredMethods()[i];

			if (method.isAnnotationPresent(Column.class))
				getterMethods.add(method);
			else if (method.isAnnotationPresent(Setter.class))
				setterMethods.add(method);
		}
		
		
		
	}

	public String getORMTableName() {
		return ORMTableName;
	}

	public ArrayList<Method> getSetterMethods() {
		return setterMethods;
	}

	public ArrayList<Method> getGetterMethods() {
		return getterMethods;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Object createObject() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}
	
	

}
