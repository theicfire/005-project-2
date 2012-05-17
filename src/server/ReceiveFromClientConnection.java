package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import main.Server;

import messages.*;

/**
 * {@link ReceiveFromClientConnection} and {@link ReceiveFromServerConnection} are both very similar in their nature.
 * {@link ReceiveFromClientConnection} is initiated in {@link Server} and handles all of the message from the clients. 
 * The messages are read in by {@link handleConnection}, and forwarded to {@link handleRequest}. {@link HandleRequest} 
 * in turn checks for which type of message it is and calls the corresponding message in {@link Server} (i.e. 
 * {@link Server.handleConnectionMessage}). 
 */
public class ReceiveFromClientConnection extends Thread {
	private Socket gSocket;
	private String username = null;
	
	/**
	 * Constructor - only requires you to pass in the socket which you wish to use.
	 * @param socket
	 */
	public ReceiveFromClientConnection(Socket socket) {
		gSocket = socket;
	}
	
	/**
	 * Starts running a receiving thread for the server - uses handleConnection to handle the connection.
	 */
	public void run() {
		try {
			handleConnection(gSocket);
        } catch (IOException e) {
        	// a person disconnected; this is fine
        } finally {
        	Server.println("Server: connection really closed");
            try {
				gSocket.close();
				Server.disconnect(username);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
    /**
     * Handle the receiving of messages from all the clients. If the server disconnects, it will return.
     * Passes the lines into handleRequest to be parsed, etc.
     * @param socket  socket where clients are connected.
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
        	for (String line = in.readLine(); line != null; line = in.readLine()) {
        		Server.println("Got message: " + line + " current username is " + username);
        		if (username == null) {
        			if (Server.connect(line, socket)) {
        				username = line;
        				out.println("GOOD_LOGIN");
        				// stuff that needs to happen after GOOD_LOGIN is sent
        				Server.finishConnect(line, socket);
        			} else {
        				out.println("DUPLICATE_LOGIN");
        			}
        		} else {
	        		try {
						handleRequest(line);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
        	}
        } finally {     
        	in.close();
        	out.close();
        }
    }

	/**
	 * handler for client input - just calls the appropriate method in Server.
	 * @param input
	 */
	private void handleRequest(String input) throws Exception {
		// MSG TO_NAME MESSAGE
		if (TextMessage.isTextMessage(input)) {
			TextMessage message = TextMessage.parseStringMessage(input);
			Server.handleTextMessage(message);
		} else if (AddToGroupMessage.isAddToGroupMessage(input)) {
			AddToGroupMessage message = AddToGroupMessage.parseStringMessage(input);
			Server.handleAddToGroupMessage(message);
		} else if (TypingMessage.isTypingMessage(input)) {
			TypingMessage message = TypingMessage.parseStringMessage(input);
			Server.handleTypingMessage(message);
		} else if (NoticeMessage.isNoticeMessage(input)) {
			NoticeMessage message = NoticeMessage.parseStringMessage(input);
			Server.handleNoticeMessage(message);
		}
	}
}