package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import ui.BuddyList;
import ui.LoginGUI;

import messages.*;
import client.*;

import client.ReceiveFromServerConnection;

/**
 * GUI chat client runner.
 */
public class Client {
	private static ConcurrentHashMap<String, ArrayBlockingQueue<Message>> chats
		= new ConcurrentHashMap<String, ArrayBlockingQueue<Message>>();
	private String username;
	private static BuddyList buddyList;
	
	public Client(String username) { // TODO Bad for 005 yo!
		//Make a login screen, which for now will just print it out, but will eventually call .login(String username)
		// For now....

		this.username = username;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// prompt the user to enter their name
		Socket socket;
		try {
			socket = new Socket("localhost", 4444);
			// probably won't use this variable; everything happens inside of the thread
			
			ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1000);
			SendToServerConnection sender = new SendToServerConnection(socket, queue, username);
			sender.start();
			ReceiveFromServerConnection receiver = new ReceiveFromServerConnection(socket, username);
			receiver.start();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void login(String username){
        buddyList = new BuddyList(username);
        buddyList.setVisible(true); 
	}
	
	
	
	/* Handles connection messages by calling loggedIn/loggedOut.
	 */
	public static void handleConnectionMessage(ConnectionMessage message) {
		try{
			if (message.type == ConnectionMessage.types.CONNECT){
				buddyList.buddyLogin(message.getFromUsername());
				
			} else if (message.type == ConnectionMessage.types.DISCONNECT){
				buddyList.buddyLogout(message.getFromUsername());
			}
			updateOnline();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/* Handles Request messages by calling acceptRequest.
	 * If the request is accepted, it loggedIn/loggedOut.
	 */
	public static void handleRequestMessage(final RequestMessage message) {
		new Thread(new Runnable() {
            public void run() {
                if(!chats.containsKey(message.getFromUsername()) && acceptRequest(message)){
                	ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(0);
                	chats.put(message.getFromUsername(),queue);
                	//Open up a new chat window!
                	ChatWindow chatWindow = new ChatWindow(message.getToUsername(), message.getFromUsername(),queue);
                }
                //Need to remove users from chats when they stop talking - TODO
            }
        }, "Chat Request from " + message.getFromUsername()).start();
	}
	
	/* Handles Text messages by forwarding them to the proper ArrayBlockingQueue
	 * Throws an exception if you receive a message for which you do not currently have a chat open.
	 */
	public static void handleTextMessage(TextMessage message) throws Exception {
		try{
			chats.get(message.getFromUsername()).offer(message);
		} catch (NullPointerException e) {
			throw new Exception("handleTextMessage: message received from person not connected to");
		}
	}

	private static void updateOnline() {
		// TODO Auto-generated method stub
	}
	
	/* Called by handleRequestMessage, when a user requests to start a chat, and a REQUEST
	 * is sent to the client from the server. Opens up a popup window seeing if they want to
	 * accept the chat?... Up do you, Sebastian - TODO
	 * @returns true if the user wishes to accept the chat, and false otherwise.
	 */
	public static boolean acceptRequest(RequestMessage message){
		//TODO: Make a pop-up window?...
		return true;
	}
	
	/**
	 * Start a GUI chat client.
	 */
	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LoginGUI main = new LoginGUI();
				main.setVisible(true);
			}
		});
	}
}
