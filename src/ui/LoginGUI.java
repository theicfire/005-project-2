package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
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
	private JLabel teamPicLabel;

	// holds all the "guessing" threads. This is necessary because
	// each GuessThread has a kill method that needs to be called when a new
	// puzzle is made.

	public LoginGUI() throws IOException {
		super("Login");
		loginButton = new JButton();
		loginButton.setName("loginButton");
		loginButton.setText("Login");
		username = new JTextField(20);
		username.setName("username");

		JLabel usernameLabel = new JLabel("Type a username:");
				
		
		java.net.URL imageURL = LoginGUI.class.getResource("teamPhoto.png");
		ImageIcon teamPic = new ImageIcon(imageURL);
		teamPicLabel = new JLabel(teamPic);

		// call System.exit() when user closes the window
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container cp = this.getContentPane();


		// Make the layout
		GroupLayout layout = new GroupLayout(cp);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		cp.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(teamPicLabel))
				.addGroup(layout.createSequentialGroup()
						.addComponent(usernameLabel).addComponent(username)
						.addComponent(loginButton)));
				
		layout.setVerticalGroup(layout
				.createSequentialGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(teamPicLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(usernameLabel).addComponent(username)
						.addComponent(loginButton)));


		
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
	
	/*
	private void paintComponent(Graphics g, Image img){
		g.drawImage(img, 0, 0, null);
	}*/

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

	public void makeDialog() {
		Object[] options = { "Yes, please", "No, thanks" };
		int n = JOptionPane.showOptionDialog(this, "Connect with this user?",
				"Chat Request", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 1 || n == -1) {
			System.out.println("The user hit the second option or hit exit");
		} else {
			System.out.println("The user hit the first option");
		}
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LoginGUI main;
				try {
					main = new LoginGUI();
					main.setVisible(true);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

}
