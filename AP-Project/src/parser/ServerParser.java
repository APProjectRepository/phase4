package parser;

import java.util.Vector;

import parser.QueryParserException;
import query.DeleteFromTableQuery;
import query.InsertIntoQuery;
import query.SingleTableQuery;
import query.UpdateTableDataQuery;

public class ServerParser
{
	public SingleTableQuery parse(String command) throws ServerParserException, QueryParserException
	{
		String str1, str2;
		int i;

		// Find ';' in the command.
		i = command.length() - 1;
		while (i >= 0)
		{
			char c = command.charAt(i);
			if (c == ';')
				break;
			else if (c != ' ')
				throw new ServerParserException(1);
			--i;
		}		
		if (i >= 0)
			command = command.substring(0, i);
		else
			throw new ServerParserException(1);

		str2 = firstWord(command);
		str1 = deleteFirstWord(command);		
		str2 = str2.concat(" " + firstWord(str1));
		str1 = deleteFirstWord(str1);

		// CREATE USER command
		if (str2.equalsIgnoreCase("CREATE USER"))
			return parseCREATE(str1);
		
		// GRANT CONNECT command
		else if (str2.equalsIgnoreCase("GRANT CONNECT"))
			return parseGRANT(str1, 1);

		// GRANT DBA command
		else if (str2.equalsIgnoreCase("GRANT DBA"))
			return parseGRANT(str1, 2);

		// ALTER USER command
		else if (str2.equalsIgnoreCase("ALTER USER"))
			return parseALTER(str1);
			
		// REVOKE CONNECT command
		else if (str2.equalsIgnoreCase("REVOKE CONNECT"))
			return parseREVOKE(str1, 1);

		// REVOKE DBA command
		else if (str2.equalsIgnoreCase("REVOKE DBA"))
			return parseREVOKE(str1, 2);

		// DROP USER command
		if (str2.equalsIgnoreCase("DROP USER"))
			return parseDROP(str1);

		else
			throw new ServerParserException(1);

	}

	// 'CREATE USER' command
 	private InsertIntoQuery parseCREATE(String str) throws ServerParserException
	{
		Vector<String> vec = new Vector<String>();
		// At the end:
		// vec[0] to vec[n - 1] = the column names
		// vec[n] to vec[2 * n - 1] = the column values

		String s;

		vec.addElement("username");
		vec.addElement("password");
		vec.addElement("connect");
		vec.addElement("DBA");
		
		// Find the user's name.
		s = firstWord(str);
		str = deleteFirstWord(str);
		
		if (!validName(s))
			throw new ServerParserException(1);
		vec.addElement("\"".concat(s).concat("\""));
		
		// Find the keyword 'IDENTIFIED BY'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		s = s.concat(" " + firstWord(str));
		str = deleteFirstWord(str);
		if (!s.equalsIgnoreCase("IDENTIFIED BY"))
			throw new ServerParserException(1);
		
		// Find the user's password.
		s = firstWord(str);
		str = deleteFirstWord(str);

		if (!validName(s))
			throw new ServerParserException(1);
		vec.addElement("\"".concat(s).concat("\""));
		
		str = str.trim();
		if (!str.isEmpty())
			throw new ServerParserException(1);

		vec.addElement("false");
		vec.addElement("false");
		
		// Insert a row in the 'users' table.
		return new InsertIntoQuery("users", vec, 4);
	}
 	 
 	// 'GRANT CONNECT' and 'GRANT DBA' commands
	private UpdateTableDataQuery parseGRANT(String str, int ID) throws ServerParserException
	{
		Vector<String> setVec = new Vector<String>();
		String[] condStr = new String[2];
		String s;

		// Find the keyword 'TO'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		
		if (!s.equalsIgnoreCase("TO"))
			throw new ServerParserException(1);
		
		// Find the user's name.
		s = firstWord(str);
		str = deleteFirstWord(str);
		
		if (!validName(s))
			throw new ServerParserException(1);
		
		str = str.trim();
		if (!str.isEmpty())
			throw new ServerParserException(1);
		
		if (ID == 1)
		{
			setVec.addElement("connect");
			setVec.addElement("true");
		}
		else if (ID == 2)
		{
			setVec.addElement("DBA");
			setVec.addElement("true");
		}
		
		condStr[0] = "username";
		condStr[1] = "\"".concat(s).concat("\"");

		return new UpdateTableDataQuery("users", setVec, condStr);
	}
 	
	// 'ALTER' command
	private UpdateTableDataQuery parseALTER(String str) throws ServerParserException
	{
		Vector<String> setVec = new Vector<String>();
		String[] condStr = new String[2];
		String s;

		// Find the user's name.
		s = firstWord(str);
		str = deleteFirstWord(str);
		
		if (!validName(s))
			throw new ServerParserException(1);
		condStr[0] = "username";
		condStr[1] = "\"".concat(s).concat("\"");
		
		// Find the keyword 'IDENTIFIED BY'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		s = s.concat(" " + firstWord(str));
		str = deleteFirstWord(str);
		if (!s.equalsIgnoreCase("IDENTIFIED BY"))
			throw new ServerParserException(1);
		
		// Find the user's password.
		s = firstWord(str);
		str = deleteFirstWord(str);

		str = str.trim();
		if (!str.isEmpty() || s.isEmpty() || !validName(s))
			throw new ServerParserException(2);

		setVec.addElement("password");
		setVec.addElement("\"".concat(s).concat("\""));		

		return new UpdateTableDataQuery("users", setVec, condStr);	
	}
	
 	// 'REVOKE CONNECT' and 'REVOKE DBA' commands
	private UpdateTableDataQuery parseREVOKE(String str, int ID) throws ServerParserException
	{
		Vector<String> setVec = new Vector<String>();
		String[] condStr = new String[2];
		String s;

		// Find the keyword 'FROM'.
		s = firstWord(str);
		str = deleteFirstWord(str);
		
		if (!s.equalsIgnoreCase("FROM"))
			throw new ServerParserException(1);
		
		// Find the user's name.
		s = firstWord(str);
		str = deleteFirstWord(str);
		
		if (!validName(s))
			throw new ServerParserException(1);
		
		str = str.trim();
		if (!str.isEmpty())
			throw new ServerParserException(1);
		
		if (ID == 1)
		{
			setVec.addElement("connect");
			setVec.addElement("false");
		}
		else if (ID == 2)
		{
			setVec.addElement("DBA");
			setVec.addElement("false");
		}
		
		condStr[0] = "username";
		condStr[1] = "\"".concat(s).concat("\"");

		return new UpdateTableDataQuery("users", setVec, condStr);
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
	
	// 'DELETE USER' command
 	private DeleteFromTableQuery parseDROP(String str) throws ServerParserException
	{
		String[] condStr = new String[2];
		String s;
		boolean cascade = false;

		// Find the user's name.
		s = firstWord(str);
		str = deleteFirstWord(str);
		
		if (!validName(s))
			throw new ServerParserException(1);
		condStr[1] = ("\"".concat(s).concat("\""));
		condStr[0] = "username";
		
		s = firstWord(str);
		str = deleteFirstWord(str);

		if (s.equalsIgnoreCase("CASCADE"))
			cascade = true;
		else
		{
			s = s.trim();
			if (!s.isEmpty())
				throw new ServerParserException(1);
		}

		str = str.trim();
		if (!str.isEmpty())
			throw new ServerParserException(1);

		// Delete a row from the 'users' table.
		return new DeleteFromTableQuery("users", condStr, cascade);
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
				(c == '$'))
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
