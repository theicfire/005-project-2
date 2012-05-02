package messages;

import java.sql.Timestamp;

public class TextMessage extends ToMessage {

	private String text;

	public TextMessage(String fromUsername, String toUsername,
			Timestamp timestamp, String text) {
		super(fromUsername, toUsername, timestamp);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
