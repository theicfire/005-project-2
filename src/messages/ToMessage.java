package messages;

import java.sql.Timestamp;

public abstract class ToMessage extends Message {
	public static String patternStr = ""; //TODO
	protected String toUsername;
	public ToMessage(String fromUsername, String toUsername,
			Timestamp timestamp) {
		super(fromUsername, timestamp);
		this.toUsername = toUsername;
	}
	public ToMessage(String fromUsername, String toUsername) {
		super(fromUsername);
		this.toUsername = toUsername;
	}
	
	public String getToUsername() {
		return toUsername;
	}

	public void setToUsername(String toUsername) {
		this.toUsername = toUsername;
	}
	
	public abstract String getStringMessage();
}
