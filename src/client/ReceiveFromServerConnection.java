package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import main.Client;
import main.Server;

import messages.*;

/**
 * Needs to both:
 * 	client, which sends messages to the server
 * 	server, which sends messages to the client
 * 	So two threads are needed?
 * @author chase
 *
 */
public class ReceiveFromServerConnection extends Thread {

	private Socket gSocket;
	public String username;
	public ReceiveFromServerConnection(Socket socket, String username) {
		this.username = username;
		System.out.println("make new obj");
		gSocket = socket;
	}
	public void run() {
		try {
			handleConnection(gSocket);
        } catch (IOException e) {
            e.printStackTrace(); // but don't terminate serve()
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
        	System.out.println("ReceiveFromServerConnection closed");
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
     * @throws Exception 
     */
    private void handleConnection(Socket socket) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        try {
        	for (String line = in.readLine(); line != null; line = in.readLine()) {
        		System.out.println("ReceiveFromServer line input: " + line);
        		handleRequest(line);
        	}
        } finally {     
        	System.out.println("connection closed");
        	in.close();
        	out.close();
        }
    }

	/**
	 * handler for client input
	 * @param input
	 * @return
	 * @throws Exception 
	 */
	private void handleRequest(String input) throws Exception {
		if (ConnectionMessage.isConnectionMessage(input)){
			ConnectionMessage message = ConnectionMessage.parseStringMessage(input);
			System.out.println(message.getStringMessage());
			Client.handleConnectionMessage(message);
		} else if (RequestMessage.isRequestMessage(input)){
			RequestMessage message =  RequestMessage.parseStringMessage(input);
			Client.handleRequestMessage(message);
		} else if (TextMessage.isTextMessage(input)){
			TextMessage message =  TextMessage.parseStringMessage(input);
			Client.handleTextMessage(message);
		} else if (input.equals("DUPLICATE_LOGIN")) {
			System.out.println("duplicate!!");
		} else if (input.equals("GOOD_LOGIN")) {
			// call login
			Client.login(username);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("hello");
//		handleRequest("REQUEST chase");
	}
}