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
}
