package messages;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoticeMessage extends ToMessage {
	public static String patternStr = "NOTICE\\|([^\\|]*)\\|([^\\|]*)\\|([0-9]*)\\|([^\\|]*)\\|([^\\|]*)";
	private String notice;

	public NoticeMessage(String fromUsername, String toUsername, int roomID, String notice, Timestamp timestamp) {
		super(fromUsername, toUsername, roomID, timestamp);
		this.notice = notice;
	}

	public NoticeMessage(String fromUsername, String toUsername, int roomID, String notice) {
		super(fromUsername, toUsername, roomID);
		this.notice = notice;
	}
	
	public String getStringMessage() {
		return "NOTICE" + super.getStringMessageSuffix() + "|" + notice;
	}
	
	public static NoticeMessage  parseStringMessage(String input) throws Exception {
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
	
	public static void main(String[] args) throws Exception {
		System.out.println("ehy");
		NoticeMessage n = new NoticeMessage("from", "to", 222, "my notice");
		System.out.println(NoticeMessage.parseStringMessage(n.getStringMessage()).getStringMessage());
	}
}