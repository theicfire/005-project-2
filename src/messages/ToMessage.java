package messages;

import java.sql.Timestamp;

/**
 * A ToMessage is simply a Message that is sent from one user to another, or from one user to a room.
 * This is compared to Messages, which are sent from the server to specific user (such as ConnectionMessages).
 * 
 * Extends Message, also keeping track of the toUsername and roomID
 */
public abstract class ToMessage extends Message {
	public static String patternStr = ""; //TODO
	protected String toUsername;
	protected int roomID;
	
	/**
	 * Standard Constructors. 
	 * Users the Message constructors, but also keeps track of toUsername and roomID.
	 */
	public ToMessage(String fromUsername, String toUsername, int roomID,
			Timestamp timestamp) {
		super(fromUsername, timestamp);
		this.roomID = roomID;
		this.toUsername = toUsername.toLowerCase();
	}
	public ToMessage(String fromUsername, String toUsername, int roomID) {
		super(fromUsername);
		this.roomID = roomID;
		this.toUsername = toUsername;
	}
	
	/**
	 * Simply a helper function for getStringMessage().
	 */
	public String getStringMessageSuffix(){
		return "|" + fromUsername + "|" + toUsername + "|" + roomID + "|" + timestamp.toString();
	}
	
	/**
	 * Standard getters/setters
	 */
	public String getToUsername() {
		return toUsername;
	}
	public void setToUsername(String toUsername) {
		this.toUsername = toUsername;
	}
	public int getRoomID() {
		return roomID;
	}
	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
	
	public abstract String getStringMessage();
}
