package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
	private ServerSocket serverSocket;
	
	// TODO think about what static is going to be doing
	private static ConcurrentHashMap<String, ArrayBlockingQueue<Message>> messages
		= new ConcurrentHashMap<String, ArrayBlockingQueue<Message>>();
	private static HashMap<String, SendToClientConnection> sendThreadPool;
	private final static int MAX_CLIENTS = 1000;
	
	/**
	 * @param port
	 *            port number, requires 0 <= port <= 65535.
	 */
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		sendThreadPool = new HashMap<String, SendToClientConnection>();
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
	
	// TODO; I don't think this should be in the server
	public static boolean connect(String username, Socket socket) {
		// make a message queue for this client
		ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(MAX_CLIENTS);
		if (messages.containsKey(username)) {
			return false; // same username
		}
		messages.put(username, queue);
		
		// make a new thread to be able to send stuff to the client
		SendToClientConnection thread = new SendToClientConnection(socket, queue);
		thread.start();
		sendThreadPool.put(username, thread);
		
		// tell everyone that this user has now connected
	    sendAll(new ConnectionMessage(username, Utils.Utils.getCurrentTimestamp(), 
        		ConnectionMessage.types.CONNECT));
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
	 * Start a MinesweeperServer running on the default port.
	 */
	public static void main(String[] args) {
		try {
			Server server = new Server(PORT);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
