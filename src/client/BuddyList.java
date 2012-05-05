package client;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class BuddyList extends JFrame implements ListSelectionListener {
	//http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDemoProject/src/components/ListDemo.java
	
	private JList list;
    private DefaultListModel listModel;

    private JTextField partnerName;
    private static final String requestString = "Request";
    private JButton requestButton;

	private static List<String> online = new ArrayList<String>();
	
	public BuddyList(String username) {
	    listModel = new DefaultListModel();
	    listModel.addElement("Test 1");
	    listModel.addElement("Test 2");
	
	    //Create the list and put it in a scroll pane.
	    list = new JList(listModel);
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.addListSelectionListener(this);
	    list.setVisibleRowCount(10);
	    JScrollPane listScrollPane = new JScrollPane(list);
	
	    requestButton = new JButton(requestString);
	    //RequestListener requestListener = new RequestListener(requestButton);
	    requestButton.setActionCommand(requestString);
	    //requestButton.addActionListener(requestListener);
	    requestButton.setEnabled(false);
	
	    partnerName = new JTextField(10);
	    //partnerName.addActionListener(hireListener);
	    //partnerName.getDocument().addDocumentListener(hireListener);
	
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
	    
        setTitle("Find");
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
    public void actionPerformed(ActionEvent e) {
    	// TODO
    }
    
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
        	requestButton.setEnabled(true);
        	partnerName.setText(listModel.getElementAt(list.getSelectedIndex()).toString());
        }
    }

	public void buddyLogin(String fromUsername) {
		// TODO Auto-generated method stub
		
	}

	public void buddyLogout(String fromUsername) {
		// TODO Auto-generated method stub
		
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
                new BuddyList("tschultz").setVisible(true);
            }
        });
    }
}
