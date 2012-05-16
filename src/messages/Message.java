package messages;

import java.sql.Timestamp;

import Utils.Utils;

public abstract class Message {
	/**
	 * This is the base class for all messages sent between servers and clients.
	 * Note that there are never any plain Messages sent, always specific types
	 * of messages such as ConnectionMessages, TextMessages, and TypingMessages.
	 * 
	 * Look more in the respective classes for more information about each. The
	 * important things to note are that each type of message will have a
	 * parseStringMethod and a
	 * getStringMethod method to aid in the translation between message and string
	 * so that we can easily send Messages between the server and client.
	 */
	public static String patternStr = ""; //TODO
	protected String fromUsername;
	protected Timestamp timestamp;
	
	/**
	 * Standard constructors.
	 */
	public Message(String fromUsername, Timestamp timestamp) {
		this.fromUsername = fromUsername.toLowerCase();
		this.timestamp = timestamp;
	}
	public Message(String fromUsername) {
		this.fromUsername = fromUsername;
		this.timestamp = Utils.getCurrentTimestamp();
	}
	
	/**
	 * Standard getters and setters
	 */
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
	
	/**
	 * Returns the message in the form of a string that can easily be 
	 * passed between the server and clients. Each type of message will
	 * have different string representations, so this method is abstract.
	 */
	public abstract String getStringMessage();
}
