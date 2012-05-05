package ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class convoGui extends JFrame {

	private JTextArea convo;
	private JButton enter;
	private JTextField newText;
	private final static String newline = "\n";
	private String buddy = "Jesus";

	public convoGui() {
		
		enter = new JButton("Enter");
		enter.setName("Enter");
		enter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				enterTextNow();
			}
		});

		newText = new JTextField();
		newText.setName("newText");
		newText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent a) {
				enterTextNow();
			}
		});

		convo = new JTextArea();
		convo.setName("Convo");
		convo.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(convo);


		setPreferredSize(new Dimension(450, 350));
		createAndShowGUI();

	}
	
	private void enterTextNow(){
		String toAdd = buddy + ": "+ newText.getText() + newline;
		convo.append(toAdd);
		newText.setText("");
	}

	public void createAndShowGUI() {

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(layout.createSequentialGroup().addComponent(convo))
				.addGroup(
						layout.createSequentialGroup().addComponent(newText)
								.addComponent(enter)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup().addComponent(convo))
				.addGroup(
						layout.createParallelGroup().addComponent(newText)
								.addComponent(enter)));

		// layout.linkSize(SwingConstants.HORIZONTAL, puzzleNumber, guessHere);
		layout.linkSize(SwingConstants.VERTICAL, newText, enter);

		setTitle("Talk to "+buddy);
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				convoGui main = new convoGui();

				main.setVisible(true);

			}
		});
	}

}
