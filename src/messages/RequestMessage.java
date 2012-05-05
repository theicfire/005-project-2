package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestMessage extends ToMessage {
	public static String patternStr = "REQUEST\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)";
	public RequestMessage(String fromUsername, String toUsername,
			Timestamp timestamp) {
		super(fromUsername, toUsername, timestamp);
	}
	public RequestMessage(String fromUsername, String toUsername) {
		super(fromUsername, toUsername);
	}
	
	public String getStringMessage() {
		return "REQUEST|" + fromUsername + "|" + toUsername + "|" + timestamp.toString();
	}
	
	public static RequestMessage parseStringMessage(String input) throws Exception {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()){
			matcher.group();
			return new RequestMessage(matcher.group(1),
									  matcher.group(2),
									  Timestamp.valueOf(matcher.group(3)));
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
		return "RequestMessage [getToUsername()=" + getToUsername()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	public static void main(String[] args) throws Exception{
		RequestMessage requestMessage = new RequestMessage("from","to");
		System.out.println(requestMessage.toString());
		System.out.println(parseStringMessage(requestMessage.getStringMessage()).toString());
	}
}