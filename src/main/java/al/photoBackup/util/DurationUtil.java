package al.photoBackup.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class DurationUtil {

	public static int hoursBetween(LocalDateTime from, LocalDateTime to) {
		return (int) Math.abs(Duration.between(from, to).toHours());
	}

	public static int minutesBetween(LocalDateTime from, LocalDateTime to) {
		return (int) Math.abs(Duration.between(from, to).toMinutes());
	}

	public static int daysBetween(LocalDate from, LocalDate to) {
		return (int) Period.between(from, to).get(ChronoUnit.DAYS);
	}

}
