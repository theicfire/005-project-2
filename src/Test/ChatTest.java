package Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;

import main.Client;
import main.Server;
import messages.AddToGroupMessage;
import messages.ConnectionMessage;
import messages.Message;
import messages.TextMessage;
import messages.TypingMessage;

import org.junit.Test;

import client.ReceiveFromServerConnection;
import client.SendToServerConnection;

import ui.LoginGUI;
import static org.junit.Assert.*;

public class ChatTest{
	    
	    @Test(expected=AssertionError.class)
	    public void testAssertionsEnabled() {
	        assert false;
	    }

	    @Test
	    public void testSomething() {
	    	assertTrue(true);
	    }
	    
	    @Test
	    public void testServer() throws Exception {
	    	Thread server = new Thread(new Runnable() {
	            public void run() {
	            	Server.runServer();
	            }
	    	});
	    	server.start();
	    	
	    	// allow server to start before clients connect
	    	Thread.sleep(1000);
	    	
	    	// first client sender
	    	Socket socket = new Socket("localhost", 4444);
	    	ArrayBlockingQueue<Message> chaseSendQueue = new ArrayBlockingQueue<Message>(1000);
			SendToServerConnection sender = new SendToServerConnection(socket, chaseSendQueue, "chase");
			sender.start();
			
			// first client receiver
			ArrayBlockingQueue<String> chaseReceiveQueue = new ArrayBlockingQueue<String>(1000);
			TestReceiveFromServerConnection receiver = new TestReceiveFromServerConnection(socket, "chase", chaseReceiveQueue);
			receiver.start();
			
			// allow first client to connect
			Thread.sleep(1000);
			// second client sender
			Socket socket2 = new Socket("localhost", 4444);
	    	ArrayBlockingQueue<Message> tomSendQueue = new ArrayBlockingQueue<Message>(1000);
			SendToServerConnection sender2 = new SendToServerConnection(socket2, tomSendQueue, "tom");
			sender2.start();

			// get two messages on initial login
			int roomID = 22;
			assertEquals(chaseReceiveQueue.take(), "GOOD_LOGIN");
			ConnectionMessage msg = ConnectionMessage.parseStringMessage(chaseReceiveQueue.take()); // checks that this is a connectionMessage
			assertEquals(msg.getFromUsername(), "tom");
			
			// thomas sends a message to chase			
			tomSendQueue.offer(new AddToGroupMessage("tom", "chase", roomID));
			tomSendQueue.offer(new TextMessage("tom", roomID, "hello chase"));
			AddToGroupMessage msg2 = AddToGroupMessage.parseStringMessage(chaseReceiveQueue.take());
			assertEquals(msg2.getFromUsername(), "tom");
			assertEquals(msg2.getRoomID(), 22);
			TextMessage msg3 = TextMessage.parseStringMessage(chaseReceiveQueue.take());
			assertEquals(msg3.getFromUsername(), "tom");
			assertEquals(msg3.getRoomID(), 22);
			assertEquals(msg3.getText(), "hello chase");
	    }
	    
	    @Test
	    public void doLogin() throws InterruptedException {
//	    	Thread server = new Thread(new Runnable() {
//				public void run() {
//	    	Server.runServer();
//				}
//	    	});
//	    	server.start();
//	    	
//	    	new Thread(new Runnable() {
//				public void run() {
//			    	LoginGUI main = new LoginGUI();
//					main.setVisible(true);
//					main.testSetUsername("Chase");
//					main.testLogin();
//				}
//	    	}).start();
//			Thread.sleep(3000);
//	    	new Thread(new Runnable() {
//				public void run() {
//			    	LoginGUI main = new LoginGUI();
//					main.setVisible(true);
//					main.testSetUsername("Sebastian");
//					main.testLogin();
//				}
//	    	}).start();
//	    	
//			
//			// TODO make sure each other is seen
//			
//			while (true) {
//				// block
//			}
	    }
	    
	    
	    
	    
	    public class TestReceiveFromServerConnection extends Thread {

	    	private Socket gSocket;
	    	public String username;
	    	public ArrayBlockingQueue<String> queue;
	    	public TestReceiveFromServerConnection(Socket socket, String username, ArrayBlockingQueue<String> queue) {
	    		this.username = username;
	    		System.out.println("make new obj");
	    		gSocket = socket;
	    		this.queue = queue;
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
	            try {
	            	for (String line = in.readLine(); line != null; line = in.readLine()) {
	            		System.out.println("client got line: " + line);
	            		queue.offer(line);
	            	}
	            } finally {     
	            	System.out.println("connection closed");
	            	in.close();
	            }
	        }
	    }
	    
}