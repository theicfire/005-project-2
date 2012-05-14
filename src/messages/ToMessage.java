package messages;

import java.sql.Timestamp;

public abstract class ToMessage extends Message {
	public static String patternStr = ""; //TODO
	protected String toUsername;
	protected int roomID;
	
	public ToMessage(String fromUsername, int roomID,
			Timestamp timestamp) {
		super(fromUsername, timestamp);
		this.roomID = roomID;
	}
	public ToMessage(String fromUsername, int roomID) {
		super(fromUsername);
		this.roomID = roomID;
	}
	
	
	public String getToUsername() {
		return toUsername;
	}
	
	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
	
	public abstract String getStringMessage();
}
