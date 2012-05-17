package main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;
import ui.BuddyList;
import ui.ConvoGUI;
import ui.LoginGUI;
import messages.*;
import client.*;

/**
 * GUI chat client runner.
 */
public class Client {
	private static ConcurrentHashMap<Integer, ConvoGUI> chats = new ConcurrentHashMap<Integer, ConvoGUI>();
	private String username;
	private static BuddyList buddyList;
	private static ArrayBlockingQueue<Message> queue;
	public static LoginGUI loginGui;

	public Client(String username, String host, String port) throws UnknownHostException, IOException {
		int portNum = Integer.parseInt(port);
		Socket socket = new Socket(host, portNum);
		this.username = username;
		queue = new ArrayBlockingQueue<Message>(1000);
		SendToServerConnection sender = new SendToServerConnection(socket, queue, username);
		sender.start();
		ReceiveFromServerConnection receiver = new ReceiveFromServerConnection(socket, username, loginGui);
		receiver.start();
	}

	/**
	 * Makes a buddy list for the current user
	 * @param username the username to log in
	 */
	public static void login(String username) {
		buddyList = new BuddyList(username);
		buddyList.setVisible(true);
	}

	/**
	 * Handles connection messages by calling loggedIn/loggedOut.
	 */
	public static void handleConnectionMessage(ConnectionMessage message) {
		while (buddyList == null) {
			// block until this happens
		}
		try {
			if (message.type == ConnectionMessage.types.CONNECT) {
				buddyList.buddyLogin(message.getFromUsername());

			} else if (message.type == ConnectionMessage.types.DISCONNECT) {
				buddyList.buddyLogout(message.getFromUsername());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles AddToGroup messages by making new convoGUI's, where appropriate
	 * @param message
	 */
	public static void handleAddToGroupMessage(AddToGroupMessage message) {
		if (!chats.containsKey(message.getRoomID())){
			ConvoGUI convoGUI = new ConvoGUI(message.getToUsername(), message.getRoomID());
			chats.put(message.getRoomID(),convoGUI);
		} else {
			System.out.println("This client already has the added room ID");
		}
	}
	
	/**
	 * Wrapper to have notice=false
	 */
	public static void handleTextMessage(TextMessage message) throws Exception {
		handleTextMessage(message, false);
	}
	
	/**
	 * Handles Text messages by forwarding them to the proper ArrayBlockingQueue
	 * Throws an exception if you receive a message for which you do not
	 * currently have a chat open.
	 * @param message The message being sent
	 * @param notice If this is a notice from the server or a message from a user
	 * @throws Exception
	 */
	public static void handleTextMessage(TextMessage message, boolean notice) throws Exception {
		if (chats.get(message.getRoomID()) != null) {
			chats.get(message.getRoomID()).handleTextMessage(message, notice);
		} else {
			throw new RuntimeException("Received a text message but did not have the room for it");
		}
	}
	
	/**
	 * Handles notices that the server sends to be displayed
	 * @param message
	 * @throws Exception
	 */
	public static void handleNoticeMessage(NoticeMessage message) throws Exception {
		handleTextMessage(new TextMessage(message.getFromUsername(), message.getRoomID(), message.getNotice()), true);
	}
	
	/**
	 * Handles messages that are sent to update typing status (typing status is in the title bar)
	 * @param message The typing message
	 * @throws Exception
	 */
	public static void handleTypingMessage(TypingMessage message) throws Exception {
		try {
			chats.get(message.getRoomID()).setStatus(message);
		} catch (NullPointerException e) {
			throw new Exception("handleTypingMessage: message received from person not connected to");
		}
	}
	
	/**
	 * @return the queue that is used to send messages to the server via SendToServerConnection
	 */
	public static ArrayBlockingQueue<Message> getQueue() {
		return queue;
	}

	/**
	 * @return the convoGUI chats that this client has open
	 */
	public static ConcurrentHashMap<Integer, ConvoGUI> getChats() {
		return chats;
	}

	/**
	 * @return the budy list
	 */
	public static BuddyList getBuddyList() {
		return buddyList;
	}

	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loginGui = new LoginGUI();
				loginGui.setVisible(true);
			}
		});
	}	
}
