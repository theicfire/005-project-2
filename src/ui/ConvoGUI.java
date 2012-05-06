package ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import main.Client;
import messages.RequestMessage;
import messages.TextMessage;

public class ConvoGUI extends JFrame {

	private JTextArea convo;
	private JButton enter;
	private JTextField newText;
	private final static String newline = "\n";
	private JScrollPane scrollPane;
	private String toUsername;
	private String fromUsername;
	private boolean otherText = false; // true if the other person has sent some text
	
	public ConvoGUI(String fromUsername, String toUsername) {
		this.fromUsername = fromUsername;
		this.toUsername = toUsername;
		enter = new JButton("Enter");
		enter.setName("Enter");
		enter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				enterTextFromField();
			}
		});

		newText = new JTextField();
		newText.setName("newText");
		newText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent a) {
				enterTextFromField();
			}
		});

		convo = new JTextArea();
		convo.setName("Convo");
		convo.setEditable(false);
        scrollPane = new JScrollPane(convo);


		setPreferredSize(new Dimension(450, 350));
		createAndShowGUI();
		
	}
	
	private void enterTextFromField(){
		String toAdd = fromUsername + ": "+ newText.getText() + newline;
		convo.append(toAdd);
		Client.getQueue().offer(new TextMessage(fromUsername, toUsername, newText.getText()));
		newText.setText("");
	}
	
	public void enterText(String text) {
		convo.append(toUsername + ": "+ text + newline);
		if (!otherText) {
			this.setVisible(true);
			otherText = true;
		}
		System.out.println(fromUsername + "|" + toUsername + " just sent a message (enterText)");
	}
	
	public void createAndShowGUI() {

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(layout.createSequentialGroup().addComponent(scrollPane))
				.addGroup(
						layout.createSequentialGroup().addComponent(newText)
								.addComponent(enter)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup().addComponent(scrollPane))
				.addGroup(
						layout.createParallelGroup().addComponent(newText)
								.addComponent(enter)));

		// layout.linkSize(SwingConstants.HORIZONTAL, puzzleNumber, guessHere);
		layout.linkSize(SwingConstants.VERTICAL, newText, enter);

		setTitle("Talk to " + toUsername);
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				ConvoGUI main = new ConvoGUI("chase", "bob");

				main.setVisible(true);

			}
		});
	}
	
	public boolean hasOtherText() {
		return otherText;
	}

}
