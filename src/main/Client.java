package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.BuddyList;
import ui.ConvoGUI;
import ui.DialogGUI;
import ui.LoginGUI;

import messages.*;
import client.*;

import client.ReceiveFromServerConnection;

/**
 * GUI chat client runner.
 */
public class Client {
	private static ConcurrentHashMap<Integer, ConvoGUI> chats = new ConcurrentHashMap<Integer, ConvoGUI>();
	private String username;
	private static BuddyList buddyList;
	private static ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(
			1000);

	public Client(String username, String host, String port) throws UnknownHostException, IOException { // TODO Bad for 005 yo!
		// Make a login screen, which for now will just print it out, but will
		// eventually call .login(String username)
		// For now....

		int portNum = Integer.parseInt(port);
		
		this.username = username;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// prompt the user to enter their name
		Socket socket;
		socket = new Socket(host, portNum);
		// probably won't use this variable; everything happens inside of
		// the thread
		SendToServerConnection sender = new SendToServerConnection(socket,
				queue, username);
		sender.start();
		ReceiveFromServerConnection receiver = new ReceiveFromServerConnection(
				socket, username);
		receiver.start();
	}

	public static void login(String username) {
		buddyList = new BuddyList(username);
		buddyList.setVisible(true);
	}

	/*
	 * Handles connection messages by calling loggedIn/loggedOut.
	 */
	public static void handleConnectionMessage(ConnectionMessage message) {
		try {
			if (message.type == ConnectionMessage.types.CONNECT) {
				buddyList.buddyLogin(message.getFromUsername());

			} else if (message.type == ConnectionMessage.types.DISCONNECT) {
				buddyList.buddyLogout(message.getFromUsername());
			}
			updateOnline();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Handles Request messages by calling acceptRequest. If the request is
	 * accepted, it loggedIn/loggedOut.
	 */
	public static void handleRequestMessage(final RequestMessage message) {
		if (!chats.containsKey(message.getRoomID())){
			if (message.type == RequestMessage.types.REQUEST) {
				if (acceptRequest(message)) {
					// ArrayBlockingQueue<Message> queue = new
					// ArrayBlockingQueue<Message>(100);
					// Open up a new chat window!
					ConvoGUI convoGUI = new ConvoGUI(message.getToUsername(), message.getRoomID());
					chats.put(message.getRoomID(),convoGUI);
//					convoGUI.setVisible(true); // will be set to true when other person talks first
				} else {
					// send the requester that you have rejected them
					// we are switching the order of from/to on purpose to send the message back
					Client.getQueue().offer(
							new RequestMessage(message.getToUsername(), message.getFromUsername(), message.getRoomID(),
									RequestMessage.types.REJECT_REQUEST));
				}
			} else {
				// you have requested someone to chat but they rejected you :(
				// TODO
				chats.get(message.getRoomID()).dispose();
			}
		}
	}

	/*
	 * Handles Text messages by forwarding them to the proper ArrayBlockingQueue
	 * Throws an exception if you receive a message for which you do not
	 * currently have a chat open.
	 */
	public static void handleTextMessage(TextMessage message) throws Exception {
		try {
			System.out.println("looking for : " + message.getRoomID());
			System.out.println("found: " + chats.get(message.getRoomID()));
			chats.get(message.getRoomID()).handleTextMessage(message);
		} catch (NullPointerException e) {
			throw new Exception(
					"handleTextMessage: message received from person not connected to");
		}
	}
	
	/*
	 * TODO - write shit
	 */
	public static void handleTypingMessage(TypingMessage message) throws Exception {
		try {
			System.out.println("looking for : " + message.getFromUsername());
			System.out.println("found: " + chats.get(message.getRoomID()));
			chats.get(message.getRoomID()).setStatus(message);
		} catch (NullPointerException e) {
			throw new Exception(
					"handleTypingMessage: message received from person not connected to");
		}
	}
	
	private static void updateOnline() {
		// TODO Auto-generated method stub
	}

	/*
	 * Called by handleRequestMessage, when a user requests to start a chat, and
	 * a REQUEST is sent to the client from the server. Opens up a popup window
	 * seeing if they want to accept the chat?... Up do you, Sebastian - TODO
	 * 
	 * @returns true if the user wishes to accept the chat, and false otherwise.
	 */
	public static boolean acceptRequest(RequestMessage message) {
		// This makes a popup dialog; disable for now
		if (true) {
			return true;
		}
		DialogGUI dialog = new DialogGUI();
		return dialog.makeDialog();
	}

	/**
	 * Start a GUI chat client.
	 */

	public static ArrayBlockingQueue<Message> getQueue() {
		return queue;
	}

	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LoginGUI main = new LoginGUI();
				main.setVisible(true);
//				new Client("thomas" + (int) (Math.random() * 100));
			}
		});
	}

	public static ConcurrentHashMap<Integer, ConvoGUI> getChats() {
		return chats;
	}
}
