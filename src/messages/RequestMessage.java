package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
/**
 * Now deprecated, a Request Message was originally sent whenever a user wanted to initate a chat with another user.
 * It has now been replaced by AddToGroupMessages and NoticeMessages.
 * 
 * We original included RequestMessage because users were to be able to reject chat invites, but now we just have
 * the user automatically accept the requests, so RequestMessages aren't necessary. We are keeping them so that 
 * if, in the future, we want to implement adding people as friends, we could do that through RequestMessages.
 * 
 * Extends the ToMessage class, keeping the additional information of what type of RequestMessage it is.
 */
public class RequestMessage extends ToMessage {
	public static String patternStr = "(REQUEST|REJECT_REQUEST)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)";
	public RequestMessage.types type;
	public enum types {
		REQUEST,
		REJECT_REQUEST
	}
	public RequestMessage(String fromUsername, String toUsername, int roomID, RequestMessage.types type,
			Timestamp timestamp) {
		super(fromUsername, toUsername, roomID, timestamp);
		this.type = type;
	}
	public RequestMessage(String fromUsername, String toUsername, int roomID, RequestMessage.types type) {
		super(fromUsername, toUsername, roomID);
		this.type = type;
	}
	
	public String getStringMessage() {
		return ((type == types.REQUEST) ? "REQUEST" : "REJECT") + super.getStringMessageSuffix();
	}
	
	public static RequestMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()){
			matcher.group();
			return new RequestMessage(matcher.group(2),
									  matcher.group(3),
									  Integer.parseInt(matcher.group(4)),
									  matcher.group(1).equals("REQUEST") ? 
											  RequestMessage.types.REQUEST : RequestMessage.types.REJECT_REQUEST,
									  Timestamp.valueOf(matcher.group(5))
									  );
		}
		throw new Exception("Invalid RequestMessage string: " + input);
	}
	
	public static boolean isRequestMessage(String input){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}	

	@Override
	public String toString() {
		return "RequestMessage [getRoomID()=" + getRoomID()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	public static void main(String[] args) throws Exception{
		RequestMessage requestMessage = new RequestMessage("from", "to", 0, RequestMessage.types.REQUEST);
		System.out.println(requestMessage.toString());
		System.out.println(parseStringMessage(requestMessage.getStringMessage()).toString());
	}
}