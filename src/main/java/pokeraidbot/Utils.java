package pokeraidbot;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String printTime(LocalTime time) {
        return time.format(dateTimeFormatter);
    }
}
