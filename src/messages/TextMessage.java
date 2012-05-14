package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextMessage extends ToMessage {
	public static String patternStr = "MSG\\|([^\\|]*)\\|([^\\|]*)\\|([^\\|]*)\\|(.+)";
	private String text;

	public TextMessage(String fromUsername, int roomID,
			Timestamp timestamp, String text) {
		super(fromUsername, "", roomID, timestamp);
		this.text = text;
	}
	
	public TextMessage(String fromUsername, int roomID, String text) {
		super(fromUsername, "", roomID);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getStringMessage() {
		return "MSG|" + fromUsername + "|" + roomID + "|" + timestamp.toString() + "|" + text;
	}

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
	
	@Override
	public String toString() {
		return "TextMessage [text=" + text + ", getText()=" + getText()
				+ ", getRoomID()=" + getRoomID()
				+ ", getFromUsername()=" + getFromUsername()
				+ ", getTimestamp()=" + getTimestamp() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	public static boolean isTextMessage(String input){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
	
	public static void main(String[] args) throws Exception{
		TextMessage textMessage = new TextMessage("from",0,"text");
		System.out.println(textMessage.toString());
		System.out.println(parseStringMessage(textMessage.getStringMessage()).toString());
	}
}
