package ui;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class BuddyList extends JFrame implements ListSelectionListener, ActionListener {
	//http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDemoProject/src/components/ListDemo.java
	
	private JList list;
    private DefaultListModel listModel;

    private JTextField partnerName;
    private static final String requestString = "Request";
    private JButton requestButton;

	private static List<String> online = new ArrayList<String>();
	
	public BuddyList(String username) {
	    listModel = new DefaultListModel();
	
	    //Create the list and put it in a scroll pane.
	    list = new JList(listModel);
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.addListSelectionListener(this);
	    list.setVisibleRowCount(10);
	    JScrollPane listScrollPane = new JScrollPane(list);

	    MouseListener mouseListener = new MouseAdapter() {
	        public void mouseClicked(MouseEvent e) {
	            if (e.getClickCount() == 2) {
	                int index = list.locationToIndex(e.getPoint());
	                request(listModel.getElementAt(index).toString());
	             }
	        }
	    };
	    list.addMouseListener(mouseListener);
	    
	    
	    requestButton = new JButton(requestString);
	    requestButton.setActionCommand(requestString);
	    requestButton.addActionListener(this);
	    requestButton.setEnabled(false);
	
	    partnerName = new JTextField(10);
	    partnerName.addActionListener(this);
	    
	    //Create a panel that uses GroupLayout.
	    GroupLayout layout = new GroupLayout(getContentPane());
	    getContentPane().setLayout(layout);
	    layout.setAutoCreateGaps(true);
	    layout.setAutoCreateContainerGaps(true);
	    
	    layout.setHorizontalGroup(layout.createSequentialGroup()
	    	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	    	        .addComponent(listScrollPane)
	    	        .addGroup(layout.createSequentialGroup()
	    	            .addComponent(partnerName)
	    	            .addComponent(requestButton))));
	    
	    layout.setVerticalGroup(layout.createSequentialGroup()
    	        .addComponent(listScrollPane)
	    	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	    	        .addComponent(partnerName)
	    	        .addComponent(requestButton)));
	    
        setTitle("Logged in as: " + username);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
    public void actionPerformed(ActionEvent e) {
    	request(partnerName.getText());
    }
    
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
        	requestButton.setEnabled(true);
        	partnerName.setText(listModel.getElementAt(list.getSelectedIndex()).toString());
        }
    }
	public void buddyLogin(String fromUsername) {
		if (!online.contains(fromUsername)) {
		    listModel.addElement(fromUsername);
		    online.add(fromUsername);
		    return;
		}
		throw new RuntimeException("buddyLogin: Logged in user that was already logged in.");
	}

	public void buddyLogout(String fromUsername) {
		if (online.contains(fromUsername)) {
		    int index = online.indexOf(fromUsername);
		    listModel.remove(index);
		    online.remove(index);
		    return;
		}
		throw new RuntimeException("buddyLogout: Logged out user that was already logged out.");
		
	}
	
	public void request(String username){
		System.out.println("Chat request sent for: " + username);
		// TODO
	}
	
	public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                                  "javax.swing.plaf.metal.MetalLookAndFeel");
                                //  "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                                //UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                BuddyList buddyList = new BuddyList("tschultz");
                buddyList.setVisible(true);
                try {
					buddyList.buddyLogin("theicfire");
					buddyList.buddyLogin("theicfire2");
					buddyList.buddyLogin("theicfire3");
					buddyList.buddyLogout("theicfire2");
					buddyList.buddyLogin("theicfire3");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
}
