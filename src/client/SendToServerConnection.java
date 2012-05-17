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
 * Needs to both:
 * 	client, which sends messages to the server
 * 	server, which sends messages to the client
 * 	So two threads are needed?
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
	public void kill() {
		queue.offer(new NoticeMessage("kill", null, 0, null));
	}
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
     * Handle a single client connection.  Returns when client disconnects.
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
        		handleRequest(message, out);
        	}
        } finally {     
        	System.out.println("send connection closed");
        	out.close();
        }
    }

	/**
	 * Sends the information to the server
	 * @param message what to send
	 * @param out what to write to to send the message
	 */
	private static void handleRequest(Message message, PrintWriter out) {
		out.println(message.getStringMessage());
	}
}