package pokeraidbot;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.*;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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

    public static boolean isTypeDoubleStrongVsPokemonWithTwoTypes(String pokemonTypeOne, String pokemonTypeTwo, String typeToCheck) {
        Validate.notEmpty(pokemonTypeOne);
        Validate.notEmpty(pokemonTypeTwo);
        Validate.notEmpty(typeToCheck);
        if (typeIsStrongVsPokemon(pokemonTypeOne, typeToCheck) && typeIsStrongVsPokemon(pokemonTypeTwo, typeToCheck)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean typeIsStrongVsPokemon(String pokemonType, String typeToCheck) {
        Validate.notEmpty(pokemonType);
        Validate.notEmpty(typeToCheck);
        String check = typeToCheck.toUpperCase().trim();
        switch (pokemonType.toUpperCase().trim()) {
            case "NORMAL": return check.equals("FIGHTING");
            case "FIRE": return Arrays.asList("WATER", "GROUND", "ROCK").contains(check);
            case "WATER": return Arrays.asList("ELECTRIC", "GRASS").contains(check);
            case "ELECTRIC": return Arrays.asList("GROUND").contains(check);
            case "GRASS": return Arrays.asList("FIRE", "ICE", "POISON", "FLYING", "BUG").contains(check);
            case "ICE": return Arrays.asList("FIRE", "FIGHTING", "ROCK", "STEEL").contains(check);
            case "FIGHTING": return Arrays.asList("FLYING", "PSYCHIC", "FAIRY").contains(check);
            case "POISON": return Arrays.asList("PSYCHIC", "GROUND").contains(check);
            case "GROUND": return Arrays.asList("WATER", "GRASS", "ICE").contains(check);
            case "FLYING": return Arrays.asList("ELECTRIC", "ICE", "ROCK").contains(check);
            case "PSYCHIC": return Arrays.asList("BUG", "GHOST", "DARK").contains(check);
            case "BUG": return Arrays.asList("FIRE", "FLYING", "ROCK").contains(check);
            case "ROCK": return Arrays.asList("WATER", "GRASS", "FIGHTING", "GROUND", "STEEL").contains(check);
            case "GHOST": return Arrays.asList("GHOST", "DARK").contains(check);
            case "DRAGON": return Arrays.asList("ICE", "DRAGON", "FAIRY").contains(check);
            case "DARK": return Arrays.asList("BUG", "FIGHTING", "FAIRY").contains(check);
            case "STEEL": return Arrays.asList("FIRE", "FIGHTING", "GROUND").contains(check);
            case "FAIRY": return Arrays.asList("POISON", "STEEL").contains(check);
        }
        return false;
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

    public static String printWeaknesses(Pokemon pokemon) {
        Set<String> weaknessesToPrint = new LinkedHashSet<>();
        final Set<String> typeSet = pokemon.getTypes().getTypeSet();
        for (String weakness : pokemon.getWeaknesses()) {
            if (typeSet.size() > 1) {
                final Iterator<String> iterator = typeSet.iterator();
                if (isTypeDoubleStrongVsPokemonWithTwoTypes(iterator.next(), iterator.next(), weakness)) {
                    weaknessesToPrint.add("**" + weakness + "**");
                } else {
                    weaknessesToPrint.add(weakness);
                }
            } else {
                weaknessesToPrint.add(weakness);
            }
        }

        return StringUtils.join(weaknessesToPrint, ", ");
    }
}
