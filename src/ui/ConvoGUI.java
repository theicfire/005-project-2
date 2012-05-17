package ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.Document;

import main.Client;
import messages.AddToGroupMessage;
import messages.NoticeMessage;
import messages.TextMessage;
import messages.TypingMessage;

public class ConvoGUI extends JFrame implements KeyListener, WindowListener  {

//	private JLabel friendIs;
	private JTextArea convo;
	private JTextField newText;
	private final static String newline = "\n";
	private JScrollPane scrollPane;

	private int roomID;
	private String fromUsername;
	private JLabel pugLabel;
	
	private String baseTitle;
	private boolean otherText = false; // true if the other person has sent some text
	
	public long lastKeyPress; 
	
	public ConvoGUI(String fromUsername, int roomID) {
		this.setLocation((int) (Math.random() * 500), (int) (Math.random() * 500));
		this.fromUsername = fromUsername;
		this.baseTitle = "Room " + roomID;
		this.roomID = roomID;
		
		java.net.URL imageURL = LoginGUI.class.getResource("img/pugCslice.png");
		ImageIcon pugPic = new ImageIcon(imageURL);
		pugLabel = new JLabel(pugPic);


		newText = new JTextField();
		newText.setName("newText");
        newText.addKeyListener(this);
		newText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent a) {
				enterTextFromField();
			}
		});
		
		lastKeyPress = System.currentTimeMillis();
		
		convo = new JTextArea();
		convo.setName("Convo");
		convo.setEditable(false);
		convo.setLineWrap(true);
        scrollPane = new JScrollPane(convo);

        
        /*
        friendIs = new JLabel(" "+fromUsername+":");
        friendIs.setName("friendIs");
*/

		setPreferredSize(new Dimension(450, 450));
		createAndShowGUI();
		addWindowListener(this);
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == 10){
	        Client.getQueue().offer(new TypingMessage(fromUsername, roomID, TypingMessage.types.NOTHING));
			return;
		}
    	new Thread(new Runnable(){
    		public void run(){
    			if(System.currentTimeMillis()-lastKeyPress > 1000)
   					Client.getQueue().offer(new TypingMessage(fromUsername,	roomID,	TypingMessage.types.TYPING));
    			
    			lastKeyPress = System.currentTimeMillis();
    			
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		
    			if(System.currentTimeMillis()-lastKeyPress > 900)
    				Client.getQueue().offer(new TypingMessage(fromUsername,
    														  roomID,
    														  newText.getText().equals("") ? TypingMessage.types.NOTHING : 
    															  							 TypingMessage.types.ENTERED));
    		}
    	}).start();
    }
	
	public void scrollDown() {
		Document d = convo.getDocument();
		convo.select(d.getLength(), d.getLength());
	}

	public void setStatus(TypingMessage message) {
		setTitle(baseTitle + message.toTitle());
	}
	private void enterTextFromField(){
		String text = newText.getText();
		if(text.equals(""))
			return;
		if(text.startsWith("/add ")){
			String toUsername = text.substring(5);
			
			if(Client.getBuddyList().getOnline().contains(toUsername)){
				Client.getQueue().offer(new AddToGroupMessage(fromUsername, toUsername, roomID));
			} else {
				convo.append(fromUsername + ": " + toUsername + " is not online!" + newline);
			}
		} else {
			convo.append(fromUsername + ": "+ text + newline);
			Client.getQueue().offer(new TextMessage(fromUsername, roomID, newText.getText()));
		}
		
		
		newText.setText("");
		scrollDown();
	}
	
	public void handleTextMessage(TextMessage message) {
		handleTextMessage(message, false);
	}
	
	/**
	 * 
	 * @param message
	 * @param notice if this is on, don't make your window visible just because of it
	 */
	public void handleTextMessage(TextMessage message, boolean notice) {
		convo.append(message.getFromUsername() + ": " + message.getText() + newline);
		if(!otherText && !notice) {
			this.setVisible(true);
			otherText = true;
		}
//		System.out.println(message.getFromUsername() + "|" + message.getText() + " just sent a message (enterText)");
	}
	
	public void createAndShowGUI() {

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
							.addComponent(pugLabel))
					.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup().addComponent(scrollPane))
								.addComponent(newText)));

		layout.setVerticalGroup(layout
				.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(pugLabel))
				.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup().addComponent(scrollPane))
							.addComponent(newText)));

		layout.linkSize(SwingConstants.HORIZONTAL, pugLabel);
		layout.linkSize(SwingConstants.VERTICAL, newText);

		setTitle(baseTitle);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				ConvoGUI main = new ConvoGUI("chase", 0);

				main.setVisible(true);

			}
		});
	}
	
	public boolean hasOtherText() {
		return otherText;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		Client.getQueue().offer(new NoticeMessage(fromUsername, "none", roomID, "closing"));
		System.out.println("window closing");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
