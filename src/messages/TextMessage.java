package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A ConnectionMessage is a message that is sent from the user to the server,
 * and then propagated to other users in the room specified by roomID.
 * 
 * Extends the ToMessage class, only carrying the additional information of
 * what text the message contains.
 */
public class TextMessage extends ToMessage {
	
	public static String patternStr = "MSG\\|([0-9a-zA-Z]+)\\|([0-9]+)\\|([^\\|]*)\\|(.+)";
	private String text;
	
	/** 
	 * Standard constructors.
	 * Uses the Message constructors, but also keeps track of the text.
	 */
	public TextMessage(String fromUsername, int roomID,
			Timestamp timestamp, String text) {
		super(fromUsername, "", roomID, timestamp);
		this.text = text;
	}	
	public TextMessage(String fromUsername, int roomID, String text) {
		super(fromUsername, "", roomID);
		this.text = text;
	}

	/**
	 * Standard getter/setter
	 */
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getStringMessage() {
		return "MSG|" + fromUsername + "|" + roomID + "|" + timestamp.toString() + "|" + text;
	}
	
	/** 
	 * Parses a string input into a TextMessage using regex.
	 * @param  input - the input (a string)
	 * @return TextMessage - the parsed ConnectionMessage
	 * @throws Exception if input isn't in the proper format.
	 */
	public static TextMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()){
			matcher.group();
			return new TextMessage(matcher.group(1),
								   Integer.parseInt(matcher.group(2)),
								   Timestamp.valueOf(matcher.group(3)),
								   matcher.group(4));
		}
		throw new Exception("Invalid TextMessage string: " + input);
	}
	
	/** 
	 * Checks if the input is a valid TextMessage.
	 * @param input - the input (a string)
	 * @return boolean - true if the input is valid, false otherwise.
	 */
	public static boolean isTextMessage(String input){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
	
	@Override
	public String toString() {
		return "TextMessage [text=" + text + ", getText()=" + getText()
				+ ", getRoomID()=" + getRoomID()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	public static void main(String[] args) throws Exception{
		
	}
}
