package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import messages.ConnectionMessage;
import messages.Message;
import messages.ToMessage;
import server.ReceiveFromClientConnection;
import server.SendToClientConnection;

/**
 * Chat server runner.
 */
public class Server {

	private final static int PORT = 4444;
	private final ServerSocket serverSocket;
	
	// TODO think about what static is going to be doing
	private static ConcurrentHashMap<String, ArrayBlockingQueue<Message>> messages
		= new ConcurrentHashMap<String, ArrayBlockingQueue<Message>>();
	private static ConcurrentHashMap<String, SendToClientConnection> sendThreadPool;
	private static ConcurrentHashMap<Integer, ArrayList<String>> chatRooms
		= new ConcurrentHashMap<Integer, ArrayList<String>>();
	private final static int MAX_CLIENTS = 1000;
	
	/**
	 * @param port
	 *            port number, requires 0 <= port <= 65535.
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

			// handle the client
			// open up the receiving thread
			Thread thread = new ReceiveFromClientConnection(socket);
			thread.start();
		}
	}
	
	// TODO; I don't think this should be in the server
	public static void sendMsgToClient(ToMessage msg) {
		// needed because we are first getting something form messages and then editing that thing
		synchronized(messages) {
			ArrayBlockingQueue<Message> queue = messages.get(msg.getToUsername());
			System.out.println(messages.keySet());
			System.out.println(msg);
			System.out.println(queue);
			try {
				queue.offer(msg);
			} catch (Exception e) {
				// queue is probably null
				System.out.println("Client does not exist");
			}
		}
	}	
	
	// TODO; I don't think this should be in the server
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
						System.out.println("Client does not exist");
					}
				}
			}
		}
	}
	
	// TODO; I don't think this should be in the server
	public static boolean connect(String username, Socket socket) {
		// make a message queue for this client
		ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(MAX_CLIENTS);
		// Needed, otherwise we may add multiple of the same username
		synchronized(messages) {
			if (messages.containsKey(username)) {
				return false; // same username
			}
			messages.put(username, queue);
		}
		System.out.println("Connecting with username " + username);
		// make a new thread to be able to send stuff to the client
		SendToClientConnection thread = new SendToClientConnection(socket, queue);
		thread.start();
		sendThreadPool.put(username, thread);
		
		
		// tell everyone that this user has now connected
	    sendAll(new ConnectionMessage(username, Utils.Utils.getCurrentTimestamp(), 
        		ConnectionMessage.types.CONNECT));
	    
	    giveAllConnections(username);
	    return true;
	}
	
	// end threads and tell everyone that this user disconnected
	public static void disconnect(String username) {
		System.out.println("Disconnecting username: " + username);
		messages.remove(username);
		sendThreadPool.get(username).kill();
		sendThreadPool.remove(username);
		
		// tell everyone that this user has disconnected
	    sendAll(new ConnectionMessage(username, Utils.Utils.getCurrentTimestamp(), 
        		ConnectionMessage.types.DISCONNECT));
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

	        // tell this user ever username except it's own
	        if (! pair.getKey().equals(username)) {
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
	        	pair.getValue().offer(msg);
	        }
	    }
	}

	/**
	 * Start a ChatServer running on the default port.
	 */
	public static void main(String[] args) {
		runServer();
	}
	
	public static void runServer() {
		System.out.println("Starting server at " + PORT);
		try {
			Server server = new Server(PORT);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ConcurrentHashMap<Integer,ArrayList<String>> getChatRooms() {
		return chatRooms;
	}
}
