package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ui.LoginGUI;
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
	private LoginGUI loginGui;
	public ReceiveFromServerConnection(Socket socket, String username, LoginGUI loginGui) {
		this.username = username;
		gSocket = socket;
		this.loginGui = loginGui;
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
        	loginGui.showErrorPopup("Internet connection lost");
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
     * @throws Exception 
     */
    private void handleConnection(Socket socket) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
        	for (String line = in.readLine(); line != null; line = in.readLine()) {
        		System.out.println("ReceiveFromServer line input to " + username + ": " + line);
        		handleRequest(line);
        	}
        } finally {     
        	in.close();
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
		} else if (AddToGroupMessage.isAddToGroupMessage(input)){
			AddToGroupMessage message =  AddToGroupMessage.parseStringMessage(input);
			Client.handleAddToGroupMessage(message);
		} else if (NoticeMessage.isNoticeMessage(input)){
			NoticeMessage message =  NoticeMessage.parseStringMessage(input);
			Client.handleNoticeMessage(message);
		} else if (TextMessage.isTextMessage(input)){
			TextMessage message =  TextMessage.parseStringMessage(input);
			Client.handleTextMessage(message);
		} else if (TypingMessage.isTypingMessage(input)){
			TypingMessage message =  TypingMessage.parseStringMessage(input);
			Client.handleTypingMessage(message);
		} else if (input.equals("DUPLICATE_LOGIN")) {
			Client.loginGui.duplicateLogin();
		} else if (input.equals("GOOD_LOGIN")) {
			// call login
			Client.loginGui.dispose();
			Client.login(username);
		} else {
//			throw new Exception("Could not parse the sent message: " + input);
			System.out.println("could not parse: " + input);
		}
	}
	
}