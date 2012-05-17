package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import messages.Message;
import messages.NoticeMessage;

/**
 * Waits on a blocking queue, whenever the queue adds a new message, this packages it up and sends it to the server
 * @author chase
 *
 */
public class SendToServerConnection extends Thread {

	private Socket gSocket;
	ArrayBlockingQueue<Message> queue;
	private String username;
	public SendToServerConnection(Socket socket, ArrayBlockingQueue<Message> queue, String username) {
		gSocket = socket;
		this.queue = queue;
		this.username = username;
	}
	/**
	 * Starts running a sending thread for the server - uses handleConnection to handle the connection.
	 */
	public void run() {
		try {
			handleConnection(gSocket);
        } catch (IOException e) {
            e.printStackTrace(); // but don't terminate serve()
        } catch (InterruptedException e) {
        	e.printStackTrace(); // but don't terminate serve()
        } finally {
        	System.out.println("SendToServerConnection closed");
            try {
				gSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * Disconnects the user from the server, sending a notification... ?
	 */
	public void kill() {
		queue.offer(new NoticeMessage("kill", null, 0, null));
	}
	
    /**
     * Waits for messages from a queue and sends the packaged message to the server 
     * @param socket  socket where client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     * @throws InterruptedException 
     */
    private void handleConnection(Socket socket) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);        
        try {
        	// initial login passing to server
        	out.println(username);
        	for (Message message = queue.take(); message != null && !message.getFromUsername().equals("kill"); message = queue.take()) {
        		out.println(message.getStringMessage());
        	}
        } finally {     
        	System.out.println("send connection closed");
        	out.close();
        }
    }
}