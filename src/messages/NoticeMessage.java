package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A NoticeMessage is a message very similar to a TextMessage - it is used to notify the user when another user
 * joins or leaves the chat.
 * 
 * Extends the ToMessage class, only carrying the additional information of the specific notice.
 */
public class NoticeMessage extends ToMessage {
	public static String patternStr = "NOTICE\\|([^\\|]*)\\|([^\\|]*)\\|([0-9]*)\\|([^\\|]*)\\|([^\\|]*)";
	private String notice;

	/** 
	 * Standard constructors.
	 * Uses the ToMessage constructors, but also keeps track of the notice.
	 */
	public NoticeMessage(String fromUsername, String toUsername, int roomID, String notice) {
		super(fromUsername, toUsername, roomID);
		this.notice = notice;
	}
	public NoticeMessage(String fromUsername, String toUsername, int roomID, String notice, Timestamp timestamp) {
		super(fromUsername, toUsername, roomID, timestamp);
		this.notice = notice;
	}

	
	public String getStringMessage() {
		return "NOTICE" + super.getStringMessageSuffix() + "|" + notice;
	}
	
	/** 
	 * Parses a string input into a NoticeMessage using regex.
	 * @param  input - the input (a string)
	 * @return NoticeMessage - the parsed ConnectionMessage
	 * @throws Exception if input isn't in the proper format.
	 */
	public static NoticeMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		System.out.println("compare input" + input);
		if(matcher.matches()){
			matcher.group();
			System.out.println("parse int of " + matcher.group(3));
			return new NoticeMessage(matcher.group(1),
									  matcher.group(2),
									  Integer.parseInt(matcher.group(3)),
									  matcher.group(5),
									  Timestamp.valueOf(matcher.group(4))
									  );
		}
		throw new Exception("Invalid NoticeMessage string: " + input);
	}
	
	/** 
	 * Checks if the input is a valid NoticeMessage.
	 * @param input - the input (a string)
	 * @return boolean - true if the input is valid, false otherwise.
	 */	
	public static boolean isNoticeMessage(String input){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		System.out.println("check" + input + "----" + patternStr);
		System.out.println("true/false: " + matcher.matches());
		return matcher.matches();
	}	
	
	public String getNotice() {
		return notice;
	}

	@Override
	public String toString() {
		return "NoticeMessage [getRoomID()=" + getRoomID()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}