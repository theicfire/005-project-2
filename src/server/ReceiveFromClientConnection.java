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
            e.printStackTrace(); // but don't terminate serve()
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
			TextMessage msg = TextMessage.parseStringMessage(input);
			if(Server.getChatRooms().containsKey(msg.getRoomID()))
				Server.sendMsgToClients(TextMessage.parseStringMessage(input));
			else
				Server.println("Shouldn't reach here... textMessage, but no chatRoom");
			return;
		} else if (AddToGroupMessage.isAddToGroupMessage(input)) {
			AddToGroupMessage msg = AddToGroupMessage.parseStringMessage(input);
			if(Server.getChatRooms().containsKey(msg.getRoomID())){
				if (Server.getChatRooms().get(msg.getRoomID()).contains(msg.getToUsername())) {
					// this person is already added
					Server.sendMsgToClient(new NoticeMessage("server", msg.getFromUsername(), msg.getRoomID(), "Already added"));
				} else {
					Server.getChatRooms().get(msg.getRoomID()).add(msg.getToUsername());
					Server.sendMsgToClient(msg);
					Server.sendMsgToClients((new TextMessage("server", msg.getRoomID(), 
							msg.getToUsername() + " has been added by " + msg.getFromUsername())));
				}
			} else {
				ArrayList<String> clients = new ArrayList<String>();
				clients.add(msg.getFromUsername());
				clients.add(msg.getToUsername());
				Server.getChatRooms().put(msg.getRoomID(), clients);
				Server.sendMsgToClient(msg);
			}
			return;
		} else if (TypingMessage.isTypingMessage(input)) {
			TypingMessage msg = TypingMessage.parseStringMessage(input);
			if(Server.getChatRooms().containsKey(msg.getRoomID()))
				Server.sendMsgToClients(TypingMessage.parseStringMessage(input));
			return;
		} else if (NoticeMessage.isNoticeMessage(input)) {
			NoticeMessage msg = NoticeMessage.parseStringMessage(input);
			if (msg.getNotice().equals("closing")) {
				// tell the room this is closing
				Server.sendMsgToClients(new NoticeMessage("server", msg.getFromUsername(), msg.getRoomID(), msg.getFromUsername() + " left the room"));
			}
		}
	}
}