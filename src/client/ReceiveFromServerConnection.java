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
	public ReceiveFromServerConnection(Socket socket) {
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
				
				Server.disconnect(username);
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
        out.println("Welcome to the chat server. Please enter your username");
        try {
        	for (String line = in.readLine(); line != null; line = in.readLine()) {
        		if (username == null) {
        			if (Server.connect(line, socket)) {
        				username = line;	
        			} else {
        				out.println("Name already taken; please choose a different name");
        			}
        		} else {
	        		System.out.println("reading line" + line);
	        		handleRequest(line);
        		}
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
			Client.handleConnectionMessage(message);
		} else if (RequestMessage.isRequestMessage(input)){
			RequestMessage message =  RequestMessage.parseStringMessage(input);
			Client.handleRequestMessage(message);
		} else if (TextMessage.isTextMessage(input)){
			TextMessage message =  TextMessage.parseStringMessage(input);
			Client.handleTextMessage(message);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("hello");
//		handleRequest("REQUEST chase");
	}
}