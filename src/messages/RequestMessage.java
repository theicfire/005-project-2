package messages;

import java.sql.Timestamp;

public class RequestMessage extends ToMessage {
	private String toUsername;
	public RequestMessage(String fromUsername, String toUsername,
			Timestamp timestamp) {
		super(fromUsername, toUsername, timestamp);
	}
}