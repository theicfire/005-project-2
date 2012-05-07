package ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DialogGUI extends JFrame {

	public boolean makeDialog() {
		Object[] options = { "Yes, please", "No, thanks"};
		int n = JOptionPane.showOptionDialog(this,
				"Connect with this user?",
				"Chat Request", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 1 || n == -1) {
			System.out.println("The user hit the second option or hit exit");
			return false;
		}
		System.out.println("The user hit the first option");
		return true;
	}
}
