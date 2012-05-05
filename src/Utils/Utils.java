package Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Server;
import messages.*;


public class Utils {
	public static Timestamp getCurrentTimestamp() {
		Date date= new Date();
		return new Timestamp(date.getTime());
	}
	
	public static Message parseStringMessage(String input) throws Exception{
		if (ConnectionMessage.isConnectionMessage(input)){
			return ConnectionMessage.parseStringMessage(input);
		} else if (RequestMessage.isRequestMessage(input)){
			return RequestMessage.parseStringMessage(input);
		} else if (TextMessage.isTextMessage(input)){
			return TextMessage.parseStringMessage(input);
		}
		throw new Exception ("Utils.parseStringMessage: Invalid Message");
	}
}
