package messages;

import java.sql.Timestamp;

import Utils.Utils;

public abstract class Message {
	protected String fromUsername;
	protected Timestamp timestamp;
	
	public Message(String fromUsername, Timestamp timestamp) {
		this.fromUsername = fromUsername;
		this.timestamp = timestamp;
	}
	
	public Message(String fromUsername) {
		this.fromUsername = fromUsername;
		this.timestamp = Utils.getCurrentTimestamp();
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
	public abstract String getStringMessage();

}
