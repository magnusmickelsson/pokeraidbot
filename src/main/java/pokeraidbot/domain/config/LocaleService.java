package pokeraidbot.domain.config;

import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleService {
    public static final String NEW_EX_RAID_HELP = "NEW_EX_RAID_HELP";
    public static final String RAIDSTATUS = "RAIDSTATUS";
    public static final String NO_RAID_AT_GYM = "NO_RAID_AT_GYM";
    public static final String REMOVE_SIGNUP_HELP = "REMOVE_SIGNUP_HELP";
    public static final String SIGNUP_REMOVED = "SIGNUP_REMOVED";
    public static final String NO_SIGNUP_AT_GYM = "NO_SIGNUP_AT_GYM";
    public static final String SIGNUP_HELP = "SIGNUP_HELP";
    public static final String ERROR_PARSE_PLAYERS = "ERROR_PARSE_PLAYERS";
    public static final String CURRENT_SIGNUPS = "CURRENT_SIGNUPS";
    public static final String SIGNUPS = "SIGNUPS";
    public static final String WHERE_GYM_HELP = "WHERE_GYM_HELP";
    public static final String ALREADY_SIGNED_UP = "ALREADY_SIGNED_UP";
    public static final String NO_POKEMON = "NO_POKEMON";
    public static final String TIMEZONE = "TIMEZONE";
    public static final String NO_RAIDS_NOW = "NO_RAIDS_NOW";
    public static final String NO_RAID_TOO_LONG = "NO_RAID_TOO_LONG";
    public static final String NO_ETA_AFTER_RAID = "NO_ETA_AFTER_RAID";
    public static final String GYM_SEARCH = "GYM_SEARCH";
    public static final String GYM_SEARCH_OPTIONS = "GYM_SEARCH_OPTIONS";
    public static final String GYM_SEARCH_MANY_RESULTS = "GYM_SEARCH_MANY_RESULTS";
    public static final String GYM_CONFIG_ERROR = "GYM_CONFIG_ERROR";
    public static final String SERVER_HELP = "SERVER_HELP";
    public static final String DONATE = "DONATE";
    public static final String TRACKING_EXISTS = "TRACKING_EXISTS";
    public static final String TRACKED_RAID = "TRACKED_RAID";
    public static final String TRACKING_ADDED = "TRACKING_ADDED";
    public static final String TRACKING_NOT_EXISTS = "TRACKING_NOT_EXISTS";
    public static final String TRACK_HELP = "TRACK_HELP";
    public static final String UNTRACK_HELP = "UNTRACK_HELP";
    public static final String TRACKING_REMOVED = "TRACKING_REMOVED";
    public static final String SIGN_BEFORE_RAID = "SIGN_BEFORE_RAID";
    public static final String GYM_NOT_FOUND = "GYM_NOT_FOUND";
    public static final String RAID_EXISTS = "RAID_EXISTS";
    public static final String USAGE = "USAGE";
    public static final String USAGE_HELP = "USAGE_HELP";
    public static final String AT_YOUR_SERVICE = "AT_YOUR_SERVICE";
    public static final String NEW_RAID_HELP = "NEW_RAID_HELP";
    public static final String NEW_RAID_CREATED = "NEW_RAID_CREATED";
    public static final String RAID_TOSTRING = "RAID_TOSTRING";
    public static final String VS_HELP = "VS_HELP";
    public static final String WEAKNESSES = "WEAKNESSES";
    public static final String RESISTANT = "RESISTANT";
    public static final String BEST_COUNTERS = "BEST_COUNTERS";
    public static final String OTHER_COUNTERS = "OTHER_COUNTERS";
    public static final String IF_CORRECT_MOVESET = "IF_CORRECT_MOVESET";
    public static final String LIST_HELP = "LIST_HELP";
    public static final String LIST_NO_RAIDS = "LIST_NO_RAIDS";
    public static final String RAID_BETWEEN = "RAID_BETWEEN";
    public static final String CURRENT_RAIDS = "CURRENT_RAIDS";
    public static final String SIGNED_UP = "SIGNED_UP";
    public static final String RAIDSTATUS_HELP = "RAIDSTATUS_HELP";
    public static final String GENERIC_USER_ERROR = "GENERIC_USER_ERROR";

    public static final Locale SWEDISH = new Locale("sv");

    // Change this if you want another default locale, affects the usage texts etc
    public static final Locale DEFAULT = SWEDISH;
    public static final Locale[] SUPPORTED_LOCALES = {DEFAULT, Locale.ENGLISH};
    public static final String MANUAL_RAID = "MANUAL_RAID";
    public static final String MANUAL_SIGNUP = "MANUAL_SIGNUP";
    public static final String MANUAL_MAP = "MANUAL_MAP";
    public static final String MANUAL_INSTALL = "MANUAL_INSTALL";
    public static final String MANUAL_CHANGE = "MANUAL_CHANGE";
    public static final String WRONG_NUMBER_OF_ARGUMENTS = "WRONG_NUMBER_OF_ARGUMENTS";
    public static final String MANUAL_TRACKING = "MANUAL_TRACKING";
    public static final String ACTIVE = "ACTIVE";
    public static final String START_GROUP = "START_GROUP";
    public static final String FIND_YOUR_WAY = "FIND_YOUR_WAY";
    public static final String RAID_BOSS = "RAID_BOSS";
    public static final String FOR_HINTS = "FOR_HINTS";
    public static final String RAID_GROUP_HELP = "RAID_GROUP_HELP";
    public static final String CANT_CREATE_GROUP_LATE = "CANT_CREATE_GROUP_LATE";
    public static final String HANDLE_SIGNUP = "HANDLE_SIGNUP";
    public static final String REMOVED_GROUP = "REMOVED_GROUP";
    public static final String GROUP_HEADLINE = "GROUP_HEADLINE";
    public static final String POKEMON = "POKEMON";
    public static final String SIGNED_UP_TOTAL = "SIGNED_UP_TOTAL";
    public static final String SIGNED_UP_AT = "SIGNED_UP_AT";
    public static final String NO_EMOTES = "NO_EMOTES";
    public static final String MANUAL_GROUPS = "MANUAL_GROUPS";

    private Map<I18nLookup, String> i18nMessages = new HashMap<>();

    public LocaleService() {
        i18nMessages.put(new I18nLookup(NO_EMOTES, Locale.ENGLISH),
                "Administrator has not installed pokeraidbot's emotes. " +
                        "Ensure he/she runs the following command: !raid install-emotes"
        );
        i18nMessages.put(new I18nLookup(NO_EMOTES, SWEDISH),
                "Administratören för denna server har inte installerat pokeraidbot's emotes. " +
                        "Se till att hen kör följande kommando: !raid install-emotes"
        );

        i18nMessages.put(new I18nLookup(SIGNED_UP_AT, Locale.ENGLISH),
//                "Signed up to start at"
                "Signed up"
        );
        i18nMessages.put(new I18nLookup(SIGNED_UP_AT, SWEDISH),
//                "Anmälda att komma"
                "Anmälda"
        );

        i18nMessages.put(new I18nLookup(SIGNED_UP_TOTAL, Locale.ENGLISH),
                "Signed up for raid"
        );
        i18nMessages.put(new I18nLookup(SIGNED_UP_TOTAL, SWEDISH),
                "Anmälda totalt till raiden"
        );

        i18nMessages.put(new I18nLookup(POKEMON, Locale.ENGLISH),
                "Pokemon:"
        );
        i18nMessages.put(new I18nLookup(POKEMON, SWEDISH),
                "Pokemon:"
        );

        i18nMessages.put(new I18nLookup(GROUP_HEADLINE, Locale.ENGLISH),
//                "%1's group @ %2, starts at %3"
                "%1 @ %2, starts at %3"
        );
        i18nMessages.put(new I18nLookup(GROUP_HEADLINE, SWEDISH),
//                "%1s grupp @ %2, startar %3"
                "%1 @ %2, startar %3"
        );

//        i18nMessages.put(new I18nLookup(HANDLE_SIGNUP, Locale.ENGLISH),
//                " Removed your group which was supposed to start at %1, since it's expired. " +
//                        "Your signups remain on raid total until " +
//                        "you either type \"!raid remove %2\" or the raid expires.\n" +
//                        "If you want to run a group later than %1, " +
//                        "type \"!raid group {tid}\" and give a later time.");
//        i18nMessages.put(new I18nLookup(HANDLE_SIGNUP, SWEDISH),
//                " Tog bort din grupp som skulle börja raiden vid %1, tiden har nu passerat. " +
//                        "Era signups står kvar på raidens total, tills " +
//                        "ni kör kommandot \"!raid remove %2\" eller raiden tar slut.\n" +
//                        "Om ni vill köra en ny grupp lite senare, " +
//                        "skriv \"!raid group {tid}\" och ange en senare tid.");

        i18nMessages.put(new I18nLookup(HANDLE_SIGNUP, Locale.ENGLISH),
                "Handle sign up via the buttons below.");
        i18nMessages.put(new I18nLookup(HANDLE_SIGNUP, SWEDISH),
                "Hantera anmälning via knapparna nedan.");

        i18nMessages.put(new I18nLookup(CANT_CREATE_GROUP_LATE, Locale.ENGLISH),
                "Can't create a group to raid after raid has ended. :("
        );
        i18nMessages.put(new I18nLookup(CANT_CREATE_GROUP_LATE, SWEDISH),
                "Kan inte skapa en grupp som ska samlas efter att raiden slutat."
        );

        i18nMessages.put(new I18nLookup(RAID_GROUP_HELP, Locale.ENGLISH),
                "Create a group which will start the raid together at a given time: " +
                        "!raid group [start time (HH:MM)] [gym name]");
        i18nMessages.put(new I18nLookup(RAID_GROUP_HELP, SWEDISH),
                "Skapa ett tillfälle för en grupp att köra vid en skapad raid: " +
                "!raid group [start time (HH:MM)] [gym name]");

        i18nMessages.put(new I18nLookup(FOR_HINTS, Locale.ENGLISH), "For hints - type:");
        i18nMessages.put(new I18nLookup(FOR_HINTS, SWEDISH), "För tips - skriv:");

        i18nMessages.put(new I18nLookup(RAID_BOSS, Locale.ENGLISH), "Raidboss:");
        i18nMessages.put(new I18nLookup(RAID_BOSS, SWEDISH), "Raidboss:");

        i18nMessages.put(new I18nLookup(FIND_YOUR_WAY, Locale.ENGLISH), "Find your way here:");
        i18nMessages.put(new I18nLookup(FIND_YOUR_WAY, SWEDISH), "Hitta dit:");

        i18nMessages.put(new I18nLookup(START_GROUP, Locale.ENGLISH), "Start group - write (change the time)");
        i18nMessages.put(new I18nLookup(START_GROUP, SWEDISH), "Starta grupp - skriv (med egen tid)");

        i18nMessages.put(new I18nLookup(ACTIVE, Locale.ENGLISH), "Active");
        i18nMessages.put(new I18nLookup(ACTIVE, SWEDISH), "Aktiv");
        // todo: change to use Spring resource bundles instead
        i18nMessages.put(new I18nLookup(WRONG_NUMBER_OF_ARGUMENTS, Locale.ENGLISH),
                "Wrong number of arguments for command, expected %1 but was %2. Write \"!raid man\" for help.");
        i18nMessages.put(new I18nLookup(WRONG_NUMBER_OF_ARGUMENTS, SWEDISH),
                "Fel antal argument, förväntade %1, men det var %2. " +
                        "Skriv \"!raid man\" för att få hjälp");

        i18nMessages.put(new I18nLookup(UNTRACK_HELP, Locale.ENGLISH),
                "Remove an active Pokemon tracking - !raid untrack [Pokemon]");
        i18nMessages.put(new I18nLookup(UNTRACK_HELP, SWEDISH),
                "Ta bort övervakning för viss Pokemon - !raid untrack [Pokemon]");

        i18nMessages.put(new I18nLookup(TRACK_HELP, Locale.ENGLISH),
                "Track new raids for a certain Pokemon (message in DM) - !raid track [Pokemon]");
        i18nMessages.put(new I18nLookup(TRACK_HELP, SWEDISH),
                "Håll koll efter nya raider för en viss Pokemon (via DM) - !raid track [Pokemon]");

        i18nMessages.put(new I18nLookup(TRACKING_ADDED, Locale.ENGLISH),
                "Added tracking for pokemon %1 for user %2.");
        i18nMessages.put(new I18nLookup(TRACKING_ADDED, SWEDISH),
                "Lade till övervakning av pokemon %1 för %2.");

        i18nMessages.put(new I18nLookup(TRACKING_REMOVED, Locale.ENGLISH),
                "Removed tracking for %1 for user %2.");
        i18nMessages.put(new I18nLookup(TRACKING_REMOVED, SWEDISH),
                "Tog bort övervakning av %1 för %2.");

        i18nMessages.put(new I18nLookup(TRACKED_RAID, Locale.ENGLISH),
                "Raid was created for %1 by %2 - %3");
        i18nMessages.put(new I18nLookup(TRACKED_RAID, SWEDISH),
                "Raid skapades för raidboss %1 av %2 - %3");

        i18nMessages.put(new I18nLookup(TRACKING_NOT_EXISTS, Locale.ENGLISH),
                "There was no such tracking set for you.");
        i18nMessages.put(new I18nLookup(TRACKING_NOT_EXISTS, SWEDISH),
                "Det fanns ingen sådan övervakning för dig.");

        i18nMessages.put(new I18nLookup(TRACKING_EXISTS, Locale.ENGLISH),
                "You're already tracking that pokemon.");
        i18nMessages.put(new I18nLookup(TRACKING_EXISTS, SWEDISH),
                "Du har redan övervakning satt för det.");

        i18nMessages.put(new I18nLookup(DONATE, Locale.ENGLISH),
                "How to support development and maintenance of this bot via donating.");
        i18nMessages.put(new I18nLookup(DONATE, SWEDISH),
                "Hur kan man stödja utveckling och drift av botten?");

        i18nMessages.put(new I18nLookup(GYM_CONFIG_ERROR, Locale.ENGLISH),
                "There are no gyms for this region. " +
                        "Please check configuration and/or notify administrator!");
        i18nMessages.put(new I18nLookup(GYM_CONFIG_ERROR, SWEDISH),
                "Det finns inga gym för din valda region. Meddela en administratör så de kan kontrollera " +
                        "konfigurationen av servern. Ni har väl sett till att importera gymdata?");
        i18nMessages.put(new I18nLookup(GYM_SEARCH_MANY_RESULTS, Locale.ENGLISH),
                "Could not find one unique gym/pokestop, your query returned 5+ results. Try refine your search.");
        i18nMessages.put(new I18nLookup(GYM_SEARCH_MANY_RESULTS, SWEDISH),
                "Kunde inte hitta ett unikt gym/pokestop, din sökning returnerade mer än 5 resultat. " +
                        "Försök vara mer precis.");

        i18nMessages.put(new I18nLookup(GYM_SEARCH_OPTIONS, Locale.ENGLISH),
                "Could not find one unique gym/pokestop. Did you want any of these? %1");
        i18nMessages.put(new I18nLookup(GYM_SEARCH_OPTIONS, SWEDISH),
                "Kunde inte hitta ett unikt gym/pokestop. Var det något av dessa du sökte efter? %1");

        i18nMessages.put(new I18nLookup(GYM_SEARCH, Locale.ENGLISH),
                "Empty input for gym name, try giving me a proper name to search for.");
        i18nMessages.put(new I18nLookup(GYM_SEARCH, SWEDISH),
                "Tom söksträng för gymnamn, ge mig något skoj att söka efter!");

        i18nMessages.put(new I18nLookup(SIGN_BEFORE_RAID, Locale.ENGLISH),
                "Can't sign up for this raid before current time. Your given time is %1, time is currently %2");
        i18nMessages.put(new I18nLookup(SIGN_BEFORE_RAID, SWEDISH),
                "Du kan inte anmäla dig att anlända innan nuvarande tid. Din ETA är %1, men klockan är %2.");

        i18nMessages.put(new I18nLookup(NO_ETA_AFTER_RAID, Locale.ENGLISH),
                "Can't arrive after raid has ended. Your given time is %1, raid ends at %2");
        i18nMessages.put(new I18nLookup(NO_ETA_AFTER_RAID, SWEDISH),
                "Det är väl inte så lämpligt att anlända efter att raiden slutat? Din ETA är %1, raiden slutar %2.");

        i18nMessages.put(new I18nLookup(NO_RAID_TOO_LONG, Locale.ENGLISH),
                "You can't set an end of raid time which are later than %3 hours from the current time " +
                        "%2 except for EX raids - your input was %1.");
        i18nMessages.put(new I18nLookup(NO_RAID_TOO_LONG, SWEDISH),
                "Du kan inte sätta en sluttid för raid senare än %3 timmar från vad klockan är nu (%2), " +
                        "förutom för EX raider. Du angav %1.");

        i18nMessages.put(new I18nLookup(NO_RAIDS_NOW, Locale.ENGLISH),
                "You can't create raids between 22:00 and 07:00 - your time was %1.");
        i18nMessages.put(new I18nLookup(NO_RAIDS_NOW, SWEDISH),
                "Du kan inte skapa en raid som slutar mellan 22.00 och 07:00. Du angav %1.");

        i18nMessages.put(new I18nLookup(TIMEZONE, Locale.ENGLISH),
                "You seem to be living in a different timezone. Your input was %1, while it's currently %2.");
        i18nMessages.put(new I18nLookup(TIMEZONE, SWEDISH),
                "Du kan inte ange att en raid ska sluta innan nuvarande tid (utom för EX-raid). " +
                        "Du angav %1, vilket är innan %2.");

        i18nMessages.put(new I18nLookup(NO_POKEMON, Locale.ENGLISH), "Could not find a pokemon with name \"%1\".");
        i18nMessages.put(new I18nLookup(NO_POKEMON, SWEDISH), "Kunde inte hitta pokemon med namn \"%1\".");

        i18nMessages.put(new I18nLookup(ALREADY_SIGNED_UP, Locale.ENGLISH), "You're already signed up for: %1 ... " +
                "%2 - remove your current signup then signup again.");
        i18nMessages.put(new I18nLookup(ALREADY_SIGNED_UP, SWEDISH), "Du har redan anmält dig till raid vid %1 ... " +
                "%2 - ta bort din anmälan och gör om som du vill ha det.");

        i18nMessages.put(new I18nLookup(WHERE_GYM_HELP, Locale.ENGLISH), "Get map link for gym - !raid map [Gym name]");
        i18nMessages.put(new I18nLookup(WHERE_GYM_HELP, SWEDISH), "Visa karta för gym - !raid map [Gym]");

        i18nMessages.put(new I18nLookup(SIGNUPS, Locale.ENGLISH), "%1 sign up added to %2. %3");
        i18nMessages.put(new I18nLookup(SIGNUPS, SWEDISH), "Anmälan från %1 registrerad till %2. %3");

        i18nMessages.put(new I18nLookup(CURRENT_SIGNUPS, Locale.ENGLISH), "Current signups: ");
        i18nMessages.put(new I18nLookup(CURRENT_SIGNUPS, SWEDISH), "Vilka kommer: ");

        i18nMessages.put(new I18nLookup(ERROR_PARSE_PLAYERS, Locale.ENGLISH),
                "Can't parse this number of people: %1 - give a valid number 1-%2.");
        i18nMessages.put(new I18nLookup(ERROR_PARSE_PLAYERS, SWEDISH),
                "Felaktigt antal personer: %1 - ange ett korrekt antal 1-%2.");

        i18nMessages.put(new I18nLookup(SERVER_HELP, Locale.ENGLISH),
                "Info about your server's configuration: !raid server");
        i18nMessages.put(new I18nLookup(SERVER_HELP, SWEDISH),
                "Information om serverns konfiguration: !raid server");

        i18nMessages.put(new I18nLookup(SIGNUP_HELP, Locale.ENGLISH),
                "Sign up for a raid: !raid add [number of people] [ETA (HH:MM)] [Gym]");
        i18nMessages.put(new I18nLookup(SIGNUP_HELP, SWEDISH),
                "Anmäl dig till en raid: !raid add [antal spelare] [ETA (HH:MM)] [Gym]");

        i18nMessages.put(new I18nLookup(NO_RAID_AT_GYM, Locale.ENGLISH), "%1 had no signup to remove for gym %2");
        i18nMessages.put(new I18nLookup(NO_RAID_AT_GYM, SWEDISH), "%1 hade ingen anmälan att ta bort för raid vid %2");

        i18nMessages.put(new I18nLookup(SIGNUP_REMOVED, Locale.ENGLISH), "Signup removed for gym %1: %2");
        i18nMessages.put(new I18nLookup(SIGNUP_REMOVED, SWEDISH), "Tog bort din anmälan för gym %1: %2");

        i18nMessages.put(new I18nLookup(REMOVE_SIGNUP_HELP, Locale.ENGLISH),
                "Remove your signup for this gym: !raid remove [Gym]");
        i18nMessages.put(new I18nLookup(REMOVE_SIGNUP_HELP, SWEDISH),
                "Ta bort din anmälan för raid på ett gym: !raid remove [Gym]");

        i18nMessages.put(new I18nLookup(NO_RAID_AT_GYM, Locale.ENGLISH),
                "Could not find an active raid for this gym: \"%1\".");
        i18nMessages.put(new I18nLookup(NO_RAID_AT_GYM, SWEDISH),
                "Kunde inte hitta någon aktiv raid för \"%1\".");

        i18nMessages.put(new I18nLookup(RAIDSTATUS, Locale.ENGLISH), "%1:");
        i18nMessages.put(new I18nLookup(RAIDSTATUS, SWEDISH), "%1:");

        i18nMessages.put(new I18nLookup(RAIDSTATUS_HELP, Locale.ENGLISH),
                "Check status for raid - !raid status [Gym name].");
        i18nMessages.put(new I18nLookup(RAIDSTATUS_HELP, SWEDISH),
                "Se status för raid - !raid status [Gym].");

        i18nMessages.put(new I18nLookup(SIGNED_UP, Locale.ENGLISH), "signed up");
        i18nMessages.put(new I18nLookup(SIGNED_UP, SWEDISH), "anmäld(a)");

        i18nMessages.put(new I18nLookup(CURRENT_RAIDS, Locale.ENGLISH), "Current raids");
        i18nMessages.put(new I18nLookup(CURRENT_RAIDS, SWEDISH), "Pågående raids");

        i18nMessages.put(new I18nLookup(RAID_BETWEEN, Locale.ENGLISH), "between %1 and %2");
        i18nMessages.put(new I18nLookup(RAID_BETWEEN, SWEDISH), "mellan %1 och %2");

        i18nMessages.put(new I18nLookup(LIST_NO_RAIDS, Locale.ENGLISH), "There are currently no active raids. " +
                "To register a raid, use the following command:\n!raid new {pokemon} {ends at (HH:mm)} {gym}\n" +
                "Example: !raid new Entei 09:45 Solna Platform");
        i18nMessages.put(new I18nLookup(LIST_NO_RAIDS, SWEDISH),
                "Det finns just nu inga registrerade raids. " +
                        "För att registrera en raid, skriv:\n" +
                        "!raid new {pokemon} {sluttid (HH:mm)} {gym}\n" +
        "Exempel: !raid new Entei 09:45 Solna Platform");

        i18nMessages.put(new I18nLookup(LIST_HELP, Locale.ENGLISH),
                "Check current raids - !raid list [optional: Pokemon]");
        i18nMessages.put(new I18nLookup(LIST_HELP, SWEDISH), "Visa aktiva raids - " +
                "!raid list [Pokemon (frivilligt att ange)]");


        i18nMessages.put(new I18nLookup(IF_CORRECT_MOVESET, Locale.ENGLISH), "(if correct moveset)");
        i18nMessages.put(new I18nLookup(IF_CORRECT_MOVESET, SWEDISH), "(om bra \"moves\")");

        i18nMessages.put(new I18nLookup(OTHER_COUNTERS, Locale.ENGLISH), "Other counters: ");
        i18nMessages.put(new I18nLookup(OTHER_COUNTERS, SWEDISH), "Andra bra val: ");

        i18nMessages.put(new I18nLookup(BEST_COUNTERS, Locale.ENGLISH), "Best counter: ");
        i18nMessages.put(new I18nLookup(BEST_COUNTERS, SWEDISH), "Bästa valet: ");

        i18nMessages.put(new I18nLookup(RESISTANT, Locale.ENGLISH), "Avoid using: ");
        i18nMessages.put(new I18nLookup(RESISTANT, SWEDISH), "Undvik: ");

        i18nMessages.put(new I18nLookup(WEAKNESSES, Locale.ENGLISH), "Use: ");
        i18nMessages.put(new I18nLookup(WEAKNESSES, SWEDISH), "Använd: ");

        i18nMessages.put(new I18nLookup(VS_HELP, Locale.ENGLISH),
                "List information about a pokemon, it's types, weaknesses etc. - !raid vs [Pokemon]");
        i18nMessages.put(new I18nLookup(VS_HELP, SWEDISH),
                "Se information om en pokemon, dess typ, svagheter etc. - !raid vs [Pokemon]");

        i18nMessages.put(new I18nLookup(RAID_TOSTRING, Locale.ENGLISH), "Raid for %1 at gym %2, ends at %3");
        i18nMessages.put(new I18nLookup(RAID_TOSTRING, SWEDISH), "%1 raid vid %2, slut %3");

        i18nMessages.put(new I18nLookup(NEW_RAID_CREATED, Locale.ENGLISH), "Raid created: %1");
        i18nMessages.put(new I18nLookup(NEW_RAID_CREATED, SWEDISH), "Raid skapad: %1");

        i18nMessages.put(new I18nLookup(NEW_RAID_HELP, Locale.ENGLISH),
                "Create new raid - !raid new [Name of Pokemon] [Ends at (HH:MM)] [Gym name]");
        i18nMessages.put(new I18nLookup(NEW_RAID_HELP, SWEDISH),
                "Skapa ny raid - !raid new [Pokemon] [Slutar klockan (HH:MM)] [Gym]");

        i18nMessages.put(new I18nLookup(NEW_EX_RAID_HELP, Locale.ENGLISH),
                "Create new EX raid - !raid ex [Name of Pokemon] [Ends at (yyyy-mm-dd HH:MM)] [Gym name]");
        i18nMessages.put(new I18nLookup(NEW_EX_RAID_HELP, SWEDISH),
                "Skapa ny EX raid - !raid ex [Pokemon] [Slutar (yyyy-mm-dd HH:MM)] [Gym]");

        i18nMessages.put(new I18nLookup(AT_YOUR_SERVICE, Locale.ENGLISH), "PokeRaidBot reporting for duty!");
        i18nMessages.put(new I18nLookup(AT_YOUR_SERVICE, SWEDISH), "PokeRaidBot till er tjänst!");

        i18nMessages.put(new I18nLookup(USAGE_HELP, Locale.ENGLISH), "Shows usage of bot.");
        i18nMessages.put(new I18nLookup(USAGE_HELP, SWEDISH), "Visar hur man använder botten.");

        i18nMessages.put(new I18nLookup(GENERIC_USER_ERROR, Locale.ENGLISH), "%1: %2");
        i18nMessages.put(new I18nLookup(GENERIC_USER_ERROR, SWEDISH), "%1: %2");

        i18nMessages.put(new I18nLookup(USAGE, Locale.ENGLISH), featuresString_EN);
        i18nMessages.put(new I18nLookup(USAGE, SWEDISH), featuresString_SV);

        i18nMessages.put(new I18nLookup(GYM_NOT_FOUND, Locale.ENGLISH), "Could not find Gym with name: \"%1\"");
        i18nMessages.put(new I18nLookup(GYM_NOT_FOUND, SWEDISH), "Kunde inte hitta gym: \"%1\"");

        i18nMessages.put(new I18nLookup(RAID_EXISTS, Locale.ENGLISH),
                "Sorry, %1, a raid at gym %2 already exists (for %3). Sign up for it!");
        i18nMessages.put(new I18nLookup(RAID_EXISTS, SWEDISH),
                "Tyvärr, %1, en raid vid gym %2 finns redan (för %3). Anmäl dig till den?");

        i18nMessages.put(new I18nLookup(MANUAL_RAID, Locale.ENGLISH),
                "**Note: All of these commands must be executed in a server text channel, not in DM!**\n\n" +
                "**To register a new raid:**\n!raid new *[Pokemon]* *[Ends at (HH:MM)]* *[Gym name]*\n" +
                        "*Example:* !raid new entei 09:25 Solna Platform\n\n" +
                        "**Check status for a raid in a gym:**\n!raid status *[Gym name]*\n" +
                        "*Example:* !raid status Solna Platform\n\n" +
                        "**Get a list of all active raids:**\n!raid list\n" +
                        "*Examples:* !raid list Entei - list all raids for Entei.\n" +
                        "!raid list - list all active raids.\n\n" +
                "**Info about a raid boss:**\n!raid vs *[Pokemon]*\n" +
                        "*Example:* !raid vs Entei"
        );
        i18nMessages.put(new I18nLookup(MANUAL_RAID, SWEDISH),
                "**OBS: Alla dessa kommandon måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**För att registrera en raid:**\n!raid new *[Pokemon]* " +
                        "*[Slutar klockan (HH:MM)]* *[Gym-namn]*\n" +
                        "*Exempel:* !raid new entei 09:25 Solna Platform\n\n" +
                        "**Kolla status för en raid:**\n!raid status *[Gym-namn]*\n" +
                        "*Exempel:* !raid status Solna Platform\n\n" +
                        "**Visa alla registrerade raider:**\n!raid list\n" +
                        "*Exempel:* !raid list Entei - visa alla aktiva raider med Entei som boss.\n" +
                        "!raid list - lista alla aktiva raider oavsett boss.\n\n" +
                        "**Information om en raidboss:**\n!raid vs *[Pokemon]*\n" +
                        "*Exempel:* !raid vs Entei"
        );

        i18nMessages.put(new I18nLookup(MANUAL_CHANGE, Locale.ENGLISH),
                "**Note: All of these commands must be executed in a server text channel, not in DM!**\n\n" +
                "**Change endtime for a raid:** !raid change when *[New end of raid (HH:MM)]* *[Pokestop name]* " +
                        "(Only raid creator or server admins may do this)\n" +
                        "*Example:* !raid change when 09:45 Solna Platform\n\n" +
                        "**Change raid boss:** !raid change pokemon *[Pokemon]* *[Pokestop name]* " +
                        "(Only raid creator or server admins may do this)\n" +
                        "*Example:* !raid change pokemon Suicune Solna Platform\n\n" +
                        "**Delete a raid:** !raid change remove *[Pokestop name]* (Only server admins may do this)\n" +
                        "*Example:* !raid change remove Solna Platform"
        );
        i18nMessages.put(new I18nLookup(MANUAL_CHANGE, SWEDISH),
                "**OBS: Alla dessa kommandon måste köras i en servers textkanal, inte i DM!**\n\n" +
                "**Ändra en raids sluttid:** !raid change when *[Ny sluttid (HH:MM)]* *[Gym-namn]* " +
                        "(Endast raidskapare eller admin får göra detta)\n" +
                        "*Exempel:* !raid change when 09:45 Solna Platform\n\n" +
                        "**Ändra en raids boss:** !raid change pokemon *[Pokemon]* *[Pokestop name]* " +
                        "(Endast raidskapare eller admin får göra detta)\n" +
                        "*Exempel:* !raid change pokemon Suicune Solna Platform\n\n" +
                        "**Ta bort en raid:** !raid change remove *[Pokestop name]* (Endast admins får göra detta)\n" +
                        "*Exempel:* !raid change remove Solna Platform"
        );

        i18nMessages.put(new I18nLookup(MANUAL_GROUPS, Locale.ENGLISH),
                "**Note:This command must be executed in a server text channel, not in DM!**\n\n" +
                "**Create a group to run a raid at a certain time:** !raid group {time (HH:MM)} {gym name}\n" +
                        "*Example:* !raid group 09:45 Solna Platform"
                // todo: fix this message
        );
        i18nMessages.put(new I18nLookup(MANUAL_GROUPS, SWEDISH),
                "**OBS: Kommandot måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**Skapa en grupp för att köra raid en viss tid:** !raid group {time (HH:MM)} {gym namn}\n" +
                        "*Exempel:* !raid group 09:45 Solna Platform\n\n" +
                        "Man anmäler sig till en raid via emotes som dyker upp under svaret på kommandot ovan.\n\n" +
                        "1-6 anmäler det antal som står på knappen. " +
                        "Tryck samma knapp igen för att ta bort samma antal.\n\n" +
                        "Meddelandet kommer uppdateras var 15:e sekund med alla som anmäler sig. När tiden gått " +
                        "ut för gruppen, kommer meddelandet tas bort. Dock kommer alla anmälningar att ligga kvar " +
                        "på raidens total, tills man antingen tar bort dem (via !raid remove {gym namn}) eller " +
                        "att raidens tid tar slut.\n\n" +
                        "Eftersom denna funktion är tämligen ny, uppskattas feedback på hur man kan göra den bättre."
        );

        i18nMessages.put(new I18nLookup(MANUAL_INSTALL, Locale.ENGLISH),
                "**Install configuration for this server:** !raid install - starts install process\n" +
        "!raid install server=[server name];region=[region dataset reference];" +
                        "replyInDm=[true or false];locale=[2 char language code]\n" +
        "**Example:** !raid install server=My test server;region=stockholm;replyInDm=false;locale=sv"
        );
        i18nMessages.put(new I18nLookup(MANUAL_INSTALL, SWEDISH),
                "**Installera konfiguration för denna server:** !raid install - startar processen\n" +
                        "!raid install server=[servernamn];region=[region, datasetsreferens];" +
                        "replyInDm=[true eller false];locale=[2 teckens språkkod, t.ex. sv]\n" +
                        "**Exempel:** !raid install server=My test server;region=stockholm;replyInDm=false;locale=sv"
        );

        i18nMessages.put(new I18nLookup(MANUAL_MAP, Locale.ENGLISH),
                "**Note: This command must be executed in a server text channel, not in DM!**\n\n" +
                "**Get map for a certain gym:**\n!raid map *[Gym name]*\n" +
                "*Example:* !raid map Solna Platform\n\n" +
                        "Note: You can click the name of the gym to go to it in Google Maps. There, you " +
                        "can get directions, estimated time of arrival etc."
        );
        i18nMessages.put(new I18nLookup(MANUAL_MAP, SWEDISH),
                "**OBS: Detta kommando måste köras i en servers textkanal, inte i DM!**\n\n" +
                "**Hämta karta för gym:**\n!raid map *[Gym-namn]*\n" +
                "*Exempel:* !raid map Solna Platform\n\n" +
                "OBS: Du kan klicka på gymnamnet för att gå till det i Google Maps. Där kan du sedan få t.ex. " +
                        "vägbeskrivning, beräknad ankomsttid och så vidare."
        );

        i18nMessages.put(new I18nLookup(MANUAL_SIGNUP, Locale.ENGLISH),
                "**Note: All of these commands must be executed in a server text channel, not in DM!**\n\n" +
                "**Sign up for a raid:**\n!raid add *[number of people] [ETA (HH:MM)] [Gym name]*\n" +
                        "*Example:* !raid add 3 09:15 Solna Platform\n\n" +
                        "**Unsign raid:**\n!raid remove *[Gym name]*\n" +
                        "*Example:* !raid remove Solna Platform"
        );
        i18nMessages.put(new I18nLookup(MANUAL_SIGNUP, SWEDISH),
                "**OBS: Alla dessa kommandon måste köras i en servers textkanal, inte i DM!**\n\n" +
                "**Säg att du kommer till en viss raid:**\n!raid add *[antal som kommer] [ETA (HH:MM)] [Gym-namn]*\n" +
                        "*Exempel:* !raid add 3 09:15 Solna Platform\n\n" +
                        "**Ta bort din signup för en raid:**\n!raid remove *[Gym-namn]*\n" +
                        "*Exempel:* !raid remove Solna Platform"
        );

        i18nMessages.put(new I18nLookup(MANUAL_TRACKING, Locale.ENGLISH),
                "**Note: All of these commands must be executed in a server text channel, not in DM!**\n\n" +
                        "**Track new raids for raid boss (Note: *any tracking is reset on bot restart*):**\n" +
                        "!raid track *[Pokemon]*\n" +
                        "*Example:* !raid track Entei\n\n" +
                        "**Untrack raids for raid boss:**\n!raid untrack *[Pokemon]*\n" +
                        "*Example - remove your tracking of Entei:* !raid untrack Entei\n" +
                        "*Example - remove all your tracking:* !raid untrack"
        );
        i18nMessages.put(new I18nLookup(MANUAL_TRACKING, SWEDISH),
                "**OBS: Alla dessa kommandon måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**Övervakning av nya raids för pokemon (OBS: *nollställs om botten startas om*):" +
                        "**\n!raid track *[Pokemon]*\n" +
                        "*Exempel:* !raid track Entei\n\n" +
                        "**Ta bort övervakning av nya raids för pokemon:**\n!raid untrack *[Pokemon]*\n" +
                        "*Exempel - ta bort din övervakning för Entei:* !raid untrack Entei\n" +
                        "*Exempel - ta bort alla dina övervakningar:* !raid untrack"
        );
    }

    // todo: implement saving locale setting for user (will require pay version of Heroku database)
    public Locale getLocaleForUser(User user) {
        return DEFAULT;
    }

    public Locale getLocaleForUser(String username) {
        return DEFAULT;
    }

    public LocaleService(Map<I18nLookup, String> i18nMessages) {
        this.i18nMessages = i18nMessages;
    }

    public void storeMessage(String messageKey, Locale locale, String message) {
        i18nMessages.put(new I18nLookup(messageKey.toUpperCase(), locale), message);
    }

    public String getMessageFor(String messageKey, Locale locale, String ... parameters) {
        final String messageWithoutParameters = getMessageTextToInjectParametersIn(messageKey, locale);
        String messageWithParameters = messageWithoutParameters;
        int i = 1;
        for (String param : parameters) {
            messageWithParameters = messageWithParameters.replaceAll("[%][" + i + "]", param);
            i++;
        }
        return messageWithParameters;
    }

    public String getMessageTextToInjectParametersIn(String messageKey, Locale locale) {
        String message = i18nMessages.get(new I18nLookup(messageKey.toUpperCase(), locale));
        if (message == null || message.length() < 1) {
            message = i18nMessages.get(new I18nLookup(messageKey.toUpperCase(), DEFAULT));
        }
        if (message == null || message.length() < 1) {
            throw new RuntimeException("Could not find text for message key " + messageKey +
                    " - an admin needs to add it to the LocaleService!");
        }
        return message;
    }

    private class I18nLookup {
        private String messageKey;
        private Locale locale;

        public I18nLookup(String messageKey, Locale locale) {
            this.messageKey = messageKey;
            this.locale = locale;
        }

        public String getMessageKey() {
            return messageKey;
        }

        public Locale getLocale() {
            return locale;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof I18nLookup)) return false;

            I18nLookup that = (I18nLookup) o;

            if (messageKey != null ? !messageKey.equals(that.messageKey) : that.messageKey != null) return false;
            return locale != null ? locale.equals(that.locale) : that.locale == null;
        }

        @Override
        public int hashCode() {
            int result = messageKey != null ? messageKey.hashCode() : 0;
            result = 31 * result + (locale != null ? locale.hashCode() : 0);
            return result;
        }
    }

    public static String featuresString_EN =
            "Pokeraidbot - a Discord bot to help Pokémon Go raiders. Raid, map, pokemon info functions.\n\n" +
                    "**Get detailed help about how the bot works:**\n!raid man\n\n" +
                    "**To see one of its features, type the following:** " +
                    "!raid map {name of a raid gym in your vicinity}\n" +
                    "*Example:* !raid map Solna Platform\n\n" +
                    "https://github.com/magnusmickelsson/pokeraidbot to report errors, request features, " +
                    "see screenshots of usage etc.\n\n" +
            "**How do I support development of this bot?**\n!raid donate";
    public static String featuresString_SV =
            "Pokeraidbot - en Discord-bot för att hjälpa Pokémon Go raiders, med t.ex. kartor till gym, raidplanering" +
                    " och information om pokemons.\n\n" +
                    "**Få detaljerad hjälp om hur botten fungerar:**\n!raid man {frivilligt: ämne}\n" +
                    "*Exempel:* !raid man - berättar om hur man använder raid man och vilka hjälpämnen som finns\n\n" +
                    "**För ett exempel på vad botten kan göra, skriv:** !raid map {namn på ett gym i området}\n" +
                    "*Exempel:* !raid map Solna Platform\n\n" +
                    "https://github.com/magnusmickelsson/pokeraidbot för att rapportera fel, önska funktioner, " +
                    "se screenshots av användning etc.\n\n" +
            "**Hur kan jag stödja utveckling av botten?**\n!raid donate\n\n" +
            "**If you want this information in english:**\n!raid usage en";
}
