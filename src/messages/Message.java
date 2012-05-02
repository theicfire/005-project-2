package messages;

import java.sql.Timestamp;

public abstract class Message {
	private String fromUsername;
	private Timestamp timestamp;
	
	public Message(String fromUsername, Timestamp timestamp) {
		super();
		this.fromUsername = fromUsername;
		this.timestamp = timestamp;
	}
	public String getFromUsername() {
		return fromUsername;
	}
	public void setFromUsername(String fromUsername) {
		this.fromUsername = fromUsername;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
