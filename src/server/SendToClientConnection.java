package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import messages.Message;

/**
 * Needs to both:
 * 	client, which sends messages to the server
 * 	server, which sends messages to the client
 * 	So two threads are needed?
 * @author chase
 *
 */
public class SendToClientConnection extends Thread {

	private Socket gSocket;
	ArrayBlockingQueue<Message> queue;
	private boolean isKilled = false;
	public SendToClientConnection(Socket socket, ArrayBlockingQueue<Message> queue) {
		System.out.println("Making sendToClient obj");
		gSocket = socket;
		this.queue = queue;
	}
	public void kill() {
		isKilled = true;
	}
	public void run() {
		try {
			handleConnection(gSocket);
        } catch (IOException e) {
            e.printStackTrace(); // but don't terminate serve()
        } catch (InterruptedException e) {
        	e.printStackTrace(); // but don't terminate serve()
        } finally {
        	System.out.println("connection really closed");
            try {
            	System.out.println("actually closing here");
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
        out.println("Success! Start chatting.");
        
        try {
        	for (Message message = queue.take(); message != null; message = queue.take()) {
        		if (isKilled) {
        			break;
        		}
        		
        		handleRequest(message, out);
        	}
        } finally {     
        	System.out.println("send connection closed");
        	out.close();
        }
    }

	/**
	 * handler for client input
	 * @param input
	 * @return
	 */
	private static void handleRequest(Message message, PrintWriter out) {
		out.println(message.getStringMessage());
	}
}