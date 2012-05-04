package messages;

import java.sql.Timestamp;

public class TextMessage extends ToMessage {

	private String text;

	public TextMessage(String fromUsername, String toUsername,
			Timestamp timestamp, String text) {
		super(fromUsername, toUsername, timestamp);
		this.text = text;
	}
	
	public TextMessage(String fromUsername, String toUsername, String text) {
		super(fromUsername, toUsername);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getStringMessage() {
		return "Use protocol, but this is a text message with text" + text;
	}

	@Override
	public String toString() {
		return "TextMessage [text=" + text + ", getText()=" + getText()
				+ ", getToUsername()=" + getToUsername()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
	

}
