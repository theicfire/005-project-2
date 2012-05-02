package Utils;

import java.sql.Timestamp;
import java.util.Date;

public class Utils {
	public static Timestamp getCurrentTimestamp() {
		Date date= new Date();
		return new Timestamp(date.getTime());
	}

}
