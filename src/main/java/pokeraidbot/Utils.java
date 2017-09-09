package pokeraidbot;

import pokeraidbot.domain.ClockService;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static final DateTimeFormatter dateTimeParseFormatter = DateTimeFormatter.ofPattern("HH[:][.]mm");
    public static final DateTimeFormatter dateTimePrintFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static ClockService clockService = new ClockService();

    public static ClockService getClockService() {
        return clockService;
    }

    public static void setClockService(ClockService clockService) {
        Utils.clockService = clockService;
    }

    public static String printTime(LocalTime time) {
        return time.format(dateTimePrintFormatter);
    }

    public static void assertGivenTimeNotBeforeNow(String userName, LocalTime time, LocaleService localeService) {
        final LocalTime now = clockService.getCurrentTime();
        if (time.isBefore(now)) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.TIMEZONE, LocaleService.DEFAULT, printTime(time), printTime(now)));
        }
    }

    public static void assertTimeNotInNoRaidTimespan(String userName, LocalTime time, LocaleService localeService) {
        if (time.isAfter(LocalTime.of(22, 00)) || time.isBefore(LocalTime.of(7, 0))) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.NO_RAIDS_NOW, LocaleService.DEFAULT, printTime(time)));
        }
    }

    public static void assertTimeNotMoreThanTwoHoursFromNow(String userName, LocalTime time, LocaleService localeService) {
        final LocalTime now = clockService.getCurrentTime();
        if (now.plusHours(2).isBefore(time)) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.NO_RAID_TOO_LONG, LocaleService.DEFAULT, printTime(time), printTime(now)));
        }
    }

    public static void assertEtaNotAfterRaidEnd(String userName, Raid raid, LocalTime eta, LocaleService localeService) {
        if (eta.isAfter(raid.getEndOfRaid())) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.NO_ETA_AFTER_RAID, LocaleService.DEFAULT, printTime(eta), printTime(raid.getEndOfRaid())));
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
