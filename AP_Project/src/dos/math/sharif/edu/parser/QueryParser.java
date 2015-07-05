package dos.math.sharif.edu.parser;

import java.util.Vector;

import dos.math.sharif.edu.query.CreateTableQuery;
import dos.math.sharif.edu.query.DeleteFromTableQuery;
import dos.math.sharif.edu.query.DropTableQuery;
import dos.math.sharif.edu.query.InsertIntoQuery;
import dos.math.sharif.edu.query.PermissionQuery;
import dos.math.sharif.edu.query.Query;
import dos.math.sharif.edu.query.SelectTableDataQuery;
import dos.math.sharif.edu.query.UpdateTableDataQuery;

public class QueryParser
{
	public Query parse(String command) throws QueryParserException
	{
		String str1, str2;

		str2 = firstWord(command);
		str1 = deleteFirstWord(command);

		// CREATE command
		if (str2.equalsIgnoreCase("CREATE"))
			return parseCREATE(str1);

		// DROP command
		else if (str2.equalsIgnoreCase("DROP"))
			return parseDROP(str1);

		// INSERT command
		else if (str2.equalsIgnoreCase("INSERT"))
			return parseINSERT(str1);

		// DELETE command
		else if (str2.equalsIgnoreCase("DELETE"))
			return parseDELETE(str1);

		// UPDATE command
		else if (str2.equalsIgnoreCase("UPDATE"))
			return parseUPDATE(str1);

		// SELECT command
		else if (str2.equalsIgnoreCase("SELECT"))
			return parseSELECT(str1);

		// GRANT command
		else if (str2.equalsIgnoreCase("GRANT"))
			return parseGRANT_REVOKE(str1, true);

		// REVOKE command
		else if (str2.equalsIgnoreCase("REVOKE"))
			return parseGRANT_REVOKE(str1, false);

		else
			throw new QueryParserException(1, "");
	}
		
	// Parse the 'CREATE' command.
 	private CreateTableQuery parseCREATE(String str) throws QueryParserException
	{
		Vector<String> vec = new Vector<String>();
		// At the end:
		// vec[0] = the table name
		// vec[2 * i + 1] = the column names
		// vec[2 * i + 2] = the column types

		int[] index = new int[3];
		String s;

		// Find the keyword 'TABLE'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		if (!s.equalsIgnoreCase("TABLE"))
			throw new QueryParserException(1, "");

		// Find the table name and put it at vec[0].
		index[0] = 0;		
		if (!findChar('(', str, index))
			throw new QueryParserException(1, "");
		
		s = str.substring(index[0], index[1]);
		if (!validName(s))
			throw new QueryParserException(1, "");
		vec.addElement(s);
		
		// Find the command parameters and put them at vec.
		while (true)
		{
			index[0] = index[2] + 1;
			if(!findChar(':', str, index))
				throw new QueryParserException(1, "");

			s = str.substring(index[0], index[1]);
			if (!validName(s))
				throw new QueryParserException(1, "");
			vec.addElement(s);

			index[0] = index[2] + 1;
			if (!findChar(',', str, index))
			{
				if (!findChar(')', str, index))
					throw new QueryParserException(1, "");

				s = str.substring(index[0], index[1]);
				if (!validName(s))
					throw new QueryParserException(1, "");
				vec.addElement(s);
				break;
			}
			else
			{
				s = str.substring(index[0], index[1]);
				if (!validName(s))
					throw new QueryParserException(1, "");
				vec.addElement(s);
			}
		}
		
		// Check if any character exists after ')'. 
		s = str.substring(index[2] + 1, str.length());
		s = s.trim();
		if (!s.isEmpty())
			throw new QueryParserException(1, "");
		

		// Check the columns.
		for (int i = 1; i < vec.size(); i = i + 2)
		{
			// check for column duplicates
			str = vec.elementAt(i);
			for (int j = 1; j < i; j = j + 2)
				if (str.equalsIgnoreCase(vec.elementAt(j)))
					throw new QueryParserException(2, str);
			
			// check for valid data types
			str = vec.elementAt(i + 1);
			if (!str.equalsIgnoreCase("integer") &&
				!str.equalsIgnoreCase("decimal") &&
				!str.equalsIgnoreCase("string") &&
				!str.equalsIgnoreCase("boolean"))
				throw new QueryParserException(3, str);
		}

		// Create the table.
		return new CreateTableQuery(vec);
	}
 	 
	// Parse the 'DROP' command.
	private DropTableQuery parseDROP(String str) throws QueryParserException
	{
		String s;		

		// Find the keyword 'TABLE'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		if (!s.equalsIgnoreCase("TABLE"))
			throw new QueryParserException(1, "");

		// Find the table name.
		s = firstWord(str);
		str = deleteFirstWord(str);

		if (!validName(s))
			throw new QueryParserException(1, "");

		// Check if any character exists after the table name.
		str = str.trim();
		if (!str.isEmpty())
			throw new QueryParserException(1, "");

		// Drop the table.
		return new DropTableQuery(s);
	}

	// Parse the 'INSERT' command.
	private InsertIntoQuery parseINSERT(String str) throws QueryParserException
 	{
		Vector<String> vec = new Vector<String>();
		// At the end:
		// vec[0] to vec[n - 1] = the column names
		// vec[n] to vec[2 * n - 1] = the column values
	
		String s, args, name;
		int argsNum, i, j;
		int[] index1 = new int[3];
		int[] index2 = new int[3];
		
 		// Find the keyword 'INTO'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		if (!s.equalsIgnoreCase("INTO"))
			throw new QueryParserException(1, "");

		// Find the table name.
		index1[0] = 0;		
		if (!findChar('(', str, index1))
			throw new QueryParserException(1, "");
		
		s = str.substring(index1[0], index1[1]);
		if (!validName(s))
			throw new QueryParserException(1, "");
		name = s;

		// Find the arguments between the first parentheses.
		i = index1[2];
		j = str.indexOf(")");

		if (j == -1)
			throw new QueryParserException(1, "");
			
		args = str.substring(i + 1, j + 1);
		if (args.contains("("))
			throw new QueryParserException(1, "");

		index2[0] = 0;
		while (true)
		{
			if (!findChar(',', args, index2))
			{
				findChar(')', args, index2);
				s = args.substring(index2[0], index2[1]);
				if (s.charAt(0) == '"')
					s = args.substring(index2[0], index2[2]);
				else
				{
					if (!validName(s))
						throw new QueryParserException(1, "");
				}
				vec.addElement(s);
				break;
			}
			else
			{
				s = args.substring(index2[0], index2[1]);
				if (s.charAt(0) == '"')
					s = args.substring(index2[0], index2[2]);
				else
				{
					if (!validName(s))
						throw new QueryParserException(1, "");
				}
				vec.addElement(s);				
			}
			index2[0] = index2[2] + 1;
		}
		
		argsNum = vec.size();

		// Find the keyword 'VALUES'.
		index1[0] = j + 1;
		if (!findChar('(', str, index1))
			throw new QueryParserException(1, "");
		
		s = str.substring(index1[0], index1[1]);
		if (!s.equalsIgnoreCase("VALUES"))
			throw new QueryParserException(1, "");
		
		while (true)
		{
			index1[0] = index1[2] + 1;
			if (!findChar(',', str, index1))
			{
				if (!findChar(')', str, index1))
					throw new QueryParserException(1, "");
				else
				{
					s = str.substring(index1[0], index1[1]);
					if (s.charAt(0) == '"')
						s = str.substring(index1[0], index1[2]);
					else
					{
						if (!validName(s))
							throw new QueryParserException(1, "");
					}
					vec.addElement(s);
					break;
				}
			}
			else
			{
				s = str.substring(index1[0], index1[1]);
				if (s.charAt(0) == '"')
					s = str.substring(index1[0], index1[2]);
				else
				{
					if (!validName(s))
						throw new QueryParserException(1, "");
				}
				vec.addElement(s);				
			}
		}
		
		// Check if any character exists after ')'. 
		s = str.substring(index1[2] + 1, str.length());
		s = s.trim();
		if (!s.isEmpty())
			throw new QueryParserException(1, "");

		// Check the number of columns and values.
		if (vec.size() != 2 * argsNum)
			throw new QueryParserException(4, "");

		// Insert a row in the table.
		return new InsertIntoQuery(name, vec, argsNum);
	}

	// Parse the 'DELETE' command.
	private DeleteFromTableQuery parseDELETE(String str) throws QueryParserException
	{
		String s, name;
		String[] condStr = new String[2];
		int[] index = new int[3];
		int j;
		
		condStr[0] = null;
		condStr[1] = null;

		// Find the keyword 'FROM'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		if (!s.equalsIgnoreCase("FROM"))
			throw new QueryParserException(1, "");		

		// Find the table name.
		s = firstWord(str);
		str = deleteFirstWord(str);
		if (!validName(s))
			throw new QueryParserException(1, "");
		name = s;

		// Find the keyword 'WHERE'.
		s = firstWord(str);
		str = deleteFirstWord(str);

		if (!s.equalsIgnoreCase("WHERE"))
		{
			s = s.trim();
			str = str.trim();
			if (!s.isEmpty() || !str.isEmpty())
				throw new QueryParserException(1, "");
		}
		else
		{
			// Find the column name.
			index[0] = 0;

			if (!findChar('=', str, index))
				throw new QueryParserException(1, "");
			s = str.substring(index[0], index[1]);

			if (!validName(s))
				throw new QueryParserException(1, "");
			condStr[0] = s;
			
			// Find the column value.
			if (index[2] + 1 > str.length())
				throw new QueryParserException(1, "");
			str = str.substring(index[2] + 1, str.length());
			str = str.trim();
			if (str.isEmpty())
				throw new QueryParserException(1, "");
			if (str.charAt(0) != '"')
			{
				s = firstWord(str);
				str = deleteFirstWord(str);
				str = str.trim();
				if (!str.isEmpty() || !validName(s))
					throw new QueryParserException(1, "");
			}
			else
			{
				s = str;
				for (j = s.length() - 1; j > 0; ++j)
					if (s.charAt(j) != ' ')
						break;
				s = s.substring(0,  j + 1);
			}
			condStr[1] = s;	
		}

		// Delete one or more rows from the table.
		return new DeleteFromTableQuery(name, condStr, false);	
	}

	// Parse the 'UPDATE' command.
	private UpdateTableDataQuery parseUPDATE(String str) throws QueryParserException
	{
		String s1, s2, name;
		String[] CondStr = new String[2];
		int[] index = new int[3];
		int i, j;

		Vector<String> SetVec = new Vector<String>();
		// At the end:
		// Vector[2 * i]: column names
		// Vector[2 * i + 1]: column values

		CondStr[0] = null;
		CondStr[1] = null;

		// Find the table name.
		s1 = firstWord(str);
		str = deleteFirstWord(str);
		if (!validName(s1))
			throw new QueryParserException(1, "");
		name = s1;

		// Find the keyword 'SET'.
		s1 = firstWord(str);
		str = deleteFirstWord(str);
		if (!s1.equalsIgnoreCase("SET"))
			throw new QueryParserException(1, "");
		
		// Find the UPDATE arguments.
		i = str.indexOf("WHERE ");
		
		if (i == -1)
		{
			s1 = str;
			str = null;
		}
		else
		{
			s1 = str.substring(0, i);
			str = str.substring(i + 6);
		}
		
		while (true)
		{
			// Find the column name.
			index[0] = 0;
			if (!findChar('=', s1, index))
				throw new QueryParserException(1, "");			
			s2 = s1.substring(index[0], index[1]);

			if (!validName(s2))
				throw new QueryParserException(1, "");
			SetVec.addElement(s2);
			
			// Find the column value.
			s1 = s1.substring(index[2] + 1, s1.length());
			s1 = s1.trim();

			index[0] = 0;
			if (findChar(',', s1, index))
			{
				s2 = s1.substring(index[0], index[1]);
				if (s2.charAt(0) == '"')
				{
					s2 = s1.substring(index[0], index[2]);
					for (j = s2.length() - 1; j > 0; --j)
						if (s2.charAt(j) != ' ')
							break;
					s2 = s2.substring(0, j + 1);
				}
				else
				{
					if (!validName(s2))
						throw new QueryParserException(1, "");
				}
				s1 = s1.substring(index[2] + 1, s1.length());
				s1 = s1.trim();
				if (s1.isEmpty())
					throw new QueryParserException(1, "");
			}				
			else	
			{
				s2 = firstWord(s1);
				if (s2.charAt(0) == '"')
				{
					s2 = s1;
					for (j = s2.length() - 1; j > 0; --j)
						if (s2.charAt(j) != ' ')
							break;
					s2 = s2.substring(0, j + 1);
					s1 = "";
				}
				else
				{
					s1 = deleteFirstWord(s1);
					s1 = s1.trim();
					if (!s1.isEmpty() || !validName(s2))
						throw new QueryParserException(1, "");
					}
			}

			SetVec.addElement(s2);

			s1 = s1.trim();
			
			if (s1.isEmpty())
				break;
		}
		
		// Find the condition arguments.
		if (str != null)
		{
			// Find the column name.
			index[0] = 0;
			if (!findChar('=', str, index))
				throw new QueryParserException(1, "");
			s1 = str.substring(index[0], index[1]);
	
			if (!validName(s1))
				throw new QueryParserException(1, "");
			CondStr[0] = s1;
			
			// Find the column value.
			if (index[2] + 1 > str.length())
				throw new QueryParserException(1, "");
			str = str.substring(index[2] + 1, str.length());
			str = str.trim();
			if (str.isEmpty())
				throw new QueryParserException(1, "");
			if (str.charAt(0) != '"')
			{
				s1 = firstWord(str);
				str = deleteFirstWord(str);
				str = str.trim();			
				if (!str.isEmpty() || !validName(s1))
					throw new QueryParserException(1, "");
			}
			else
			{
				s1 = str;
				for (j = s1.length() - 1; j > 0; ++j)
					if (s1.charAt(j) != ' ')
						break;
				s1 = s1.substring(0,  j + 1);
			}
			CondStr[1] = s1;
		}
	
		// Update one or more rows in the table.
		return new UpdateTableDataQuery(name, SetVec, CondStr);		
	}

	// Parse the 'SELECT' command.
	private SelectTableDataQuery parseSELECT(String str) throws QueryParserException
	{
		Vector<String> ColVec = new Vector<String>();
		int i, j;
		int[] index = new int[3];
		String s1, s2, name;
		String[] CondStr = new String[2];
		
		CondStr[0] = null;
		CondStr[1] = null;
		
		// Find the keyword 'FROM'.
		i = str.indexOf("FROM ");
		if (i == -1)
			throw new QueryParserException(1, "");

		s1 = str.substring(0, i);
		str = str.substring(i + 5);

		s1 = s1.trim();
		if (s1.charAt(0) == '*')
		{
			s1 = s1.substring(1);
			s1 = s1.trim();
			if (!s1.isEmpty())
				throw new QueryParserException(1, "");
		}
		else
		{
			while (true)
			{
				// Find the column name.
				index[0] = 0;
				if (findChar(',', s1, index))
				{
					s2 = s1.substring(index[0], index[1]);
					s1 = s1.substring(index[2] + 1, s1.length());
					s1 = s1.trim();
					if (s1.isEmpty())
						throw new QueryParserException(1, "");
				}				
				else	
				{
					s2 = firstWord(s1);
					s1 = deleteFirstWord(s1);
					s1 = s1.trim();
					if (!s1.isEmpty())
						throw new QueryParserException(1, "");
				}

				if (!validName(s2))
					throw new QueryParserException(1, "");
				ColVec.addElement(s2);

				s1 = s1.trim();
				
				if (s1.isEmpty())
					break;
			}		
		}
		
		// Find the table name.
		s1 = firstWord(str);
		str = deleteFirstWord(str);		
		if (!validName(s1))
			throw new QueryParserException(1, "");
		name = s1;
				
		// Find the keyword 'WHERE'.
		s1 = firstWord(str);
		str = deleteFirstWord(str);		
		if (!s1.equalsIgnoreCase("WHERE"))
		{
			s1 = s1.trim();
			str = str.trim();
			if (!s1.isEmpty() || !str.isEmpty())
				throw new QueryParserException(1, "");
		}
		else
		{
			// Find the column name.
			index[0] = 0;

			if (!findChar('=', str, index))
				throw new QueryParserException(1, "");
			s1 = str.substring(index[0], index[1]);

			if (!validName(s1))
				throw new QueryParserException(1, "");
			CondStr[0] = s1;
			
			// Find the column value.
			if (index[2] + 1 > str.length())
				throw new QueryParserException(1, "");
			str = str.substring(index[2] + 1, str.length());
			str = str.trim();
			if (str.isEmpty())
				throw new QueryParserException(1, "");
			if (str.charAt(0) != '"')
			{
				s1 = firstWord(str);
				str = deleteFirstWord(str);
				str = str.trim();
				if (!str.isEmpty() || !validName(s1))
					throw new QueryParserException(1, "");
			}
			else
			{
				s1 = str;
				for (j = s1.length() - 1; j > 0; ++j)
					if (s1.charAt(j) != ' ')
						break;
				s1 = s1.substring(0,  j + 1);				
			}
			CondStr[1] = s1;	
		}
		
		// Select one or more rows from the table.
		return new SelectTableDataQuery(name, ColVec, CondStr);
	}
	
	// Parse the 'GRANT' or 'REVOKE' command.
	private PermissionQuery parseGRANT_REVOKE(String str, boolean isGRANT) throws QueryParserException
	{
		String s1, s2, user, table;
		String[] permission;
		int i;
		// At the end:
		// vec[0] to vec[n - 1] = the column names
		// vec[n] to vec[2 * n - 1] = the column values
		Vector<String> vec = new Vector<String>();
		vec.addElement("user");
		vec.addElement("table");

		// Find the keyword 'ON'.
		s1 = str.toLowerCase();
		i = s1.indexOf("on ");		
		if (i == -1)
			throw new QueryParserException(1, "");
	
		s1 = s1.substring(0, i);
		str = str.substring(i + 3);
		
		// Find the table name.
		s2 = firstWord(str);
		str = deleteFirstWord(str);
		if (!validName(s2))
			throw new QueryParserException(1, "");
		table = s2;
		
		// Find the keyword 'TO' or 'FROM'.
		s2 = firstWord(str);
		str = deleteFirstWord(str);
		if (isGRANT)
		{	
			if (!s2.equalsIgnoreCase("TO"))
				throw new QueryParserException(1, "");
		}
		else if (!s2.equalsIgnoreCase("FROM"))
			throw new QueryParserException(1, "");

		// Find the user's name.
		s2 = firstWord(str);
		str = deleteFirstWord(str);
		if (!validName(s2))
			throw new QueryParserException(1, "");
		user = s2;
		
		// Check if any extra character exists at the end. 
		str = str.trim();
		if (!str.isEmpty())
			throw new QueryParserException(1, "");

		// Find the permissions.
		permission = s1.split(",");
		for (int j = 0; j < permission.length; ++j)
		{
			s1 = firstWord(permission[j]);
			s2 = deleteFirstWord(permission[j]);
			s2.trim();

			if (!s2.isEmpty())
				throw new QueryParserException(1, "");
			if (!(s1.equals("insert") || s1.equals("delete") || s1.equals("update") || s1.equals("select")))
				throw new QueryParserException(1, "");
			vec.addElement(s1);
		}
		
		vec.addElement("\"".concat(user).concat("\""));
		vec.addElement("\"".concat(table).concat("\""));

		for (int j = 0; j < permission.length; ++j)
		{
			if (isGRANT)
				vec.addElement("true");
			else
				vec.addElement("false");			
		}

		return new PermissionQuery(vec, permission.length + 2);
	}
	
	// Delete the first word of a string.
	private String deleteFirstWord(String str)
	{
		int i = 0;
		char c;

		str = str.trim();

		while (i < str.length())
		{
			c = str.charAt(i);
			if (c == ' ')
				break;
			++i;
		}
		
		return str.substring(i);
	}
	
	// Return the first word of a string.
	private String firstWord(String str)
	{
		int i = 0;
		char c;

		str = str.trim();

		while (i < str.length())
		{
			c = str.charAt(i);
			if (c == ' ')
				break;
			++i;
		}
		
		return str.substring(0, i);
	}

	// Find a special character in the string from the given index to the end.
	private boolean findChar(char c, String str, int[] index)
	{
		// At the end:
		// index[0] = index of the first character (neglecting spaces)
		// index[1] = index of the last character of the first word
		// index[2] = index of the given character

		char temp;

		while (index[0] < str.length())
		{
			temp = str.charAt(index[0]);
			if (temp != ' ')
				break;
			++index[0];
		}
		
		index[1] = index[0];

		while (index[1] < str.length())
		{
			temp = str.charAt(index[1]);
			if (temp == c)
			{
				index[2] = index[1];
				return true;
			}
			if (temp == ' ')
				break;
			++index[1];
		}
		
		index[2] = index[1];
		
		while (index[2] < str.length())
		{
			temp = str.charAt(index[2]);
			if (temp == c)
				break;
			++index[2];
		}

		if (index[2] == str.length())
			return false;
		return true;
	}

 	// Check whether the input string consists of valid characters.
	private boolean validName(String str)
	{
		char c;
		boolean flag = true;
		for (int i = 0; i < str.length(); ++i)
		{
			c = str.charAt(i);
			if (((c >= 'a') && (c <= 'z')) ||
				((c >= 'A') && (c <= 'Z')) ||
				((c >= '0') && (c <= '9')) ||
				(c == '_') ||
				(c == '$') ||
				(c == '-') ||
				(c == '+') ||
				(c == '.'))
				continue;
			else
			{
				flag = false;
				break;
			}
		}
		
		return flag;
	}
}
