package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import main.Server;
import messages.Message;

/**
 * One of these per client; used to send messages to the client
 * @author chase
 */
public class SendToClientConnection extends Thread {

	private Socket gSocket;
	ArrayBlockingQueue<Message> queue;
	private boolean isKilled = false;
	private String username;
	
	/**
	 * Create a new SendToClientConnection thread with the given parameters.
	 * @param socket
	 * @param queue
	 * @param username
	 */
	public SendToClientConnection(Socket socket, ArrayBlockingQueue<Message> queue, String username) {
		gSocket = socket;
		this.queue = queue;
		this.username = username;
	}
	
	/**
	 * Kills the thread.
	 */
	public void kill() {
		isKilled = true;
	}
	
	/**
	 * Starts the thread.
	 */
	public void run() {
		try {
			handleConnection(gSocket);
        } catch (IOException e) {
            e.printStackTrace(); // but don't terminate serve()
        } catch (InterruptedException e) {
        	e.printStackTrace(); // but don't terminate serve()
        } finally {
        	Server.println("connection closed");
            try {
				gSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
    /**
     * Handle a single client connection.  Returns when client disconnects.
     * @param socket  socket where client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     * @throws InterruptedException 
     */
    private void handleConnection(Socket socket) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        try {
        	for (Message message = queue.take(); message != null; message = queue.take()) {
        		if (isKilled) {
        			break;
        		}
        		handleRequest(message, out);
        	}
        } finally {     
        	out.close();
        }
    }

	/**
	 * handler for sending messages input
	 * @param input
	 * @return
	 */
	private void handleRequest(Message message, PrintWriter out) {
		Server.println("Sending message to " + username + ": " + message.getStringMessage());
		out.println(message.getStringMessage());
	}
}