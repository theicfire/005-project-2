package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionMessage  extends Message {
	public static String patternStr = "(CONNECT|DISCONNECT)\\|([^\\|]*)\\|([^\\|]*)";
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
		return (type == types.CONNECT? "CONNECT" : "DISCONNECT") + "|" + fromUsername + "|" + timestamp.toString();
	}
	
	public static ConnectionMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()){
			matcher.group();
			return new ConnectionMessage(matcher.group(2),
										 Timestamp.valueOf(matcher.group(3)),
										 matcher.group(1) == "CONNECT" ? types.CONNECT : types.DISCONNECT);
		}
		throw new Exception("Invalid ConnectionMessage string: " + input);
	}

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
