package client;

import java.util.concurrent.ArrayBlockingQueue;

import messages.*;

public class ChatWindow {
	String toUsername; // You are the toUsername... you are talking to the fromUsername - should rename these. TODO
	String fromUsername; 
	ArrayBlockingQueue<Message> queue;
	
	public ChatWindow(String toUsername, String fromUsername, ArrayBlockingQueue<Message> queue){
		this.toUsername = toUsername;
		this.fromUsername = fromUsername;
		this.queue = queue;
		this.test();
	}
	
	private void test(){
		new Thread(new Runnable() {
            public void run() {
                try {
                	for (Message message = queue.take(); message != null; message = queue.take()) {
                		System.out.println(((TextMessage) message).getFromUsername()+":"+((TextMessage) message).getText());
                	}
                } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {     
                	System.out.println("send connection closed");
                }
            }
        }, fromUsername + " " + toUsername).start();
	}
}
