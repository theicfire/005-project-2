package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddToGroupMessage extends ToMessage {
	public static String patternStr = "ADD\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)";

	public AddToGroupMessage(String fromUsername, String toUsername, int roomID, Timestamp timestamp) {
		super(fromUsername, toUsername, roomID, timestamp);
	}

	public AddToGroupMessage(String fromUsername, String toUsername, int roomID) {
		super(fromUsername, toUsername, roomID);
	}
	
	public String getStringMessage() {
		return "ADD" + super.getStringMessageSuffix();
	}
	
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