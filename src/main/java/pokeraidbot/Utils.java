package pokeraidbot;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.pokemon.PokemonTypes;
import pokeraidbot.domain.pokemon.ResistanceTable;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.signup.SignUp;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Utils {
    public static final DateTimeFormatter timeParseFormatter = DateTimeFormatter.ofPattern("H[:][.]mm");
    public static final DateTimeFormatter dateAndTimeParseFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH[:][.]mm");
    public static final DateTimeFormatter timePrintFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter dateAndTimePrintFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final int HIGH_LIMIT_FOR_SIGNUPS = 20;
    public static final int RAID_DURATION_IN_MINUTES = 45;
    public static final int EX_RAID_DURATION_IN_MINUTES = 45;
    private static final String EX_RAID_BOSS = "deoxys";
    private static ClockService clockService = new ClockService();
    private static ResistanceTable resistanceTable = new ResistanceTable();

    public static ClockService getClockService() {
        return clockService;
    }

    public static void setClockService(ClockService clockService) {
        Utils.clockService = clockService;
    }

    public static String printDateTime(LocalDateTime dateTime) {
        return dateTime.format(dateAndTimePrintFormatter);
    }

    public static String printTime(LocalTime time) {
        return time.format(timePrintFormatter);
    }

    public static String printTimeIfSameDay(LocalDateTime dateAndTime) {
        if (dateAndTime.toLocalDate().isEqual(LocalDate.now())) {
            return dateAndTime.toLocalTime().format(timePrintFormatter);
        } else {
            return printDateTime(dateAndTime);
        }
    }

    public static boolean isTypeDoubleStrongVsPokemonWithTwoTypes(String pokemonTypeOne,
                                                                  String pokemonTypeTwo, String typeToCheck) {
        Validate.notEmpty(pokemonTypeOne);
        Validate.notEmpty(pokemonTypeTwo);
        Validate.notEmpty(typeToCheck);
        if (typeIsStrongVsPokemon(pokemonTypeOne, typeToCheck) && typeIsStrongVsPokemon(pokemonTypeTwo, typeToCheck)) {
            return true;
        } else {
            return false;
        }
    }

    public static Set<String> getWeaknessesFor(PokemonTypes pokemonType) {
        return resistanceTable.getWeaknesses(pokemonType);
    }

    public static boolean typeIsStrongVsPokemon(String pokemonType, String typeToCheck) {
        return resistanceTable.typeIsStrongVsPokemon(pokemonType, typeToCheck);
    }

    public static void assertSignupTimeNotBeforeRaidStartAndNow(User user, LocalDateTime dateAndTime,
                                                                LocalDateTime endOfRaid, LocaleService localeService,
                                                                boolean isExRaid) {
        final LocalDateTime startOfRaid = getStartOfRaid(endOfRaid, isExRaid);
        final LocalDateTime now = clockService.getCurrentDateTime();
        assertSignupTimeNotBeforeRaidStart(user, dateAndTime, endOfRaid, localeService, isExRaid);
        if (dateAndTime.isBefore(now)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.SIGN_BEFORE_NOW, localeService.getLocaleForUser(user),
                            printTimeIfSameDay(dateAndTime), printTimeIfSameDay(now)));
        }
    }

    public static LocalDateTime getStartOfRaid(LocalDateTime endOfRaid, boolean isExRaid) {
        return isExRaid ? endOfRaid.minusMinutes(EX_RAID_DURATION_IN_MINUTES) :
                endOfRaid.minusMinutes(RAID_DURATION_IN_MINUTES);
    }

    public static void assertSignupTimeNotBeforeRaidStart(User user, LocalDateTime dateAndTime,
                                                          LocalDateTime endOfRaid, LocaleService localeService,
                                                          boolean isExRaid) {
        final LocalDateTime startOfRaid = getStartOfRaid(endOfRaid, isExRaid);
        if (dateAndTime.isBefore(startOfRaid)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.SIGN_BEFORE_RAID, localeService.getLocaleForUser(user),
                            printTimeIfSameDay(dateAndTime), printTimeIfSameDay(startOfRaid)));
        }
    }

    public static void assertGroupTimeNotBeforeNow(User user, LocalDateTime dateAndTime,
                                                        LocaleService localeService) {
        final LocalDateTime now = clockService.getCurrentDateTime();
        if (dateAndTime.isBefore(now)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_GROUP_BEFORE_NOW, localeService.getLocaleForUser(user),
                            printTimeIfSameDay(dateAndTime), printTimeIfSameDay(now)));
        }
    }

    public static void assertCreateRaidTimeNotBeforeNow(User user, LocalDateTime dateAndTime,
                                                        LocaleService localeService) {
        final LocalDateTime now = clockService.getCurrentDateTime();
        if (dateAndTime.isBefore(now)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.TIMEZONE, localeService.getLocaleForUser(user),
                            printTimeIfSameDay(dateAndTime), printTimeIfSameDay(now)));
        }
    }

    public static void assertTimeNotInNoRaidTimespan(User user, LocalTime time, LocaleService localeService) {
        if (time.isAfter(LocalTime.of(23, 0)) || time.isBefore(LocalTime.of(5, 0))) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_RAIDS_NOW, localeService.getLocaleForUser(user),
                            printTime(time)));
        }
    }

    public static void assertTimeNotMoreThanXHoursFromNow(User user, LocalTime time,
                                                          LocaleService localeService, Integer hours) {
        final LocalTime now = clockService.getCurrentTime();
        if (now.isBefore(LocalTime.of(22, 0)) && now.plusHours(2).isBefore(time)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_RAID_TOO_LONG, localeService.getLocaleForUser(user),
                            printTime(time), printTime(now), String.valueOf(hours)));
        }
    }

    public static void assertEtaNotAfterRaidEnd(User user, Raid raid, LocalDateTime eta, LocaleService localeService) {
        if (eta.isAfter(raid.getEndOfRaid())) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_ETA_AFTER_RAID,
                            localeService.getLocaleForUser(user), printTimeIfSameDay(eta),
                            printTimeIfSameDay(raid.getEndOfRaid())));
        }
    }

    public static String getStaticMapUrl(Gym gym) {
        // todo: host marker png via pokeraidbot web
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + gym.getX() + "," + gym.getY() +
                "&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C" +
                gym.getX() + "," + gym.getY() + "&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU";
        return url;
    }

    public static String getNonStaticMapUrl(Gym gym) {
        String url = "http://www.google.com/maps?q=" + gym.getX() + "," + gym.getY();
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

    public static boolean isSamePokemon(String pokemonName, String existingEntityPokemon) {
        return pokemonName.equalsIgnoreCase(existingEntityPokemon);
    }

    public static boolean raidsCollide(LocalDateTime endOfRaid, boolean isExRaid, LocalDateTime endOfRaidTwo,
                                       boolean isExRaidTwo) {
        LocalDateTime startTime = getStartOfRaid(endOfRaid, isExRaid);
        LocalDateTime startTimeTwo = getStartOfRaid(endOfRaidTwo, isExRaidTwo);
        return isInInterval(startTime, endOfRaid, startTimeTwo, endOfRaidTwo) ||
                isInInterval(startTimeTwo, endOfRaidTwo, startTime, endOfRaid);
    }

    private static boolean isInInterval(LocalDateTime startTime, LocalDateTime endOfRaid,
                                        LocalDateTime startTimeTwo, LocalDateTime endOfRaidTwo) {
        return (startTime.isAfter(startTimeTwo) && startTime.isBefore(endOfRaidTwo)) ||
                (endOfRaid.isBefore(endOfRaidTwo) && endOfRaid.isAfter(startTimeTwo));
    }

    public static boolean isRaidExPokemon(String pokemonName, PokemonRaidStrategyService strategyService, PokemonRepository pokemonRepository) {
        Pokemon pokemon = pokemonRepository.getByName(pokemonName);
        return strategyService.getRaidInfo(pokemon).getBossTier() == 5;
    }

    public static LocalTime parseTime(User user, String timeString, LocaleService localeService) {
        LocalTime endsAtTime;
        try {
            timeString = preProcessTimeString(timeString);
            endsAtTime = LocalTime.parse(timeString, Utils.timeParseFormatter);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.BAD_DATETIME_FORMAT, localeService.getLocaleForUser(user),
                            "HH:MM", timeString));
        }
        return endsAtTime;
    }

    public static Integer assertNotTooManyOrNoNumber(User user, LocaleService localeService, String people) {
        Integer numberOfPeople;
        try {
            numberOfPeople = new Integer(people);
            if (numberOfPeople < 1 || numberOfPeople > HIGH_LIMIT_FOR_SIGNUPS) {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.ERROR_PARSE_PLAYERS,
                            localeService.getLocaleForUser(user),
                            people, String.valueOf(HIGH_LIMIT_FOR_SIGNUPS)));
        }
        return numberOfPeople;
    }

    public static LocalDate parseDate(User user, String dateString, LocaleService localeService) {
        LocalDate theDate;
        try {
            theDate = LocalDate.parse(dateString);
        } catch (DateTimeException | NullPointerException e) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.BAD_DATETIME_FORMAT,
                            localeService.getLocaleForUser(user), "yyyy-MM-dd", dateString));
        }
        return theDate;
    }

    public static Set<String> getNamesOfThoseWithSignUps(Set<SignUp> signUpsAt, boolean includeEta) {
        final Set<String> signUpNames;
        signUpNames = new LinkedHashSet<>();
        for (SignUp signUp : signUpsAt) {
            if (signUp.getHowManyPeople() > 0) {
                String text = signUp.getUserName() + " (**" + signUp.getHowManyPeople();
                if (includeEta) {
                   text = text + ", ETA " + printTime(signUp.getArrivalTime());
                }
                text = text + "**)";
                signUpNames.add(text);
            }
        }
        return signUpNames;
    }

    public static String getPokemonIcon(Pokemon pokemon) {
        if (!pokemon.isEgg()) {
            return "https://pokemongohub.net/sprites/normal/" + pokemon.getNumber() + ".png";
        } else {
            return "https://pokeraidbot2.herokuapp.com/img/" + pokemon.getName().toLowerCase() + ".png";
        }
    }

    public static String[] prepareArguments(CommandEvent commandEvent) {
        return commandEvent.getArgs().replaceAll("\\s{2,4}", " ").split(" ");
    }

    public static boolean isRaidEx(Raid raid, PokemonRaidStrategyService strategyService, PokemonRepository pokemonRepository) {
        return isRaidExPokemon(raid.getPokemon().getName(), strategyService, pokemonRepository);
    }

    public static String preProcessTimeString(String timeString) {
        if (timeString != null && timeString.matches("[0-9]{3,4}")) {
            return new StringBuilder(timeString).insert(timeString.length()-2, ":").toString();
        } else {
            return timeString;
        }
    }

    public static void assertGroupStartNotBeforeRaidStart(LocalDateTime raidStart, LocalDateTime groupStart,
                                                          User user, LocaleService localeService) {
        if (raidStart.isAfter(groupStart)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_GROUP_BEFORE_RAID,
                            localeService.getLocaleForUser(user), printTimeIfSameDay(groupStart),
                            printTimeIfSameDay(raidStart)));
        }
    }

    public static void assertTimeInRaidTimespan(User user, LocalDateTime dateTimeToCheck, Raid raid,
                                                LocaleService localeService) {
        final LocalDateTime startOfRaid = getStartOfRaid(raid.getEndOfRaid(), raid.isExRaid());
        final boolean timeIsSameOrBeforeEnd =
                raid.getEndOfRaid().isAfter(dateTimeToCheck) || raid.getEndOfRaid().equals(dateTimeToCheck);
        final boolean timeIsSameOrAfterStart =
                startOfRaid.isBefore(dateTimeToCheck) || startOfRaid.equals(dateTimeToCheck);
        if (!(timeIsSameOrBeforeEnd && timeIsSameOrAfterStart)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TIME_NOT_IN_RAID_TIMESPAN,
                    localeService.getLocaleForUser(user), printDateTime(dateTimeToCheck),
                    printDateTime(startOfRaid), printTimeIfSameDay(raid.getEndOfRaid())));
        }
    }

    public static Set<String> getResistantTo(PokemonTypes pokemonTypes) {
        return resistanceTable.getResistantTo(pokemonTypes);
    }

    public static boolean isExceptionOrCauseNetworkIssues(Throwable t) {
        return t != null && (t.getMessage().contains("SocketTimeoutException") || (isInstanceOfSocketException(t) ||
                (t.getCause() != null && isInstanceOfSocketException(t.getCause()))));
    }

    private static boolean isInstanceOfSocketException(Throwable t) {
        return (t instanceof SocketException) || (t instanceof SocketTimeoutException);
    }
}
