package server;
import java.util.ArrayList;
import java.util.List;

import messages.*;

public class Conversation {
	private final List<String> usernamess = new ArrayList<String>();
	private final ArrayList<Message> messages = new ArrayList<Message>();
	
	public Conversation(String user1, String user2){
		usernamess.add(user1);
		usernamess.add(user2);
	}

	public boolean addUser(String user){
		if(!usernamess.contains(user)){
			usernamess.add(user);
			return true;
		}
		return false;
	}
	public boolean addMessage(Message message){
		try {
			messages.add(message);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public List<String> getUserIDs(){
		List<String> clone = new ArrayList<String>();
		for (String usernames : usernamess) {
			clone.add(usernames);
		}
		return clone;
	}
	
	public List<Message> getMessages(){
		List<Message> clone = new ArrayList<Message>();
		for (Message message : messages) {
			clone.add(message);
		}
		return clone;
	}
}
