package messages;

import java.sql.Timestamp;

public abstract class ToMessage extends Message {
	public static String patternStr = ""; //TODO
	protected String toUsername;
	protected int roomID;
	
	public ToMessage(String fromUsername, String toUsername, int roomID,
			Timestamp timestamp) {
		super(fromUsername, timestamp);
		this.roomID = roomID;
		this.toUsername = toUsername;
	}
	public ToMessage(String fromUsername, String toUsername, int roomID) {
		super(fromUsername);
		this.roomID = roomID;
		this.toUsername = toUsername;
	}
	

	public String getStringMessageSuffix(){
		return "|" + fromUsername + "|" + toUsername + "|" + roomID + "|" + timestamp.toString();
	}
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
