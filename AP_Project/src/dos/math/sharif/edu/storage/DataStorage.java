package dos.math.sharif.edu.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import dos.math.sharif.edu.model.RowColumnValue;
import dos.math.sharif.edu.model.Table;
import dos.math.sharif.edu.model.TableColumn;
import dos.math.sharif.edu.model.TableRow;
import dos.math.sharif.edu.query.CreateTableQuery;
import dos.math.sharif.edu.query.DeleteFromTableQuery;
import dos.math.sharif.edu.query.DropTableQuery;
import dos.math.sharif.edu.query.InsertIntoQuery;
import dos.math.sharif.edu.query.PermissionQuery;
import dos.math.sharif.edu.query.Query;
import dos.math.sharif.edu.query.SelectTableDataQuery;
import dos.math.sharif.edu.query.SingleTableQuery;
import dos.math.sharif.edu.query.UpdateTableDataQuery;
import dos.math.sharif.edu.query.condition.SimpleCondition;
import dos.math.sharif.edu.query.result.QueryResult;

public class DataStorage
{
	private String rootDir;
	private File file;
	
	public DataStorage(String dsPath) throws StorageException
	{
		rootDir = dsPath;
		file = new File(rootDir);
		if (!file.exists())
			throw new StorageException();
	}

	public DataStorage(String dsPath, String user) throws StorageException
	{
		return;
	}

	// Disconnect from database
	public void disconnect() throws StorageException
	{
		return;
	}

	// Execute the commands.
	public QueryResult execute(Query q) throws QueryException
	{
		// Based on the type of the query, run a proper method of this class.
		if (q instanceof SingleTableQuery) 
		{
			Table table = ((SingleTableQuery)q).getTable();
			if (q instanceof CreateTableQuery)
			{
				return createTable(table);
			}
			if (q instanceof DropTableQuery)
			{
				return dropTable(table);
			}
			if (q instanceof InsertIntoQuery)
			{
				return insertIntoTable(table, ((InsertIntoQuery)q).getRowData());
			}
			if (q instanceof DeleteFromTableQuery)
			{
				return deleteFromTable(table, ((DeleteFromTableQuery)q).getCondition(), ((DeleteFromTableQuery)q).cascade);
			}
			if (q instanceof UpdateTableDataQuery)
			{
				return updateTableData(table, ((UpdateTableDataQuery)q).getCondition(), ((UpdateTableDataQuery)q).getNewValues());
			}
			if (q instanceof SelectTableDataQuery)
			{
				return loadTableData(table, ((SelectTableDataQuery)q).getCondition(), ((SelectTableDataQuery)q).getColumns());
			}
			if (q instanceof PermissionQuery)
			{
				return setPermissionTable(table, ((PermissionQuery)q).getArgs(), ((PermissionQuery)q).getOwner());				
			}
		}
		return new QueryResult(false, "unsupported command!", "", 0);
	}

	// GRANT or REVOKE
	private QueryResult setPermissionTable(Table table, Vector<String> args, String owner) throws QueryException
	{
		String tableName, userOwnerTable, message;
		int argsNum = args.size() / 2;
		String[] condStr = new String[2];
		Vector<String> vec1 = new Vector<String>();
		Vector<String> vec2 = new Vector<String>();

		// Check if the user exists.
		if (!userExists(args.elementAt(argsNum)))
			throw new QueryException(6, args.elementAt(argsNum));
		
		owner = owner.substring(1, owner.length() - 1);

		userOwnerTable = args.elementAt(argsNum);
		userOwnerTable = userOwnerTable.substring(1, userOwnerTable.length() - 1);
		tableName = args.elementAt(argsNum + 1);
		tableName = tableName.substring(1, tableName.length() - 1);
		tableName = owner.concat(".").concat(tableName);
		userOwnerTable = userOwnerTable.concat(".").concat(tableName);
		userOwnerTable = "\"".concat(userOwnerTable).concat("\"");
		
		if (!tableExists(tableName))
			throw new QueryException(12, "\"".concat(tableName).concat("\""));

		condStr[0] = "user.owner.table";
		condStr[1] = userOwnerTable;
		
		for (int i = 2; i < argsNum; ++i)
		{
			vec1.addElement(args.elementAt(i));
			vec1.addElement(args.elementAt(i + argsNum));
		}
		
		// Update the 'permissions' if the appropriate row exists.
		UpdateTableDataQuery q1 = new UpdateTableDataQuery("permissions", vec1, condStr); 
		QueryResult res1 = this.execute(q1);

		// Insert a row into the 'permissions' if the appropriate row does not exist.
		if (res1.message.contains("0"))
		{
			vec2.addElement("user.owner.table");
			for (int i = 2; i < argsNum; ++i)
				vec2.addElement(args.elementAt(i));
			vec2.addElement(userOwnerTable);
			for (int i = argsNum + 2; i < 2 * argsNum; ++i)
				vec2.addElement(args.elementAt(i));
			InsertIntoQuery q2 = new InsertIntoQuery("permissions", vec2, argsNum - 1); 
			this.execute(q2);
		}
		
		message = "";
		for (int i = 2; i < argsNum; ++i)
		{
			message = message.concat(args.elementAt(i).toUpperCase()).concat(" ");
			if (i != argsNum - 1)
				message = message.concat("and ");
		}
		message = message.concat("ON " + args.elementAt(argsNum + 1).substring(1, args.elementAt(argsNum + 1).length() - 1));
		if (args.elementAt(argsNum + 2).equals("true"))
			message = message.concat(" granted to ");
		else
			message = message.concat(" revoked from ");
		message = message.concat(args.elementAt(argsNum).substring(1, args.elementAt(argsNum).length() - 1));
		message = message.concat(" successfully!");

		return new QueryResult(false, message, "", 0);
	}

	// SELECT
	synchronized private QueryResult loadTableData(Table table, SimpleCondition condition,
			List<TableColumn> columns) throws QueryException
	{
		if (!tableExists(table.name))
			throw new QueryException(2, table.name);

		File f = new File(rootDir + table.name);
		Scanner s;
		String str, outStr = "";
		String[] rowStr, tempStr;
		int index1 = 0, rowNum, colNum, selectedNum = 0, k;
		Vector<Integer> index2 = new Vector<Integer>();

		try
		{
			s = new Scanner(f);

			// Get the number of rows.
			str = s.next();
			rowNum = Integer.valueOf(str);

			// Get the column names and values.
			Vector<String> vec = getColumns(s);
			colNum = vec.size() / 2;
			
			// Check the column names.
			if (columns.size() != 0)
				for (int i = 0; i < columns.size(); ++i)
				{
					str = columns.get(i).name;
					index1 = findColIndex(str, vec);
					if (index1 == -1)
						throw new QueryException(3, str);
					index1 = index1 / 2;
					index2.add(index1);
				}
			else
				for (int i = 0; i < colNum; ++i)
					index2.add(i);

			if (condition.value != null)
			{
				// Check the column name of the condition.
				str = condition.getColName();
				index1 = findColIndex(str, vec);
				if (index1 == -1)
					throw new QueryException(3, str);
				index1 = index1 / 2;
	
				// Check the column value of the condition.
				str = condition.value;
				if (!validValue(str, vec.elementAt(2 * index1 + 1)))
					throw new QueryException(4, condition.getColName());
			}
			
			// Get the data of the rows.
			while (s.hasNext())
			{
				outStr = outStr.concat(s.nextLine());
				outStr = outStr.concat("\n");
			}
			while (outStr.length() != 0)
			{
				if (outStr.charAt(0) == ' ' || outStr.charAt(0) == '\n')
					outStr = outStr.substring(1, outStr.length());
				else
					break;
			}
			
			rowStr = outStr.split("\n");
			
			outStr = "";
			// Write the selected column names in the outStr.
			for (int i = 0; i < index2.size(); ++i)
			{
				outStr = outStr.concat(vec.elementAt(2 * index2.elementAt(i)));
				if (i != index2.size() - 1)
					outStr = outStr.concat(",");
			}
			k = outStr.length();
			outStr = outStr.concat("\n");
			for (int i = 0; i < k; ++i)
				outStr = outStr.concat("-");
			outStr = outStr.concat("\n");

			// Select the needed rows.
			for (int i = 0; i < rowNum; ++i)
			{
				tempStr = rowStr[i].split(",");
				if (condition.value == null || tempStr[index1].equals(condition.value))
				{
					for (int j = 0; j < index2.size(); ++j)
					{
						outStr = outStr.concat(tempStr[index2.elementAt(j)]);
						if (j != index2.size() - 1)
							outStr = outStr.concat(",");
					}
					outStr = outStr.concat("\n");
					++selectedNum;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		outStr = outStr.concat(selectedNum + " row(s) selected");
		return new QueryResult(true, outStr, "", 0);
	}

	// UPDATE
	synchronized private QueryResult updateTableData(Table table, SimpleCondition condition,
			List<RowColumnValue> newValues) throws QueryException
	{
		if (!tableExists(table.name))
			throw new QueryException(2, table.name);

		File f = new File(rootDir + table.name);
		Scanner s;
		PrintStream p;
		String str, outStr = "";
		String[] rowStr, tempStr;
		int index1 = 0, rowNum, colNum, updatedNum = 0, resID = 0;
		Vector<Integer> index2 = new Vector<Integer>();

		try
		{
			s = new Scanner(f);

			// Get the number of rows.
			str = s.next();
			rowNum = Integer.valueOf(str);

			// Get the column names and values.
			Vector<String> vec = getColumns(s);
			colNum = vec.size() / 2;
			
			// Check the new values.
			for (int i = 0; i < newValues.size(); ++i)
			{
				// Check the column name of the new value.
				str = newValues.get(i).getColName();
				index1 = findColIndex(str, vec);
				if (index1 == -1)
					throw new QueryException(3, str);
				index1 = index1 / 2;
				index2.add(index1);
	
				// Check the column value of the new value.
				str = newValues.get(i).value;
				if (!validValue(str, vec.elementAt(2 * index1 + 1)))
					throw new QueryException(4, condition.getColName());				
			}

			if (condition.value != null)
			{
				// Check the column name of the condition.
				str = condition.getColName();
				index1 = findColIndex(str, vec);
				if (index1 == -1)
					throw new QueryException(3, str);
				index1 = index1 / 2;
	
				// Check the column value of the condition.
				str = condition.value;
				if (!validValue(str, vec.elementAt(2 * index1 + 1)))
					throw new QueryException(4, condition.getColName());
			}
			
			// Get the data of the rows.
			while (s.hasNext())
			{
				outStr = outStr.concat(s.nextLine());
				outStr = outStr.concat("\n");
			}
			while (outStr.length() != 0)
			{
				if (outStr.charAt(0) == ' ' || outStr.charAt(0) == '\n')
					outStr = outStr.substring(1, outStr.length());
				else
					break;
			}
			
			if (table.name.equalsIgnoreCase("users"))
			{
				if (!userExists(outStr, condition.value))
					throw new QueryException(6, condition.value);
			}
			
			rowStr = outStr.split("\n");
						
			// Update the rows if needed.
			for (int i = 0; i < rowNum; ++i)
			{
				tempStr = rowStr[i].split(",");
				if (condition.value == null || tempStr[index1].equals(condition.value))
				{
					if (table.name.equalsIgnoreCase("users"))
					{
						if (newValues.get(0).getColName().equals("connect"))
						{
							if (newValues.get(0).value.equals("true"))
							{
								if (tempStr[2].equals("true"))
									throw new QueryException(7, condition.value);
								resID = 2;
							}
							else
							{
								if (tempStr[2].equals("false"))
									throw new QueryException(8, condition.value);
								resID = 3;
							}
						}
						else if (newValues.get(0).getColName().equals("DBA"))
						{
							if (newValues.get(0).value.equals("true"))
							{
								if (tempStr[3].equals("true"))
									throw new QueryException(9, condition.value);
								resID = 4;
							}
							else
							{
								if (tempStr[3].equals("false"))
									throw new QueryException(10, condition.value);
								resID = 5;
							}
						}
						else if (newValues.get(0).getColName().equals("password"))
							resID = 6;
					}
					for (int j = 0; j < index2.size(); ++j)
						tempStr[index2.elementAt(j)] = newValues.get(j).value;
					rowStr[i] = "";
					for (int j = 0; j < tempStr.length; ++j)
					{
						rowStr[i] = rowStr[i].concat(tempStr[j]);
						if (j != tempStr.length - 1)
							rowStr[i] = rowStr[i].concat(",");
					}
					++updatedNum;
				}
			}

			outStr = "";
			for (int i = 0; i < rowNum; ++i)
			{
				outStr = outStr.concat(rowStr[i]);
				outStr = outStr.concat("\n");
			}

			try
			{
				p = new PrintStream(f);
				writeToDataBase(p, rowNum, colNum, vec, outStr, "");
				p.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return new QueryResult(true, updatedNum + " row(s) updated", condition.value, resID);
	}

	// DELETE
	synchronized private QueryResult deleteFromTable(Table table, SimpleCondition condition, boolean cascade) throws QueryException
	{
		if (!tableExists(table.name))
			throw new QueryException(2, table.name);

		File f = new File(rootDir + table.name);
		Scanner s;
		PrintStream p;
		String str, outStr = "";
		String[] rowStr, tempStr;
		int index, rowNum, colNum, deletedNum = 0;

		try
		{
			s = new Scanner(f);

			// Get the number of rows.
			str = s.next();
			rowNum = Integer.valueOf(str);

			// Get the column names and values.
			Vector<String> vec = getColumns(s);
			colNum = vec.size() / 2;

			if (condition.value == null)
			{
				outStr = "";
				deletedNum = rowNum;
			}
			else
			{
				// Check the column name.
				str = condition.getColName();
				index = findColIndex(str, vec);
				if (index == -1)
					throw new QueryException(3, str);
				index = index / 2;
	
				// Check the column value.
				str = condition.value;
				if (!validValue(str, vec.elementAt(2 * index + 1)))
					throw new QueryException(4, condition.getColName());
				
				// Get the data of the rows.
				while (s.hasNext())
				{
					outStr = outStr.concat(s.nextLine());
					outStr = outStr.concat("\n");
				}
				while (outStr.length() != 0)
				{
					if (outStr.charAt(0) == ' ' || outStr.charAt(0) == '\n')
						outStr = outStr.substring(1, outStr.length());
					else
						break;
				}
				
				if (table.name.equalsIgnoreCase("users"))
				{
					if (!userExists(outStr, condition.value))
						throw new QueryException(6, condition.value);
				}

				rowStr = outStr.split("\n");
				
				for (int i = 0; i < rowNum; ++i)
				{
					if (table.name.equalsIgnoreCase("users"))
					{
						if (!cascade && userHasTable(condition.value))					
							throw new QueryException(11, condition.value);
						deleteUserTables(condition.value);
						deleteUserPermissions(condition.value);
					}
					tempStr = rowStr[i].split(",");
					if (tempStr[index].equals(condition.value))
						rowStr[i] = null;
				}
				
				outStr = "";
				for (int i = 0; i < rowNum; ++i)
					if (rowStr[i] != null)
					{
						outStr = outStr.concat(rowStr[i]);
						outStr = outStr.concat("\n");
					}
					else
						++deletedNum;
			}

			try
			{
				p = new PrintStream(f);
				rowNum -= deletedNum;
				writeToDataBase(p, rowNum, colNum, vec, outStr, "");
				p.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return new QueryResult(true, deletedNum + " row(s) deleted", condition.value, 7);
	}

	// INSERT
	synchronized private QueryResult insertIntoTable(Table table, TableRow rowData) throws QueryException
	{
		if (!tableExists(table.name))
			throw new QueryException(2, table.name);
		
		File f = new File(rootDir + table.name);
		Scanner s;
		String str, outStr = "", otherStr = "";
		int index, colNum, j, rowNum, resID = 0;
		int[] cIndex = new int[rowData.colNum()];
		PrintStream p;
		
		try
		{
			s = new Scanner(f);

			// Get the number of rows.
			str = s.next();
			rowNum = Integer.valueOf(str);

			// Get the column names and values.
			Vector<String> vec = getColumns(s);
			colNum = vec.size() / 2;

			// Get the data of the rows.
			while (s.hasNext())
			{
				otherStr = otherStr.concat(s.nextLine());
				otherStr = otherStr.concat("\n");
			}
			while (otherStr.length() != 0)
			{
				if (otherStr.charAt(0) == ' ' || otherStr.charAt(0) == '\n')
					otherStr = otherStr.substring(1, otherStr.length());
				else
					break;
			}
			
			boolean[] hasValue = new boolean[colNum];

			// Check the column names.			
			for (int i = 0; i < rowData.colNum(); ++i)
			{
				str = rowData.getColName(i); 
				index = findColIndex(str, vec);
				if (index == -1)
					throw new QueryException(3, str);
				cIndex[i] = index / 2;
				hasValue[index / 2] = true;
			}

			// Check the column values.
			for (int i = 0; i < rowData.colNum(); ++i)
			{
				str = rowData.getColValue(i);
				if (!validValue(str, vec.elementAt(2 * cIndex[i] + 1)))
					throw new QueryException(4, rowData.getColName(i));
			}
			

			if (table.name.equalsIgnoreCase("users"))
			{
				if (userExists(otherStr, rowData.getColValue(0)))
					throw new QueryException(5, rowData.getColValue(0));
				resID = 1;
			}

			for (int i = 0; i < colNum; ++i)
			{
				if (hasValue[i])
				{
					for (j = 0; j < rowData.colNum(); ++j)
						if (cIndex[j] == i)
							break;
					outStr = outStr.concat(rowData.getColValue(j));
				}
				else
					outStr = outStr.concat("null");
				if (i != colNum - 1)
					outStr = outStr.concat(",");
			}
			
			try
			{
				p = new PrintStream(f);
				++rowNum;
				writeToDataBase(p, rowNum, colNum, vec, outStr, otherStr);
				p.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}


		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return new QueryResult(true, "One row inserted into " + table.name, rowData.getColValue(0), resID);
	}

	// DROP
	synchronized private QueryResult dropTable(Table table) throws QueryException
	{
		if (!tableExists(table.name))
			throw new QueryException(2, table.name);
		
		File f = new File(rootDir + table.name);
		f.delete();
		
		return new QueryResult(true, "Table " + table.name + " dropped", "", 0);
	}

	// CREATE
	synchronized private QueryResult createTable(Table table) throws QueryException
	{
		if (tableExists(table.name))
			throw new QueryException(1, table.name);
		
		File f = new File(rootDir + table.name);
		try 
		{
			PrintStream p = new PrintStream(f);
			p.println( 0 + " " + table.colNum());
			p.print(table.colData());
			p.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return new QueryResult(true, "Table " + table.name + " created", "", 0);
	}
	
	// Check if the table with the given name exists in database.
	synchronized public boolean tableExists(String name)
	{
		String[] paths;
		paths = file.list();

		for (int i = 0; i < paths.length; ++i)
			if (paths[i].equalsIgnoreCase(name))
				return true;
		return false;		
	}

	// Get the names and types of the columns of the table.
	private Vector<String> getColumns(Scanner s)
	{
		String str;
		int colNum;
		Vector<String> vec = new Vector<String>();

		// Get the number of columns.	
		str = s.next();	
		colNum = Integer.valueOf(str);

		// Get the column names and types.
		for (int i = 0; i < 2 * colNum; ++i)
		{
			str = s.next();
			vec.addElement(str);
		}
		
		return vec;

	}

	// Find the index of the column in the table.
	private int findColIndex(String name, Vector<String> vec)
	{
		for (int i = 0; i < vec.size(); i = i + 2)
			if (name.equalsIgnoreCase(vec.elementAt(i)))
				return i;
		return -1;
	}
	
	// Check if the user with the given name exists in database.
	private boolean userExists(String str, String name)
	{
		String[] s1, s2;
		
		s1 = str.split("\n");
		
		for (int i = 0; i < s1.length; ++i)
		{
			s2 = s1[i].split(",");
				if (s2[0].equalsIgnoreCase(name))
					return true;
		}
		
		return false;
	}

	// Check if the user with the given name exists in database.
	synchronized private boolean userExists(String name)
	{
		File f = new File(rootDir + "users");
		Scanner s;
		String str;
		
		try
		{
			s = new Scanner(f);
			// Get the number of rows.
			str = s.next();

			// Get the column names and values.
			getColumns(s);

			// Get the data of the rows.
			str = "";
			while (s.hasNext())
			{
				str = str.concat(s.nextLine());
				str = str.concat("\n");
			}
			while (str.length() != 0)
			{
				if (str.charAt(0) == ' ' || str.charAt(0) == '\n')
					str = str.substring(1, str.length());
				else
					break;
			}
			return userExists(str, name);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}			
		return false;
	}

	// Get the information of the user with the given name.
	synchronized public String[] userInfo(String name)
	{
		File f = new File(rootDir + "users");
		Scanner s;
		String str = "";
		String[] s1, s2;
		try
		{
			s = new Scanner(f);

			// Get the number of rows.
			s.next();
			// Get the column names and values.
			getColumns(s);

			// Get the data of the rows.
			while (s.hasNext())
			{
				str = str.concat(s.nextLine());
				str = str.concat("\n");
			}
			while (str.length() != 0)
			{
				if (str.charAt(0) == ' ' || str.charAt(0) == '\n')
					str = str.substring(1, str.length());
				else
					break;
			}
			
			s1 = str.split("\n");
			
			for (int i = 0; i < s1.length; ++i)
			{
				s2 = s1[i].split(",");
					if (s2[0].equalsIgnoreCase(name))
						return s2;
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		String[] temp = {""};
		return temp;
	}
	
	// Check if the user is permitted to manipulate other users' tables.
	public boolean userPermitted(String username, SingleTableQuery q1) throws QueryException
	{
		Vector<String> vec = new Vector<String>();

		if (q1 instanceof InsertIntoQuery)
			vec.addElement("insert");
		else if (q1 instanceof DeleteFromTableQuery)
			vec.addElement("delete");
		else if (q1 instanceof UpdateTableDataQuery)
			vec.addElement("update");
		else if (q1 instanceof SelectTableDataQuery)
			vec.addElement("select");
		else
			return false;

		String userOwnerTable = q1.table.name;
		username = username.substring(1, username.length() - 1);
		userOwnerTable = username.concat(".").concat(userOwnerTable);
		userOwnerTable = "\"".concat(userOwnerTable).concat("\"");
		
		String[] condStr = new String[2];
		condStr[0] = "user.owner.table";
		condStr[1] = userOwnerTable;
		SelectTableDataQuery q2 = new SelectTableDataQuery("permissions", vec, condStr);
		QueryResult res = this.execute(q2);
		
		if (res.message.contains("true"))
			return true;
		return false;
	}
	
	// Check if the user with the given name has any table.
	synchronized private boolean userHasTable(String name)
	{
		String[] paths;
		paths = file.list();
		name = name.substring(1, name.length() - 1).concat(".");

		for (int i = 0; i < paths.length; ++i)
			if (paths[i].contains(name))
				return true;
		return false;	
	}

	// Delete all the tables of the user with the given name.
	synchronized private void deleteUserTables(String name)
	{
		String[] paths;
		paths = file.list();
		name = name.substring(1, name.length() - 1).concat(".");

		for (int i = 0; i < paths.length; ++i)
			if (paths[i].contains(name))
			{
				File f = new File(rootDir + paths[i]);
				f.delete();
			}		
	}

	// Delete all the permissions related to the user with the given name.
	synchronized private void deleteUserPermissions(String name)
	{
		File f = new File(rootDir + "permissions");
		Scanner s;
		PrintStream p;
		String str, otherStr = "", outStr = "";
		String[] tempStr;
		int rowNum, colNum;
		
		name = name.substring(1, name.length() - 1);
		
		try
		{
			s = new Scanner(f);

			// Get the number of rows.
			str = s.next();
			rowNum = Integer.valueOf(str);

			// Get the column names and values.
			Vector<String> vec = getColumns(s);
			colNum = vec.size() / 2;

			// Get the data of the rows.
			while (s.hasNext())
			{
				otherStr = otherStr.concat(s.nextLine());
				otherStr = otherStr.concat("\n");
			}
			while (otherStr.length() != 0)
			{
				if (otherStr.charAt(0) == ' ' || otherStr.charAt(0) == '\n')
					otherStr = otherStr.substring(1, otherStr.length());
				else
					break;
			}
			
			tempStr = otherStr.split("\n");
			for (int i = 0; i < tempStr.length; ++i)
			{
				if (!tempStr[i].contains(name))
					outStr = outStr + tempStr[i] + '\n';
				else
					--rowNum;
			}
			
			if (!outStr.isEmpty())
				outStr = outStr.substring(0, outStr.length() - 1);
			
			p = new PrintStream(f);
			p.print(rowNum + " " + colNum + "\n");
			for (int i = 0; i < vec.size(); ++i)
				p.print(vec.elementAt(i) + " ");
			if (!outStr.isEmpty())
				p.print("\n" + outStr);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		
	}

	// Check if the input string is a valid variable according to its type.
	private boolean validValue(String value, String type)
	{
		if (type.equalsIgnoreCase("integer"))
		{
			if (value.equalsIgnoreCase("null"))
				return true;
			if (validInt(value))
				return true;
			return false;
		}
		if (type.equalsIgnoreCase("decimal"))
		{
			if (value.equalsIgnoreCase("null"))
				return true;
			if (validDec(value))
				return true;
			return false;
		}
		if (type.equalsIgnoreCase("boolean"))
		{
			if (value.equalsIgnoreCase("true"))
				return true;
			if (value.equalsIgnoreCase("false"))
				return true;
			if (value.equalsIgnoreCase("null"))
				return true;
			return false;
		}
		if (type.equalsIgnoreCase("string"))
		{
			if (value.equalsIgnoreCase("null"))
				return true;
			if ((value.charAt(0) == '"') && (value.charAt(value.length() - 1) == '"'))
				return true;
			return false;
		}
		return false;
	}

	// Check if the input string is a valid integer number.
	private boolean validInt(String str)
	{
		char c;
		boolean flag = true;
		

		c = str.charAt(0);
		if (!(((c >= '0') && (c <= '9')) || (c == '-') || (c == '+')))
			return false;
		
		for (int i = 1; i < str.length(); ++i)
		{
			c = str.charAt(i);

			if ((c >= '0') && (c <= '9'))
				continue;

			flag = false;
			break;
		}
		
		return flag;
	}

	// Check if the input string is a valid decimal number.
	private boolean validDec(String str)
	{
		char c;
		boolean flag = true;
		boolean dotFlag = false;

		c = str.charAt(0);
		if (!(((c >= '0') && (c <= '9')) || (c == '-') || (c == '+')))
			return false;

		for (int i = 1; i < str.length(); ++i)
		{
			c = str.charAt(i);
			if (c == '.')
			{
				if (dotFlag == false)
				{
					dotFlag = true;
					continue;
				}
				else
				{
					flag = false;
					break;
				}
			}

			if ((c >= '0') && (c <= '9'))
				continue;

			flag = false;
			break;
		}
		
		return flag;
	}

	// Write the data of the table into its file.
	private void writeToDataBase(PrintStream p, int rowNum, int colNum, Vector<String> vec, String outStr, String otherStr)
	{
		p.println(rowNum + " " + colNum);
		
		for (int i = 0; i < vec.size(); ++i)
			p.print(vec.elementAt(i) + " ");
		
		while (otherStr.length() != 0)
		{
			if (otherStr.charAt(0) == ' ' || otherStr.charAt(0) == '\n')
				otherStr = otherStr.substring(1, otherStr.length());
			else
				break;
		}		
		
		while (outStr.length() != 0)
		{
			if (outStr.charAt(outStr.length() - 1) == ' ' || outStr.charAt(outStr.length() - 1) == '\n')
				outStr = outStr.substring(0, outStr.length() - 1);
			else
				break;
		}
		
		if (otherStr.length() != 0 || outStr.length() != 0)
			p.println();
		p.print(otherStr);
		p.print(outStr);
	}
}
