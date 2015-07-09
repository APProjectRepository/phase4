package orm;

import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ORMUserInterface extends JFrame {

	private Map<String, String> option;
	private JPanel panel;

	public ORMUserInterface(Map<String, String> option) {
		this.option = option;

		panel = new JPanel();

		getContentPane().add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("ORM User Interface");
		setSize(800, 600);
		setResizable(false);
		setVisible(true);
	}

	private void initCreatePanel(JPanel panel) {

	}

	private void initViewPanel(JPanel panel) {

	}

}
