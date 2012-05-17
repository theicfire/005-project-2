package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Utils.Utils;

import main.Client;
import main.Server;

import messages.*;

/**
 * {@link ReceiveFromClientConnection} and {@link ReceiveFromServerConnection} are both very similar in their nature.
 * {@link ReceiveFromClientConnection} is initiated in {@link Server} and handles all of the message from the clients. 
 * The messages are read in by {@link handleConnection}, and forwarded to {@link handleRequest}. {@link HandleRequest} 
 * in turn checks for which type of message it is and handles the message appropriately.
 */
public class ReceiveFromClientConnection extends Thread {

	private Socket gSocket;
	private String username = null;
	public ReceiveFromClientConnection(Socket socket) {
		gSocket = socket;
	}
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
     * Handle a single client connection.  Returns when client disconnects.
     * @param socket  socket where client is connected
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
	 * handler for client input
	 * @param input
	 * @return
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
			NoticeMessage msg = NoticeMessage.parseStringMessage(input);

		}
	}
}