package pokeraidbot;

import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final DateTimeFormatter dateTimeParseFormatter = DateTimeFormatter.ofPattern("HH[:][.]mm");
    public static final DateTimeFormatter dateTimePrintFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String printTime(LocalTime time) {
        return time.format(dateTimePrintFormatter);
    }

    public static void assertGivenTimeNotBeforeNow(String userName, LocalTime time) {
        if (time.isBefore(LocalTime.now())) {
            throw new UserMessedUpException(userName,
                    "You seem to be living in a different timezone. Your given time is " + printTime(time) +
                            ", while the current time is " + printTime(LocalTime.now()));
        }
    }

    public static void assertTimeNotInNoRaidTimespan(String userName, LocalTime time) {
        if (time.isAfter(LocalTime.of(22, 00)) || time.isBefore(LocalTime.of(7, 0))) {
            throw new UserMessedUpException(userName,
                    "You can't create raids between 22:00 and 07:00 - your time was " + printTime(time) + ".");
        }
    }

    public static void assertTimeNotMoreThanTwoHoursFromNow(String userName, LocalTime time) {
        final LocalTime now = LocalTime.now();
        if (now.plusHours(2).isBefore(time)) {
            throw new UserMessedUpException(userName,
                    "You can't create raids which are later than 2 hours from the current time " + printTime(now) + " - your time was " + printTime(time) + ".");
        }
    }

    public static void assertEtaNotAfterRaidEnd(String userName, Raid raid, LocalTime eta) {
        if (eta.isAfter(raid.getEndOfRaid())) {
            throw new UserMessedUpException(userName,
                    "Can't arrive after raid has ended. Your given time is " + printTime(eta) +
                            ", raid ends at " + printTime(raid.getEndOfRaid()));
        }
    }

    public static String getStaticMapUrl(Gym gym) {
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + gym.getX() + "," + gym.getY() +
                "&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C" +
                gym.getX() + "," + gym.getY() + "&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU";
        return url;
    }

    public static String getNonStaticMapUrl(Gym gym) {
        String url = "http://maps.google.com/maps?q=loc:" + gym.getX() + "," + gym.getY();
        return url;
    }
}
