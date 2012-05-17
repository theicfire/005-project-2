package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A ConnectionMessage is a message that is sent from the server to all users
 * whenever a user connects or disconnects. If the user connects, the type of
 * the message is CONNECT. Similarly, if the user disconnects, the type of the
 * message is DISCONNECT. 
 * 
 * Extends the Message class, only carrying the additional information of what
 * type of ConnectionMessage it is (CONNECT/DISCONNECT).
 */
public class ConnectionMessage  extends Message {
	public static String patternStr = "(CONNECT|DISCONNECT)\\|([^\\|]*)\\|([^\\|]*)";
	public static enum types {CONNECT, DISCONNECT};
	public ConnectionMessage.types type;
	
	/** 
	 * Standard constructors.
	 * Uses the Message constructors, but also keeps track of the type.
	 */
	public ConnectionMessage(String fromUsername, ConnectionMessage.types type) {
		super(fromUsername);
		this.type = type;
	}
	public ConnectionMessage(String fromUsername, Timestamp timestamp, ConnectionMessage.types type) {
		super(fromUsername, timestamp);
		this.type = type;
	}
	
	public String getStringMessage() {
		return (type == types.CONNECT? "CONNECT" : "DISCONNECT") + "|" + fromUsername + "|" + timestamp.toString();
	}
	
	/** 
	 * Parses a string input into a ConnectionMessage using regex.
	 * @param  input - the input (a string)
	 * @return ConnectionMessage - the parsed ConnectionMessage
	 * @throws Exception if input isn't in the proper format.
	 */
	public static ConnectionMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()){
			matcher.group();
			return new ConnectionMessage(matcher.group(2),
										 Timestamp.valueOf(matcher.group(3)),
										 matcher.group(1).equals("CONNECT") ? types.CONNECT : types.DISCONNECT);
		}
		throw new Exception("Invalid ConnectionMessage string: " + input);
	}
	
	/** 
	 * Checks if the input is a valid ConnectionMessage.
	 * @param input - the input (a string)
	 * @return boolean - true if the input is valid, false otherwise.
	 */
	public static boolean isConnectionMessage(String input){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}	
	
	@Override
	public String toString() {
		return "ConnectionMessage [type=" + (type == types.CONNECT ? "CONNECT" : "DISCONNECT")
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
