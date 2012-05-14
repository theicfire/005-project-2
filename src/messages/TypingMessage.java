package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypingMessage extends ToMessage {
	public static String patternStr = "TYPING\\|(NOTHING|TYPING|ENTERED)\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)";
	public static enum types {NOTHING,TYPING,ENTERED};
	public TypingMessage.types type;
	
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

	public String typeAsString(){
		if(type == types.NOTHING)
			return "NOTHING";
		if(type == types.TYPING)
			return "TYPING";
		if(type == types.ENTERED)
			return "ENTERED";
		return null;
	}
	
	public static TypingMessage.types stringAsType(String str){
		if(str.equals("NOTHING"))
			return types.NOTHING;
		if(str.equals("TYPING"))
			return types.TYPING;
		if(str.equals("ENTERED"))
			return types.ENTERED;
		return null;
	}
	
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
		TypingMessage TypingMessage = new TypingMessage("from",0,types.TYPING);
		System.out.println(TypingMessage.toString());
		System.out.println(TypingMessage.getStringMessage());
		System.out.println(parseStringMessage(TypingMessage.getStringMessage()).toString());
	}

	public String toTitle() {
		if(type == types.NOTHING)
			return "";
		if(type == types.TYPING)
			return " - " + fromUsername + " is typing...";
		if(type == types.ENTERED)
			return " - " + fromUsername + " has entered text.";
		return null;
	}
}
