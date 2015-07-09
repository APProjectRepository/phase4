package net;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import orm.Column;
import orm.InvalidORClass;
import orm.Setter;
import orm.Table;
import query.CreateTableQuery;

public class ClientNetworkInterface {
	private Socket client;
	private Scanner s;
	private PrintWriter pw;

	public ClientNetworkInterface(String serverAddress, int serverPort,
			String user, String password) throws NetworkException,
			AuthenticationException {
		// After connecting to the server at serverAddress:serverPort, client
		// sends this message to the server for authentication: HELLO FROM
		// <user>:<password>
		// If the pair of the user and the password is valid and the client has
		// connection permission, server returns: HELLO <user>
		// The the client can run queries on server through execute method and
		// the established connection.
		// Otherwise, server returns: ERROR <message>
		// and server closes the connection. Then, based on the error message,
		// proper exception is thrown.

		try {
			String str;
			client = new Socket(serverAddress, serverPort);
			s = new Scanner(client.getInputStream());
			pw = new PrintWriter(client.getOutputStream());

			// Send the authentication information
			pw.println("HELLO FROM " + user + ":" + password);
			;
			pw.flush();

			// Get the authentication result.
			str = s.nextLine();
			if (str.contains("ERROR"))
				throw new AuthenticationException(str);
		} catch (IOException e) {
			throw new NetworkException();
		}
	}

	public void disconnect() {
		// Client sends this message to the server and closes the connection:
		// BYE
		pw.println("BYE");
		pw.flush();
	}

	public String execute(String command, String username)
			throws AuthenticationException {
		// Client sends directly to the server the command string terminated by
		// a semicolon character at the end.
		// Server returns the result as a string terminated by semicolon
		// character at the end.
		// Note that if the query result itself contains semicolon character, it
		// will be escaped by repeating it twice.
		boolean b = false;
		String str;
		String temporary = "";
		String create = "";
		try {
			if (command.toLowerCase().startsWith("create object")) {

				b = true;
				Class<?> newClass = Class.forName(command.split(" ")[2]);
				command = command.toLowerCase();
				if (newClass.isAnnotationPresent(orm.Table.class)) {
					Object o = newClass.newInstance();
					command = "insert into "
							+ newClass.getAnnotation(orm.Table.class).name()
							+ "(";
					String vals = "";
					create = "create table "
							+ newClass.getAnnotation(orm.Table.class).name()
							+ "(";
					for (int i = 0; i < newClass.getDeclaredMethods().length; i++) {
						Method cur = newClass.getDeclaredMethods()[i];
						if (cur.isAnnotationPresent(Setter.class)) {
							boolean invoked = false;

							String type = newClass
									.getDeclaredField(
											(cur.getAnnotation(Setter.class)
													.fieldName())).getType()
									.getName();
							String field = newClass.getDeclaredField(
									(cur.getAnnotation(Setter.class)
											.fieldName())).getName();
							Object temp = "";
							String in = JOptionPane.showInputDialog(null,
									"Set (" + type + ") " + field + " :");
							if (type.contains("String")) {
								if (in.trim().equals("")) {
									temp = "null";
								} else
									temp = in;
								invoked = true;
							} else if (type.equalsIgnoreCase("int")) {
								try {
									temp = Integer.parseInt(in);
									invoked = true;
								} catch (Exception e) {
									invoked = false;
								}
							} else if (type.equals("long")) {
								try {
									temp = Long.parseLong(in);
									invoked = true;
								} catch (Exception e) {
									invoked = false;
								}
							} else if (type.equals("double")) {
								try {
									temp = Double.parseDouble(in);
									invoked = true;
								} catch (Exception e) {
									invoked = false;
								}
							} else if (type.equals("boolean")) {
								if (in.equals("true")) {
									temp = true;
									invoked = true;
								} else if (in.equals("false")) {
									temp = false;
									invoked = true;
								} else
									invoked = false;
							}
							if (invoked) {
								cur.invoke(o, temp);
								JOptionPane.showMessageDialog(null, "<<"
										+ field + ">> setted!");
							} else {
								JOptionPane.showMessageDialog(null,
										"Invalid input, try again!");
								i--;
							}
						}
					}

					for (int i = 0; i < newClass.getDeclaredMethods().length; i++) {
						Method cur = newClass.getDeclaredMethods()[i];
						if (cur.isAnnotationPresent(Column.class)) {
							command += cur.getAnnotation(Column.class).name()
									+ ", ";
							Object val = cur.invoke(o, new Object[] {});
							if (val instanceof String) {
								vals += ("\"" + val + "\", ");
							} else
								vals += (cur.invoke(o, new Object[] {}) + ", ");
							create += cur.getAnnotation(Column.class).name()
									+ " : "
									+ cur.getAnnotation(Column.class).type()
									+ ", ";
						}
					}
					command = command.substring(0, command.length() - 2);
					vals = vals.substring(0, vals.length() - 2);
					create = create.substring(0, create.length() - 2);
					create += ")";
					command += ") values (" + vals + ")";
					temporary = command;
				} else
					JOptionPane.showMessageDialog(null,
							"You can't create object from this kind of class.");

			} else if (command.toLowerCase().startsWith("view object")) {
				Class<?> newClass = Class.forName(command.split(" ")[2]);
				command = command.toLowerCase();
				String con;
				String fieldName = "";
				if (newClass.isAnnotationPresent(Table.class)) {

					for (Method cur : newClass.getDeclaredMethods()) {
						if (cur.isAnnotationPresent(Column.class)) {
							if (cur.getAnnotation(Column.class).isKey()) {
								fieldName = newClass.getDeclaredField(
										cur.getAnnotation(Column.class).name())
										.getName();
								break;
							}
						}
					}
					if (command.contains("where")) {
						con = "WHERE " + fieldName + " = "
								+ command.split("=")[1].trim();
					} else
						con = "";

					command = "select * from "
							+ newClass.getAnnotation(Table.class).name() + " "
							+ con;
				} else
					JOptionPane.showMessageDialog(null,
							"You can't view this object.");

			} else if (command.toLowerCase().startsWith("edit object")) {
				// edit object samples.SampleOne set f1, f2,
				// f3, f4 where key = 1;
				// f3, f4 where key = 1;
				// edit object samples.SampleOne set "a",12,true,1 where key = 1

				Class<?> newClass = Class.forName(command.split(" ")[2]);

				command = command.toLowerCase();
				String con;
				String set = "";
				String[] tempField = command.split("[, ]");
				ArrayList<String> fields = new ArrayList<>();

				for (int i = 0; i < tempField.length; i++) {
					if (tempField[i].equals("set")) {
						for (int j = i + 1; j < tempField.length
								&& !tempField[j].equals("where"); j++)
							if (!tempField[j].equals(""))
								fields.add(tempField[j]);
					}
				}
				String fieldName = "";

				if (newClass.isAnnotationPresent(Table.class)) {
					for (int i = 0; i < newClass.getDeclaredMethods().length; i++) {
						Method cur = newClass.getDeclaredMethods()[i];
						if (cur.isAnnotationPresent(Column.class))
							if (cur.getAnnotation(Column.class).isKey())
								fieldName = newClass.getDeclaredField(
										cur.getAnnotation(Column.class).name())
										.getName();
						if (cur.isAnnotationPresent(Setter.class)) {
							for (int j = 0; j < fields.size(); j++)
								if (cur.getAnnotation(Setter.class).fieldName()
										.equals(fields.get(j).trim())) {
									boolean invoked = false;
									String type = newClass
											.getDeclaredField(
													(cur.getAnnotation(Setter.class)
															.fieldName()))
											.getType().getName();
									String field = newClass.getDeclaredField(
											(cur.getAnnotation(Setter.class)
													.fieldName())).getName();
									Object temp = "";
									String in = JOptionPane.showInputDialog(
											null, "Set (" + type + ") " + field
													+ " :");
									if (type.contains("String")) {
										if (in.trim().equals("")) {
											temp = "null";
										} else
											temp = "\"" + in + "\"";
										invoked = true;
									} else if (type.equals("long")) {
										try {
											temp = Long.parseLong(in);
											invoked = true;
										} catch (Exception e) {
											invoked = false;
										}
									} else if (type.equals("int")) {
										try {
											temp = Integer.parseInt(in);
											invoked = true;
										} catch (Exception e) {
											invoked = false;
										}
									} else if (type.equals("double")) {
										try {
											temp = Double.parseDouble(in);
											invoked = true;
										} catch (Exception e) {
											invoked = false;
										}
									} else if (type.equals("boolean")) {
										if (in.equals("true")) {
											temp = true;
											invoked = true;
										} else if (in.equals("false")) {
											temp = false;
											invoked = true;
										} else
											invoked = false;
									}
									if (invoked) {
										cur.invoke(newClass.newInstance(), temp);
										for (String s : fields)
											if (field.equals(s.trim())) {
												set += s + " = " + temp + ", ";
												break;
											}
										JOptionPane.showMessageDialog(null,
												"<<" + field + ">> setted!");
									} else {
										JOptionPane.showMessageDialog(null,
												"Invalid input, try again!");
										i--;
									}
								}
						}
					}

					if (command.contains("where")) {
						con = "WHERE " + fieldName + " = "
								+ command.split("=")[1].trim();
					} else
						con = "";

					set = set.substring(0, set.lastIndexOf(","));
					command = "update "
							+ newClass.getAnnotation(Table.class).name()
							+ " set " + set + " " + con;
				} else
					JOptionPane.showMessageDialog(null,
							"You can't view this object.");

			} else if (command.startsWith("delete object")) {
				// b = true;
				Class<?> newClass = Class.forName(command.split(" ")[2]);
				String con = command.split("=")[1].trim();
				String fieldName = null;

				if (newClass.isAnnotationPresent(Table.class)) {

					for_methods: for (int i = 0; i < newClass
							.getDeclaredMethods().length; i++) {
						Method cur = newClass.getDeclaredMethods()[i];
						if (cur.isAnnotationPresent(Column.class)) {
							if (cur.getAnnotation(Column.class).isKey()) {
								fieldName = newClass.getDeclaredField(
										cur.getAnnotation(Column.class).name())
										.getName();
								break for_methods;
							}
						}
					}
					command = "delete from "
							+ newClass.getAnnotation(Table.class).name()
							+ " WHERE " + fieldName + " = " + con;
				} else
					JOptionPane.showMessageDialog(null,
							"You can't delete object.");
			}

			// Send the command.
			pw.println(command + ";");
			pw.flush();

			// Get the result.
			str = s.nextLine();

			if (str.equals("ERROR: Invalid username") // The user has been
														// deleted
														// by the server during
														// the
														// connection.
					|| str.equals("ERROR: Invalid password") // The user's
																// password
																// has been
																// changed
																// by the server
																// during the
																// connection.
					|| str.equals("ERROR: Connection not permitted") // The
																		// user's
																		// connection
																		// permission
																		// has
																		// been
																		// removed
																		// by
																		// the
																		// server
																		// during
																		// the
																		// connection.
					|| str.equals("ERROR: Invalid protocol")) // The
																// client-server
																// protocol is
																// not
																// valid.
				throw new AuthenticationException(str);

			if (!b) {
				str = modify(str, username);
				str = str.replaceAll("#", "\n");
				return str;
			}

			if (str.contains("ERROR: Invalid table name ") && b) {
				pw.println(create + ";");
				pw.flush();
				str = s.nextLine();
				pw.println(temporary + ";");
				pw.flush();
				str = s.nextLine();
				return "Object Created!";
			} else if (!str.contains("table name") && b) {
				if (command.contains("create"))
					return "Object created";
				else
					return str;
			}
			return null;
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException | Error e) {
			return "You can't create this kind of class";
		}

	}

	private String modify(String str, String username) {
		int i = str.indexOf(username.concat("."));
		if (i != -1)
			str = str.substring(0, i).concat(
					str.substring(username.length() + i + 1));
		return str;
	}

}
