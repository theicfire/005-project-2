package messages;

import java.sql.Timestamp;

public class ConnectionMessage  extends Message {
	public static enum types {CONNECT, DISCONNECT};
	public ConnectionMessage.types type;
	public ConnectionMessage(String fromUsername, Timestamp timestamp, ConnectionMessage.types type) {
		super(fromUsername, timestamp);
		this.type = type;
	}
	
	public ConnectionMessage(String fromUsername, ConnectionMessage.types type) {
		super(fromUsername);
		this.type = type;
	}
	
	public String getStringMessage() {
		return "Use protocol, but this is a connection message with type" + type;
	}
}
