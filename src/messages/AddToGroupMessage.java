package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An AddToGroupMessage is a message that is sent from the server to a specific user,
 * and then forwarded to the proper user. When that user receives the AddToGroupMessage,
 * a new Conversation window is created for that user in a new thread.
 * 
 * Extends the ToMessage class.
 */
public class AddToGroupMessage extends ToMessage {
	public static String patternStr = "ADD\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)";

	/**
	 * Standard Constructorts
	 * @param fromUsername
	 * @param toUsername
	 * @param roomID
	 */
	public AddToGroupMessage(String fromUsername, String toUsername, int roomID) {
		super(fromUsername, toUsername, roomID);
	}
	public AddToGroupMessage(String fromUsername, String toUsername, int roomID, Timestamp timestamp) {
		super(fromUsername, toUsername, roomID, timestamp);
	}
	
	public String getStringMessage() {
		return "ADD" + super.getStringMessageSuffix();
	}
	
	/** 
	 * Parses a string input into a AddToGroupMessage using regex.
	 * @param  input - the input (a string)
	 * @return AddToGroupMessage - the parsed ConnectionMessage
	 * @throws Exception if input isn't in the proper format.
	 */
	public static AddToGroupMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()){
			matcher.group();
			return new AddToGroupMessage(matcher.group(1),
									  matcher.group(2),
									  Integer.parseInt(matcher.group(3)),
									  Timestamp.valueOf(matcher.group(4))
									  );
		}
		throw new Exception("Invalid AddToGroupMessage string: " + input);
	}
	
	/** 
	 * Checks if the input is a valid AddToGroupMessage.
	 * @param input - the input (a string)
	 * @return boolean - true if the input is valid, false otherwise.
	 */
	public static boolean isAddToGroupMessage(String input){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}	

	@Override
	public String toString() {
		return "AddToGroupMessage [getRoomID()=" + getRoomID()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}