package pokeraidbot;

import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String printTime(LocalTime time) {
        return time.format(dateTimeFormatter);
    }

    public static void assertGivenTimeNotBeforeNow(String userName, LocalTime time) {
        if (time.isBefore(LocalTime.now())) {
            throw new UserMessedUpException(userName,
                    "You seem to be living in a different timezone. Your given time is " + printTime(time) +
                            ", while the current time is " + printTime(LocalTime.now()));
        }
    }

    public static void assertEtaNotAfterRaidEnd(String userName, Raid raid, LocalTime eta) {
        if (eta.isAfter(raid.getEndOfRaid())) {
            throw new UserMessedUpException(userName,
                    "Can't arrive after raid has ended. Your given time is " + printTime(eta) +
                            ", raid ends at " + printTime(raid.getEndOfRaid()));
        }
    }

    public static String getMapUrl(Gym gym) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + gym.getX() + "," + gym.getY() +
                "&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C" +
                gym.getX() + "," + gym.getY() + "&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU";
    }
}
