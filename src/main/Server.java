package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import messages.*;

import server.ReceiveFromClientConnection;
import server.SendToClientConnection;

/**
 * Chat server runner. This is where the magic happens! When you start a new Client, you actually first
 * create a new {@link LoginGUI}, and then based off of the results of that, start up a new {@link BuddyList}
 * and new {@link ReceiveFromClientConnection} and {@link SendToClientConnection} threads.
 */
public class Server {

	private final static int PORT = 4444;
	private final ServerSocket serverSocket;
	
	private static ConcurrentHashMap<String, ArrayBlockingQueue<Message>> messages
		= new ConcurrentHashMap<String, ArrayBlockingQueue<Message>>();
	private static ConcurrentHashMap<String, SendToClientConnection> sendThreadPool;
	private static ConcurrentHashMap<Integer, ArrayList<String>> chatRooms
		= new ConcurrentHashMap<Integer, ArrayList<String>>();
	private final static int MAX_CLIENTS = 1000;
	
	/**
	 * @param port port number, requires 0 <= port <= 65535.
	 */
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		sendThreadPool = new ConcurrentHashMap<String, SendToClientConnection>();
	}

	/**
	 * Run the server, listening for client connections and handling them. Never
	 * returns unless an exception is thrown.
	 * 
	 * @throws IOException
	 *             if the main server socket is broken (IOExceptions from
	 *             individual clients do *not* terminate serve()).
	 */
	private void serve() throws IOException {
		while (true) {
			// block until a client connects
			Socket socket = serverSocket.accept();

			// open up the receiving thread
			Thread thread = new ReceiveFromClientConnection(socket);
			thread.start();
		}
	}

	/**
	 * Sends a message to the client specified in the message
	 * @param msg the message to send
	 */
	public static void sendMsgToClient(ToMessage msg) {
		// needed because we are first getting something form messages and then editing that thing
		synchronized(messages) {
			ArrayBlockingQueue<Message> queue = messages.get(msg.getToUsername());
			try {
				queue.offer(msg);
			} catch (Exception e) {
				// queue is probably null
				Server.println("Client does not exist");
			}
		}
	}	
	

	/**
	 * Sends a message to all the clients in the group specified by the message
	 * @param msg the message to send
	 */
	public static void sendMsgToClients(ToMessage msg) {
		synchronized(messages) {
			ArrayList<String> clients = chatRooms.get(msg.getRoomID());
			for(String client:clients){
				if(!msg.getFromUsername().equals(client)){
					ArrayBlockingQueue<Message> queue = messages.get(client);
					try {
						queue.offer(msg);
					} catch (Exception e) {
						// queue is probably null
						Server.println("Client does not exist");
					}
				}
			}
		}
	}

	/**
	 * Changes server state to have a new client
	 * @param username
	 * @param socket
	 * @return
	 */
	public static boolean connect(String username, Socket socket) {
		Server.println("Attempt to connect with" + username);
		// make a message queue for this client
		ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1000);
		// Needed, otherwise we may add multiple of the same username
		synchronized(messages) {
			if (messages.containsKey(username)) {
				return false; // same username
			}
			messages.put(username, queue);
		}
		
		// make a new thread to be able to send stuff to the client
		SendToClientConnection thread = new SendToClientConnection(socket, queue, username);
		thread.start();
		sendThreadPool.put(username, thread);
		
		// continue on in finishConnect
		
	    return true;
	}
	
	/**
	 * Finishes up changing server state for new clients; different from connect because the user must receive a message before more messages are sent.
	 * @param username
	 * @param socket
	 */
	public static void finishConnect(String username, Socket socket) {
		Server.println("Continuing to connect with username " + username);
		
		// tell everyone that this user has now connected
	    sendAll(new ConnectionMessage(username, Utils.Utils.getCurrentTimestamp(), 
        		ConnectionMessage.types.CONNECT));
	    
	    giveAllConnections(username);
	    Server.println("True; can connect with" + username);
	}
	
	// end threads and tell everyone that this user disconnected
	public static void disconnect(String username) {
		Server.println("Disconnecting username: " + username);
		messages.remove(username);
		sendThreadPool.get(username).kill();
		sendThreadPool.remove(username);
		
		// tell everyone that this user has disconnected
	    sendAll(new ConnectionMessage(username, Utils.Utils.getCurrentTimestamp(), 
        		ConnectionMessage.types.DISCONNECT));
	}
	
	public static void handleTextMessage(TextMessage message) throws Exception {
		if(chatRooms.containsKey(message.getRoomID()))
			sendMsgToClients(message);
		else
			println("Shouldn't reach here... TextMessage, but no chatRoom");
	}
	
	public static void handleAddToGroupMessage(AddToGroupMessage message) throws Exception {
		int roomID = message.getRoomID();
		String toUsername = message.getToUsername();
		String fromUsername = message.getFromUsername();
		
		if(chatRooms.containsKey(roomID)){
			if (chatRooms.get(roomID).contains(toUsername)) {
				// This person is already added
				sendMsgToClient(new NoticeMessage("server", fromUsername, roomID, "Already added"));
			} else {
				chatRooms.get(roomID).add(toUsername);
				sendMsgToClient(message);
				sendMsgToClients((new TextMessage("server", roomID, toUsername + " has been added by " + fromUsername)));
			}
		} else {
			ArrayList<String> clients = new ArrayList<String>();
			clients.add(fromUsername);
			clients.add(toUsername);
			chatRooms.put(roomID, clients);
			sendMsgToClient(message);
		}
	}
	
	/*
	 * TODO - write shit
	 */
	public static void handleTypingMessage(TypingMessage message) throws Exception {
		if(chatRooms.containsKey(message.getRoomID()))
			sendMsgToClients(message);
		else
			println("Shouldn't reach here... TypingMessage, but no chatRoom");
	}

	public static void handleNoticeMessage(NoticeMessage message) throws Exception {
		if (message.getNotice().equals("closing")) {
			// tell the room this is closing
			sendMsgToClients(new NoticeMessage("server", message.getFromUsername(), message.getRoomID(), 
																message.getFromUsername() + " left the room"));
		}
	}
	
	/**
	 * Go send the given username all the connection messages that are needed to show all the people online
	 * @param username
	 */
	private static void giveAllConnections(String username) {
		ArrayBlockingQueue<Message> queue = messages.get(username);
		if (queue == null) {
			throw new RuntimeException("Queue is null; this should never happen!");
		}
		Iterator<Entry<String, ArrayBlockingQueue<Message>>> it = messages.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, ArrayBlockingQueue<Message>> pair = 
	        		(Map.Entry<String, ArrayBlockingQueue<Message>>)it.next();

	        // tell this user every username except it's own
	        if (! pair.getKey().equals(username)) {
	        	Server.println("giveAllConnections to " + username + " with " + pair.getKey());
	        	queue.offer(new ConnectionMessage(pair.getKey(), ConnectionMessage.types.CONNECT));
	        }
	    }
	}
	
	/**
	 * Send everyone except the sender the message
	 * @param msg
	 */
	private static void sendAll(Message msg) {
		Iterator<Entry<String, ArrayBlockingQueue<Message>>> it = messages.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, ArrayBlockingQueue<Message>> pair = 
	        		(Map.Entry<String, ArrayBlockingQueue<Message>>)it.next();

	        // send the message to everyone except the sender
	        if (! pair.getKey().equals(msg.getFromUsername())) {
	        	Server.println("sendAll to " + pair.getKey() + " with " + msg);
	        	pair.getValue().offer(msg);
	        }
	    }
	}

	public static void runServer() {
		Server.println("Starting server at " + PORT);
		try {
			Server server = new Server(PORT);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Just a getter for chatRooms
	 * @return
	 */
	public static ConcurrentHashMap<Integer,ArrayList<String>> getChatRooms() {
		return chatRooms;
	}
	
	/**
	 * Helper function for debugging and logging.
	 * @param s
	 */
	public static void println(String s) {
		System.out.println("Server: " + s);
	}
	
	/**
	 * Start a ChatServer running on the default port.
	 */
	public static void main(String[] args) {
		runServer();
	}
}
