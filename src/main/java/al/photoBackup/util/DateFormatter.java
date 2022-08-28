package al.photoBackup.util;

import java.time.format.DateTimeFormatter;

public class DateFormatter {

	public static final DateTimeFormatter dateSlashFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter dateDotFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	public static final DateTimeFormatter monthYearFormat = DateTimeFormatter.ofPattern("MM/yyyy");
	public static final DateTimeFormatter timeDateFormat = DateTimeFormatter.ofPattern("hh:mm dd/MM/yyyy");

}
