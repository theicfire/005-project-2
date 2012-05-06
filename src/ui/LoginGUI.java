package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import main.Client;

/**
 * This is the GUI that plays Jotto.
 */
public class LoginGUI extends JFrame {

	// keep these names
	private JButton loginButton;
	private JTextField username;

	// holds all the "guessing" threads. This is necessary because
	// each GuessThread has a kill method that needs to be called when a new
	// puzzle is made.

	public LoginGUI() {
		super("Login");
		loginButton = new JButton();
		loginButton.setName("loginButton");
		loginButton.setText("Login");
		username = new JTextField(20);
		username.setName("username");

		JLabel usernameLabel = new JLabel("Type a username:");

		// call System.exit() when user closes the window
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container cp = this.getContentPane();

		// Make the layout
		GroupLayout layout = new GroupLayout(cp);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		cp.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(usernameLabel).addComponent(username)
				.addComponent(loginButton));
		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(usernameLabel).addComponent(username)
				.addComponent(loginButton));

		// Listeners
		username.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login(username.getText());
			}
		});
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("clicking login");
				login(username.getText());
			}
		});

		// size the frame
		this.pack();
	}

	private void login(String username) {
		System.out.println("loggging in with " + username);
//		makePopup();
//		makeDialog();
		// TODO close
		this.dispose();
		new Client(username);
	}

	public static void makePopup() {
		JFrame frame = new JFrame("Button Popup Sample");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JButton start = new JButton("Pick Me for Popup");
		frame.add(start);

		frame.setSize(350, 250);
		frame.setVisible(true);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LoginGUI main = new LoginGUI();
				main.setVisible(true);
			}
		});

	}

}
