package messages;

import java.sql.Timestamp;

public class RequestMessage extends ToMessage {
	private String toUsername;
	public RequestMessage(String fromUsername, String toUsername,
			Timestamp timestamp) {
		super(fromUsername, toUsername, timestamp);
	}
	public RequestMessage(String fromUsername, String toUsername) {
		super(fromUsername, toUsername);
	}
	
	public String getStringMessage() {
		return "Use protocol, but this is a request message with fromUsername" + fromUsername;
	}
}