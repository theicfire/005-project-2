package messages;

import java.sql.Timestamp;

public class ToMessage extends Message {
	private String toUsername;
	public ToMessage(String fromUsername, String toUsername,
			Timestamp timestamp) {
		super(fromUsername, timestamp);
		this.toUsername = toUsername;
	}
	
	public String getToUsername() {
		return toUsername;
	}

	public void setToUsername(String toUsername) {
		this.toUsername = toUsername;
	}
}
