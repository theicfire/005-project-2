package Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Server;
import messages.*;


public class Utils {
	/**
	 * Helper function to return the current time as a timestamp.
	 * Used in the creation of messages then.
	 */
	public static Timestamp getCurrentTimestamp() {
		Date date= new Date();
		return new Timestamp(date.getTime());
	}
	
	@Deprecated
	/**
	 * Helper function to parse a message from a string. Was eventually just 
	 * essentially copy pasted into the code for Server/Client.
	 * Kept just in case we want to do something with it in the future.
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static Message parseStringMessage(String input) throws Exception{
		if (ConnectionMessage.isConnectionMessage(input)){
			return ConnectionMessage.parseStringMessage(input);
		} else if (RequestMessage.isRequestMessage(input)){
			return RequestMessage.parseStringMessage(input);
		} else if (TextMessage.isTextMessage(input)){
			return TextMessage.parseStringMessage(input);
		} else if (TypingMessage.isTypingMessage(input)){
			return TypingMessage.parseStringMessage(input);
		}
		throw new Exception ("Utils.parseStringMessage: Invalid Message");
	}
}
