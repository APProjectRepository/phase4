package dos.math.sharif.edu.cli;

import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientGraphics extends JFrame implements ActionListener {
	public static final long serialVersionUID = 1L;

	public String username;
	public String password;
	public String address;
	public String port;
	public String command;
	public Vector<String> browseCommands;

	public boolean connectPressed;
	public boolean connected;
	public boolean commandReceived;
	public boolean disconnectPressed;

	public JLabel lblAddress;
	public JTextField tfAddress;
	public JLabel lblPort;
	public JTextField tfPort;
	public JLabel lblUsername;
	public JTextField tfUsername;
	public JLabel lblPassword;
	public JTextField tfPassword;
	public JButton btnConnect;
	public JButton btnDisconnect;
	public JLabel lblStatus;
	public JLabel lblCommand;
	public JTextField tfCommand;
	public JButton btnSend;
	public JButton btnBrowse;
	public JLabel lblResult;
	public TextArea taResult;
	public JFileChooser fileChooser;

	public ClientGraphics() {
		connectPressed = false;
		connected = false;
		commandReceived = false;
		disconnectPressed = false;

		browseCommands = new Vector<String>();

		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(null);

		JPanel panel = new JPanel();
		panel.setLayout(null);

		// address label
		lblAddress = new JLabel("address");
		lblAddress.setBounds(10, 10, 60, 20);
		panel.add(lblAddress);

		// address text field
		tfAddress = new JTextField();
		tfAddress.setBounds(63, 10, 100, 20);
		panel.add(tfAddress);

		// port label
		lblPort = new JLabel("port");
		lblPort.setBounds(188, 10, 60, 20);
		panel.add(lblPort);

		// port text field
		tfPort = new JTextField();
		tfPort.setBounds(218, 10, 100, 20);
		panel.add(tfPort);

		// username label
		lblUsername = new JLabel("username");
		lblUsername.setBounds(343, 10, 60, 20);
		panel.add(lblUsername);

		// username text field
		tfUsername = new JTextField();
		tfUsername.setBounds(408, 10, 100, 20);
		panel.add(tfUsername);

		// password label
		lblPassword = new JLabel("password");
		lblPassword.setBounds(533, 10, 60, 20);
		panel.add(lblPassword);

		// password text field
		tfPassword = new JTextField();
		tfPassword.setBounds(598, 10, 100, 20);
		panel.add(tfPassword);

		// connect button
		btnConnect = new JButton("connect");
		btnConnect.setBounds(10, 40, 100, 20);
		panel.add(btnConnect);
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!connected) {
					connectPressed = true;
					address = tfAddress.getText();
					port = tfPort.getText();
					username = tfUsername.getText();
					password = tfPassword.getText();
				}
			}
		});

		// disconnect button
		btnDisconnect = new JButton("disconnect");
		btnDisconnect.setBounds(120, 40, 100, 20);
		panel.add(btnDisconnect);
		btnDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (connected)
					disconnectPressed = true;
			}
		});

		// status label
		lblStatus = new JLabel("status: disconnected");
		lblStatus.setBounds(250, 40, 120, 20);
		panel.add(lblStatus);

		// command label
		lblCommand = new JLabel("command");
		lblCommand.setBounds(10, 80, 60, 20);
		panel.add(lblCommand);

		// command text field
		tfCommand = new JTextField();
		tfCommand.setBounds(75, 80, 700, 20);
		panel.add(tfCommand);

		// send button
		btnSend = new JButton("send");
		btnSend.setBounds(10, 110, 100, 20);
		panel.add(btnSend);
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (connected) {
					commandReceived = true;
					command = tfCommand.getText();
					tfCommand.setText("");
				}
			}
		});

		btnSend.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					if (connected) {
						commandReceived = true;
						command = tfCommand.getText();
						tfCommand.setText("");
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		// browse button
		btnBrowse = new JButton("browse");
		btnBrowse.setBounds(120, 110, 100, 20);
		panel.add(btnBrowse);
		btnBrowse.addActionListener(this);

		// result label
		lblResult = new JLabel("result");
		lblResult.setBounds(10, 150, 60, 20);
		panel.add(lblResult);

		// result text field
		taResult = new TextArea();
		taResult.setBounds(10, 175, 765, 375);
		taResult.setEditable(false);
		panel.add(taResult);

		getContentPane().add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Client");
		setSize(800, 600);
		setResizable(true);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals("browse")) {
			if (connected) {
				int result = fileChooser.showOpenDialog(this);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File f = fileChooser.getSelectedFile();
						Scanner s = new Scanner(f);
						String str = "";
						String[] c;
						while (s.hasNext()) {
							str += s.nextLine();
							str += "\n";
						}
						s.close();
						c = str.split("\n");

						for (int i = 0; i < c.length; ++i)
							browseCommands.addElement(c[i]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}