package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
	public String username;
	public ReceiveFromClientConnection(Socket socket) {
		System.out.println("make new obj");
		gSocket = socket;
	}
	public void run() {
		try {
			handleConnection(gSocket);
        } catch (IOException e) {
            e.printStackTrace(); // but don't terminate serve()
        } finally {
        	System.out.println("connection really closed");
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
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
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
	 */
	private void handleRequest(String input) {
		if (input.indexOf(" ") == -1) {
			throw new RuntimeException("Not valid"); // TODO be better than this in error checking
		}
		String start = input.split(" ")[0];
		// MSG TO_NAME MESSAGE
		String patternStr = "MSG ([^ ]*) (.+)";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if (matcher.matches()) {
			System.out.println("found!!!");
			matcher.group();
			System.out.println("matcher: " + Utils.getCurrentTimestamp());
			Server.sendMsgToClient(new TextMessage(username, matcher.group(1), Utils.getCurrentTimestamp(), matcher.group(2)));
			return;
		}
		
		patternStr = "REQUEST ([^ ]*)";
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher(input);
		if (matcher.matches()) {
			Server.sendMsgToClient(new RequestMessage(username, matcher.group(1), Utils.getCurrentTimestamp()));
//			Server.sendMsgToClient
			return;
		}
	}
	
	public static void main(String[] args) {
		System.out.println("hello");
//		handleRequest("REQUEST chase");
	}
}