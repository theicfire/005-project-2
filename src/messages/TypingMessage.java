package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A TypingMessage is a message that is sent whenever the typing status of a
 * client in a chatroom changes. A TypingMessage can specify that the user is
 * currently not doing anything, that the user is currently typing, or that the
 * user has entered text. 
 * 
 * Extends the ToMessage class, only carrying the additional information of what
 * type of TypingMessage it is (NOTHING/TYPING/ENETERED).
 */
public class TypingMessage extends ToMessage {
	public static String patternStr = "TYPING\\|(NOTHING|TYPING|ENTERED)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)";
	public static enum types {NOTHING,TYPING,ENTERED};
	public TypingMessage.types type;
	
	/** 
	 * Standard constructors.
	 * Uses the Message constructors, but also keeps track of the type.
	 */
	public TypingMessage(String fromUsername, int roomID, Timestamp timestamp, TypingMessage.types type) {
		super(fromUsername, "", roomID, timestamp);
		this.type = type;
	}
	public TypingMessage(String fromUsername, int roomID, TypingMessage.types type) {
		super(fromUsername, "", roomID);
		this.type = type;
	}
	
	public String getStringMessage() {
		return ("TYPING|" + typeAsString() + "|" + fromUsername + "|" + roomID + "|" + timestamp.toString());
	}

	/**
	 * Returns the type of the message as a string.
	 */
	public String typeAsString(){
		if(type == types.NOTHING)
			return "NOTHING";
		if(type == types.TYPING)
			return "TYPING";
		if(type == types.ENTERED)
			return "ENTERED";
		return null;
	}
	
	/**
	 * Returns the type as a string to help with getStringMessage().
	 */
	public static TypingMessage.types stringAsType(String str){
		if(str.equals("NOTHING"))
			return types.NOTHING;
		if(str.equals("TYPING"))
			return types.TYPING;
		if(str.equals("ENTERED"))
			return types.ENTERED;
		return null;
	}
	
	/**
	 * Returns the type as a suffix to describe if the conversation parter is 
	 * not doing anything, typing, or has entered text. This suffix is then 
	 * appended to a base title, and set to the title in another method.
	 */
	public String toTitle() {
		if(type == types.NOTHING)
			return "";
		if(type == types.TYPING)
			return " - " + fromUsername + " is typing...";
		if(type == types.ENTERED)
			return " - " + fromUsername + " has entered text.";
		return null;
	}
	
	/** 
	 * Parses a string input into a TypingMessage using regex.
	 * @param  input - the input (a string)
	 * @return TypingMessage - the parsed ConnectionMessage
	 * @throws Exception if input isn't in the proper format.
	 */
	public static TypingMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()){
			matcher.group();
			return new TypingMessage(matcher.group(2),
									 Integer.parseInt(matcher.group(3)),
									 Timestamp.valueOf(matcher.group(4)),
									 stringAsType(matcher.group(1)));
		}
		throw new Exception("Invalid TypingMessage string: " + input);
	}
	
	/** 
	 * Checks if the input is a valid TypingMessage.
	 * @param input - the input (a string)
	 * @return boolean - true if the input is valid, false otherwise.
	 */
	public static boolean isTypingMessage(String input){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}	
	
	@Override
	public String toString() {
		return "TypingMessage [type=" + typeAsString()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	public static void main(String[] args) throws Exception{
		
	}
}
