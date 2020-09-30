package pokeraidbot.domain.config;

import main.BotServerMain;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.infrastructure.jpa.config.UserConfig;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LocaleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleService.class);
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
    public static final String WHERE_GYM_IN_CHAT_HELP = "WHERE_GYM_IN_CHAT_HELP";
    public static final String NEXT_ETA = "NEXT_ETA";
    public static final String TRACKING_NONE_FREE = "TRACKING_NONE_FREE";
    public static final String LAST_UPDATE = "LAST_UPDATE";
    public static final String MOVED_GROUP = "MOVED_GROUP";
    public static final String OVERVIEW_HELP = "OVERVIEW_HELP";
    public static final String OVERVIEW_ATTACH = "OVERVIEW_ATTACH";
    public static final String OVERVIEW_DELETED = "OVERVIEW_DELETED";
    public static final String HELP_USER_CONFIG = "HELP_USER_CONFIG";
    public static final String USER_CONFIG_BAD_SYNTAX = "USER_CONFIG_BAD_SYNTAX";
    public static final String USER_CONFIG_BAD_PARAM = "USER_CONFIG_BAD_PARAM";
    public static final String UNSUPPORTED_LOCALE = "UNSUPPORTED_LOCALE";
    public static final String LOCALE_SET = "LOCALE_SET";
    public static final String MANUAL_CONFIG = "MANUAL_CONFIG";
    public static final String GETTING_STARTED_HELP = "GETTING_STARTED_HELP";
    public static final String PLUS_SIGNUP_FAIL = "PLUS_SIGNUP_FAIL";
    public static final String NO_RAID = "NO_RAID";
    public static final String GROUP_CLEANING_UP = "GROUP_CLEANING_UP";
    public static final String SIGN_BEFORE_NOW = "SIGN_BEFORE_NOW";
    public static final String NOT_EX_RAID = "NOT_EX_RAID";
    public static final String NO_GROUP_BEFORE_RAID = "NO_GROUP_BEFORE_RAID";
    public static final String NEW_RAID_START_HELP = "NEW_RAID_START_HELP";
    public static final String GROUP_NOT_ADDED = "GROUP_NOT_ADDED";
    public static final String MANY_GROUPS_FOR_RAID = "MANY_GROUPS_FOR_RAID";
    public static final String NO_SUCH_GROUP = "NO_SUCH_GROUP";
    public static final String OVERVIEW_EXISTS = "OVERVIEW_EXISTS";
    public static final String TIME_NOT_IN_RAID_TIMESPAN = "TIME_NOT_IN_RAID_TIMESPAN";
    public static final String TOO_MANY_GROUPS = "TOO_MANY_GROUPS";
    public static final String NO_GROUP_BEFORE_NOW = "NO_GROUP_BEFORE_NOW";
    public static final String UNSIGN = "UNSIGN";
    public static final String OVERVIEW_CLEARED = "OVERVIEW_CLEARED";
    public static final String EGG_HATCH_HELP = "EGG_HATCH_HELP";
    public static final String EGG_ALREADY_HATCHED = "EGG_ALREADY_HATCHED";
    public static final String EGG_WRONG_TIER = "EGG_WRONG_TIER";
    public static final String USER_NICK_INVALID = "USER_NICK_INVALID";
    public static final String RAID_CREATE_AND_GROUP_HELP = "RAID_CREATE_AND_GROUP_HELP";
    public static final String GROUP_DELETED = "GROUP_DELETED";
    public static final String CREATED_BY = "CREATED_BY";
    public static final String COULD_NOT_ADD_GYM = "COULD_NOT_ADD_GYM";
    public static final String HELP = "HELP";
    public static final String EX_WITHOUT_RAID = "EX_WITHOUT_RAID";
    public static final String ALL_EX = "ALL_EX";

    // Change this if you want another default locale, affects the usage texts etc
    public static Locale DEFAULT = Locale.ENGLISH;
    public static Locale[] SUPPORTED_LOCALES = {SWEDISH, Locale.ENGLISH};
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
    public static final String HELP_MANUAL_HELP_TEXT = "HELP_MANUAL_HELP_TEXT";
    public static final String EX_DATE_LIMITS = "EX_DATE_LIMITS";
    public static final String CHANGE_RAID_HELP = "CHANGE_RAID_HELP";
    public static final String EX_NO_CHANGE_POKEMON = "EX_NO_CHANGE_POKEMON";
    public static final String EX_CANT_CHANGE_RAID_TYPE = "EX_CANT_CHANGE_RAID_TYPE";
    public static final String SIGNUP_BAD_NUMBER = "SIGNUP_BAD_NUMBER";
    public static final String EMOTE_INSTALLED_ALREADY = "EMOTE_INSTALLED_ALREADY";
    public static final String GETTING_HERE = "GETTING_HERE";
    public static final String WHO_ARE_COMING = "WHO_ARE_COMING";
    public static final String ONLY_ADMINS_REMOVE_RAID = "ONLY_ADMINS_REMOVE_RAID";
    public static final String RAID_NOT_EXISTS = "RAID_NOT_EXISTS";
    public static final String BAD_SYNTAX = "BAD_SYNTAX";
    public static final String BAD_DATETIME_FORMAT = "BAD_DATETIME_FORMAT";
    public static final String RAID_DETAILS = "RAID_DETAILS";
    public static final String UPDATED_EVERY_X = "UPDATED_EVERY_X";
    public static final String NO_PERMISSION = "NO_PERMISSION";
    public static final String GOOGLE_MAPS = "GOOGLE_MAPS";
    public static final String KEEP_CHAT_CLEAN = "KEEP_CHAT_CLEAN";
    public static final String ERROR_KEEP_CHAT_CLEAN = "ERROR_KEEP_CHAT_CLEAN";
    public static final String WHATS_NEW_HELP = "WHATS_NEW_HELP";
    public static final String NO_CONFIG = "NO_CONFIG";
    private final UserConfigRepository userConfigRepository;

    private Map<I18nLookup, String> i18nMessages = new HashMap<>();

    public LocaleService(Map<I18nLookup, String> i18nMessages, UserConfigRepository userConfigRepository) {
        this.i18nMessages = i18nMessages;
        this.userConfigRepository = userConfigRepository;
    }

    public LocaleService(String locale, UserConfigRepository userConfigRepository) {
        this.userConfigRepository = userConfigRepository;
        LOGGER.info("Initialize with server default locale: " + locale);
        final Locale forLanguageTag = Locale.forLanguageTag(locale);
        if (!new HashSet<>(Arrays.asList(SUPPORTED_LOCALES)).contains(forLanguageTag)) {
            throw new IllegalStateException("Given system locale " + locale + " is not in list of " +
                    "supported locales (" + SUPPORTED_LOCALES + "). Add it, and associated texts!");
        }
        DEFAULT = forLanguageTag;
        initTexts();
        LOGGER.info("Initialized. Got " + i18nMessages.keySet().size() + " texts for " + SUPPORTED_LOCALES.length + " locales.");
    }

    private void initTexts() {
        i18nMessages.put(new I18nLookup(ALL_EX, Locale.ENGLISH),
                "All EX-gyms for the region ");
        i18nMessages.put(new I18nLookup(ALL_EX, SWEDISH),
                "Alla EX-gym för regionen ");

        i18nMessages.put(new I18nLookup(EX_WITHOUT_RAID, Locale.ENGLISH),
                "Potential EX gyms without a scheduled EX-raid:");
        i18nMessages.put(new I18nLookup(EX_WITHOUT_RAID, SWEDISH),
                "Potentiella EX-gym som inte har en kommande EX-raid:");

        i18nMessages.put(new I18nLookup(HELP, Locale.ENGLISH),
                "Help");
        i18nMessages.put(new I18nLookup(HELP, SWEDISH),
                "Hjälp");

        i18nMessages.put(new I18nLookup(COULD_NOT_ADD_GYM, Locale.ENGLISH),
                "Could not add gym, it already exists for this region.");
        i18nMessages.put(new I18nLookup(COULD_NOT_ADD_GYM, SWEDISH),
                "Kunde inte lägga till gym, det finns redan för den här regionen.");

        i18nMessages.put(new I18nLookup(CREATED_BY, Locale.ENGLISH),
                "Created by"
        );
        i18nMessages.put(new I18nLookup(CREATED_BY, SWEDISH),
                "Skapad av"
        );
        i18nMessages.put(new I18nLookup(GROUP_DELETED, Locale.ENGLISH),
                "Group deleted, including any signups."
        );
        i18nMessages.put(new I18nLookup(GROUP_DELETED, SWEDISH),
                "Grupp borttagen, inklusive alla eventuella anmälningar."
        );
        i18nMessages.put(new I18nLookup(NO_SUCH_GROUP, Locale.ENGLISH),
                "No such group exists."        );
        i18nMessages.put(new I18nLookup(NO_SUCH_GROUP, SWEDISH),
                "Det fanns ingen sådan grupp."
        );

        i18nMessages.put(new I18nLookup(TOO_MANY_GROUPS, Locale.ENGLISH),
                "There is already a group created for this raid by this user. In later releases, we may allow " +
                        "creating many groups per raid per user again."
        );
        i18nMessages.put(new I18nLookup(TOO_MANY_GROUPS, SWEDISH),
                "Det finns redan en grupp för denna raid av denna användare. I framtida releaser, kan vi komma att " +
                        "tillåta att skapa flera grupper per raid per användare igen."
        );
        i18nMessages.put(new I18nLookup(MANY_GROUPS_FOR_RAID, Locale.ENGLISH),
                "There are several groups this user can change for raid %1 - " +
                        "Use *!raid change group remove {group starttime} {gym}* to remove the group."
        );
        i18nMessages.put(new I18nLookup(MANY_GROUPS_FOR_RAID, SWEDISH),
                "Det finns flera grupper denna användare kan ändra för raiden %1 - " +
                        "använd *!raid change group remove {grupptid} {gym}* för att ta bort gruppen."
        );
        i18nMessages.put(new I18nLookup(TIME_NOT_IN_RAID_TIMESPAN, Locale.ENGLISH),
                "There were several possible groups to change, so couldn't perform this change. " +
                        "Workaround: remove the group's discord messages and create a new group with the correct time."
        );
        i18nMessages.put(new I18nLookup(TIME_NOT_IN_RAID_TIMESPAN, SWEDISH),
                "Det fanns flera möjliga grupper att ändra på, så kan inte göra detta. " +
                        "Workaround: ta bort gruppens meddelanden och skapa en ny grupp med rätt tid."
        );
        i18nMessages.put(new I18nLookup(TIME_NOT_IN_RAID_TIMESPAN, Locale.ENGLISH),
                "Time %1 is not within the timespan of the raid (%2 - %3)"
        );
        i18nMessages.put(new I18nLookup(TIME_NOT_IN_RAID_TIMESPAN, SWEDISH),
                "Tiden %1 är inte inom raidens giltiga tid (%2 - %3)"
        );

        i18nMessages.put(new I18nLookup(OVERVIEW_CLEARED, Locale.ENGLISH),
                "Server overview message ID cleared."
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_CLEARED, SWEDISH),
                "Serverns översiktmeddelande rensat från konfigurationen."
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_EXISTS, Locale.ENGLISH),
                "Server already has an overview command. Don't run this command again," +
                        " except if the overview message stops updating (saved ID can be cleared via *!raid overview reset*)."
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_EXISTS, SWEDISH),
                "Servern har redan en översikt. Kör inte detta kommando igen, om inte översikten slutar uppdateras" +
                        " (sparad ID kan rensas via *!raid overview reset*)."
        );
        i18nMessages.put(new I18nLookup(GROUP_NOT_ADDED, Locale.ENGLISH),
                "There is already a raidgroup at this time for %1"
        );
        i18nMessages.put(new I18nLookup(GROUP_NOT_ADDED, SWEDISH),
                "Det finns redan en grupp för denna tid för %1"
        );
        i18nMessages.put(new I18nLookup(EGG_ALREADY_HATCHED, Locale.ENGLISH),
                "This raid has already been reported as hatched, the pokemon is %1. " +
                        "If it's wrong, change via *!raid change pokemon {pokemon name} {gym}*.");
        i18nMessages.put(new I18nLookup(EGG_ALREADY_HATCHED, SWEDISH),
                "Den här raiden har redan rapporterats som kläckt, bossen är %1. " +
                        "Om det inte stämmer, ändra via *!raid change pokemon {pokemon name} {gym}* " +
                        "(admins eller raidskapare).");
        i18nMessages.put(new I18nLookup(EGG_WRONG_TIER, Locale.ENGLISH),
                "Attempt to hatch a raid boss of a different tier than the reported egg!");
        i18nMessages.put(new I18nLookup(EGG_WRONG_TIER, SWEDISH),
                "Försök att kläcka en raidboss av annan nivå än det rapporterade ägget! Om det är fel typ av ägg, " +
        "ändra via *!raid change pokemon Egg{rätt nivå} {gym}* ..");

        i18nMessages.put(new I18nLookup(EGG_HATCH_HELP, Locale.ENGLISH),
                "Report the hatch of a reported egg - !raid hatch [Name of Pokemon] [Gym name]");
        i18nMessages.put(new I18nLookup(EGG_HATCH_HELP, SWEDISH),
                "Rapportera att ett rapporterat ägg kläckts - !raid hatch [Pokemon] [Gym]");
        i18nMessages.put(new I18nLookup(NEW_RAID_START_HELP, Locale.ENGLISH),
                "Create new raid starting at time - !raid start [Name of Pokemon] [Start (HH:MM)] [Gym name]");
        i18nMessages.put(new I18nLookup(NEW_RAID_START_HELP, SWEDISH),
                "Skapa ny raid som startar vid viss tid - !raid start [Pokemon] [Start klockan (HH:MM)] [Gym]");
        i18nMessages.put(new I18nLookup(RAID_CREATE_AND_GROUP_HELP, Locale.ENGLISH),
                "Create new raid and group starting at time - !raid start-group [Name of Pokemon] " +
                        "[Start (HH:MM)] [Gym name]");
        i18nMessages.put(new I18nLookup(RAID_CREATE_AND_GROUP_HELP, SWEDISH),
                "Skapa ny raid och grupp som startar vid viss tid - !raid start-group [Pokemon] " +
                        "[Start klockan (HH:MM)] [Gym]");
        i18nMessages.put(new I18nLookup(NO_GROUP_BEFORE_NOW, Locale.ENGLISH),
                "Can't set a raid group to start at %1, which is before current time %2."
        );
        i18nMessages.put(new I18nLookup(NO_GROUP_BEFORE_NOW, SWEDISH),
                "Kan inte sätta en raidgrupp att börja vid %1, eftersom klockan är %2."
        );
        i18nMessages.put(new I18nLookup(NO_GROUP_BEFORE_RAID, Locale.ENGLISH),
                "Can't set a raid group to start at %1, which is before raid start at %2."
        );
        i18nMessages.put(new I18nLookup(NO_GROUP_BEFORE_RAID, SWEDISH),
                "Kan inte sätta en raidgrupp att börja vid %1, eftersom raiden börjar %2."
        );
        i18nMessages.put(new I18nLookup(NOT_EX_RAID, Locale.ENGLISH),
                "%1 is not an EX raid boss. Use *!raid new* command instead to create a standard raid."
        );
        i18nMessages.put(new I18nLookup(NOT_EX_RAID, SWEDISH),
                "%1 är inte en EX raid boss. Använd *!raid new* istället för att skapa en vanlig raid."
        );
        i18nMessages.put(new I18nLookup(GROUP_CLEANING_UP, Locale.ENGLISH),
                "This group is about to be removed. " +
                        "Create a new group via *!raid group {time (HH:MM)} {gym name}*"
        );
        i18nMessages.put(new I18nLookup(GROUP_CLEANING_UP, SWEDISH),
                "Detta meddelande är på gång att städas undan. " +
                        "Skapa en ny grupp via *!raid group {tid (HH:MM)} {gym}*"
        );
        i18nMessages.put(new I18nLookup(GETTING_STARTED_HELP, Locale.ENGLISH),
                "Getting started guide for the bot"
        );
        i18nMessages.put(new I18nLookup(GETTING_STARTED_HELP, SWEDISH),
                "Kom-igång-guide för pokeraidbot"
        );
        i18nMessages.put(new I18nLookup(LOCALE_SET, Locale.ENGLISH),
                "Locale set to: %1"
        );
        i18nMessages.put(new I18nLookup(LOCALE_SET, SWEDISH),
                "Locale (språk) satt till: %1"
        );
        i18nMessages.put(new I18nLookup(UNSUPPORTED_LOCALE, Locale.ENGLISH),
                "You tried to set an unsupported locale: %1. Supported locales are: " +
                        StringUtils.join(LocaleService.SUPPORTED_LOCALES, ", ")
        );
        i18nMessages.put(new I18nLookup(UNSUPPORTED_LOCALE, SWEDISH),
                "Du försökte sätta en locale som inte stödjs: %1. Tillgängliga locales är: " +
                        StringUtils.join(LocaleService.SUPPORTED_LOCALES, ", ")
        );

        i18nMessages.put(new I18nLookup(USER_NICK_INVALID, Locale.ENGLISH),
                "Invalid nickname, needs to be between 2 and 11 chars."
        );
        i18nMessages.put(new I18nLookup(USER_NICK_INVALID, SWEDISH),
                "Ogiltigt smeknamn, ska vara mellan 2 och 11 tecken."
        );
        i18nMessages.put(new I18nLookup(USER_CONFIG_BAD_PARAM, Locale.ENGLISH),
                "The only parameters that can be changed right now via this command is locale and nick. " +
                        "You tried to set %1."
        );
        i18nMessages.put(new I18nLookup(USER_CONFIG_BAD_PARAM, SWEDISH),
                "De enda parametrarna som kan ändras just nu via detta kommando är språk (locale) och smeknamn. " +
                        "Du försökte sätta parametern %1."
        );
        i18nMessages.put(new I18nLookup(USER_CONFIG_BAD_SYNTAX, Locale.ENGLISH),
                "Bad syntax. To see user's configuration: *!raid config show*\n" +
                        "To change: *!raid config {param}={value}*"
        );
        i18nMessages.put(new I18nLookup(USER_CONFIG_BAD_SYNTAX, SWEDISH),
                "Felaktigt kommando. För att visa användarens konfiguration: *!raid config show*\n" +
                        "För att ändra den: *!raid config {param}={value}*"
        );
        i18nMessages.put(new I18nLookup(HELP_USER_CONFIG, Locale.ENGLISH),
                "Get or change user configuration - !raid config show to display, " +
                        "!raid config {param}={value} to change."
        );
        i18nMessages.put(new I18nLookup(HELP_USER_CONFIG, SWEDISH),
                "Användarkonfiguration - !raid config show för att visa, " +
                        "!raid config {param}={value} för att ändra."
        );
        i18nMessages.put(new I18nLookup(MOVED_GROUP, Locale.ENGLISH),
                "Moved group from %1 to %2 for raid at %3.\n" +
                        "**Note: all signups for this time are moved to the new time.** " +
                        "Users who don't want to be moved have to run " +
                        "*!raid remove %3* and then add their signup again."
        );
        i18nMessages.put(new I18nLookup(MOVED_GROUP, SWEDISH),
                "Flyttade grupp från %1 till %2 för raid vid %3.\n" +
                        "**OBS: Alla gjorda anmälningar för denna tid flyttas med.** " +
                        "Vill du inte det - ändra din anmälning, t.ex. via *!raid remove %3* och lägg till dig igen."
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_DELETED, Locale.ENGLISH),
                "Overview message has been removed by someone. " +
                        "Run *!raid overview* again to create a new one."
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_DELETED, SWEDISH),
                "Översiktsmeddelandet har tagits bort av någon. " +
                        "Kör *!raid overview* igen för att skapa ett nytt."
        );
        i18nMessages.put(new I18nLookup(LAST_UPDATE, Locale.ENGLISH),
                "Last update: %1"
        );
        i18nMessages.put(new I18nLookup(LAST_UPDATE, SWEDISH),
                "Senast uppdaterad: %1"
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_ATTACH, Locale.ENGLISH),
                "Pokeraidbot v" + BotServerMain.version + " is here. Raid overview will be updated in channel " +
                        "#%1. For bot info, type: *!raid usage*"
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_ATTACH, SWEDISH),
                "Pokeraidbot v" + BotServerMain.version + " är här. Raidöversikten uppdateras i kanalen " +
                        "#%1. För info om botten: *!raid usage*"
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_HELP, Locale.ENGLISH),
                "Create a message with a !raid list overview that automatically updates: !raid overview"
        );
        i18nMessages.put(new I18nLookup(OVERVIEW_HELP, SWEDISH),
                "Skapa ett meddelande med en !raid list översikt som automatiskt uppdateras: !raid overview"
        );

        i18nMessages.put(new I18nLookup(TRACKING_NONE_FREE, Locale.ENGLISH),
                "There are no free spots for pokemon tracking in your user configuration (3 spots filled). " +
                        "Please remove one or more via *!raid untrack {pokemon}*"
        );
        i18nMessages.put(new I18nLookup(TRACKING_NONE_FREE, SWEDISH),
                "Alla dina 3 trackingpokemon är upptagna. Ta bort en eller fler via *!raid untrack*"
        );

        i18nMessages.put(new I18nLookup(NEXT_ETA, Locale.ENGLISH),
                "next ETA: %1"
        );
        i18nMessages.put(new I18nLookup(NEXT_ETA, SWEDISH),
                "närmaste ETA: %1"
        );

        i18nMessages.put(new I18nLookup(NO_CONFIG, Locale.ENGLISH),
                "There is no configuration setup for this server. Make sure an administrator runs the command" +
                        " \"!raid install\"."
        );
        i18nMessages.put(new I18nLookup(NO_CONFIG, SWEDISH),
                "Det finns ingen konfiguration installerad för denna server. " +
                        "Se till att en administratör kör kommandot \"!raid install\"."
        );

        i18nMessages.put(new I18nLookup(WHATS_NEW_HELP, Locale.ENGLISH),
                "Gives the version of the bot and what's new in this version."
        );
        i18nMessages.put(new I18nLookup(WHATS_NEW_HELP, SWEDISH),
                "Ange vilken version av botten som körs, och vad som är nytt i denna version."
        );
        i18nMessages.put(new I18nLookup(ERROR_KEEP_CHAT_CLEAN, Locale.ENGLISH),
                "This message, and the associated error, will be removed in %1 seconds to keep chat clean."
        );
        i18nMessages.put(new I18nLookup(ERROR_KEEP_CHAT_CLEAN, SWEDISH),
                "Detta meddelande och tillhörande fel kommer tas bort om %1 sekunder " +
                        "för att hålla chatten ren."
        );
        i18nMessages.put(new I18nLookup(KEEP_CHAT_CLEAN, Locale.ENGLISH),
                "This message will be removed in %1 seconds to keep chat clean."
        );
        i18nMessages.put(new I18nLookup(KEEP_CHAT_CLEAN, SWEDISH),
                "Detta meddelande kommer tas bort om %1 sekunder " +
                        "för att hålla chatten ren."
        );
        i18nMessages.put(new I18nLookup(GOOGLE_MAPS, Locale.ENGLISH),
                " click the message title for a Google Maps link."
        );
        i18nMessages.put(new I18nLookup(GOOGLE_MAPS, SWEDISH),
                " klicka på meddelandetitel för Google Maps."
        );
        i18nMessages.put(new I18nLookup(NO_PERMISSION, Locale.ENGLISH),
                "You lack the permissions to do what you're trying to do. Ask an admin for help."
        );
        i18nMessages.put(new I18nLookup(NO_PERMISSION, SWEDISH),
                "Du saknar behörighet för att göra det du försökte göra. Be hjälp av en admin."
        );
        i18nMessages.put(new I18nLookup(UPDATED_EVERY_X, Locale.ENGLISH),
                "Updated every %2 %1."
        );
        i18nMessages.put(new I18nLookup(UPDATED_EVERY_X, SWEDISH),
                "Uppdateras var %2:e %1."
        );

        i18nMessages.put(new I18nLookup(RAID_DETAILS, Locale.ENGLISH),
                "To see details for a raid: !raid status {gym name}"
        );
        i18nMessages.put(new I18nLookup(RAID_DETAILS, SWEDISH),
                "För att se detaljer för en raid: !raid status {gym-namn}"
        );

        i18nMessages.put(new I18nLookup(BAD_DATETIME_FORMAT, Locale.ENGLISH),
                "Could not parse your given time, should be format %1 but was: %2"
        );
        i18nMessages.put(new I18nLookup(BAD_DATETIME_FORMAT, SWEDISH),
                "Kunde inte tolka tiden du angav, ska vara format %1 men var: %2"
        );

        i18nMessages.put(new I18nLookup(BAD_SYNTAX, Locale.ENGLISH),
                "Bad syntax for command. Refer to *!raid man*. Correct command: %1"
        );
        i18nMessages.put(new I18nLookup(BAD_SYNTAX, SWEDISH),
                "Dålig syntax för kommandot. Se *!raid man* vid behov. Korrekt kommando ser ut som: %1"
        );

        i18nMessages.put(new I18nLookup(RAID_NOT_EXISTS, Locale.ENGLISH),
                "Could not delete raid since you tried to delete one that doesn't exist."
        );
        i18nMessages.put(new I18nLookup(RAID_NOT_EXISTS, SWEDISH),
                "Kunde inte ta bort raid, eftersom den fanns inte."        );

        i18nMessages.put(new I18nLookup(ONLY_ADMINS_REMOVE_RAID, Locale.ENGLISH),
                "Only admins can remove raids with signups, sorry."
        );
        i18nMessages.put(new I18nLookup(ONLY_ADMINS_REMOVE_RAID, SWEDISH),
                "Bara administratörer kan ta bort raids med gjorda anmälningar, tyvärr."
        );
        i18nMessages.put(new I18nLookup(WHO_ARE_COMING, Locale.ENGLISH),
                "Who are coming"
        );
        i18nMessages.put(new I18nLookup(WHO_ARE_COMING, SWEDISH),
                "Vilka kommer"
        );

        i18nMessages.put(new I18nLookup(GETTING_HERE, Locale.ENGLISH),
                "Getting here:"
        );
        i18nMessages.put(new I18nLookup(GETTING_HERE, SWEDISH),
                "Hitta hit:"
        );
        i18nMessages.put(new I18nLookup(EMOTE_INSTALLED_ALREADY, Locale.ENGLISH),
                "You already have an icon with the name \"%1\"."
        );
        i18nMessages.put(new I18nLookup(EMOTE_INSTALLED_ALREADY, SWEDISH),
                "Du har redan installerat emote för: \"%1\"."
        );
        i18nMessages.put(new I18nLookup(SIGNUP_BAD_NUMBER, Locale.ENGLISH),
                "Number of people for a signup must be 1-20, you had %1 but tried to set %2."
        );
        i18nMessages.put(new I18nLookup(SIGNUP_BAD_NUMBER, SWEDISH),
                "Antal personer för en signup måste vara 1-20, du hade %1 men försökte sätta %2."
        );
        i18nMessages.put(new I18nLookup(EX_CANT_CHANGE_RAID_TYPE, Locale.ENGLISH),
                "Can't change a standard raid to be an EX raid. " +
                        "Remove the standard raid and then create an EX raid instead. Refer to !raid man change"
        );
        i18nMessages.put(new I18nLookup(EX_CANT_CHANGE_RAID_TYPE, SWEDISH),
                "Kan inte ändra en vanlig raid till att bli en EX raid. " +
                        "Ta bort den vanliga raiden och skapa en ny EX raid. Använd !raid man change"
        );
        i18nMessages.put(new I18nLookup(EX_NO_CHANGE_POKEMON, Locale.ENGLISH),
                "Can't change pokemon for an EX raid. If you want to change an EX raid, remove it and create a new " +
                        "raid via: !raid change remove {gym}"
        );
        i18nMessages.put(new I18nLookup(EX_NO_CHANGE_POKEMON, SWEDISH),
                "Kan inte ändra pokemon för en EX raid. " +
                        "Om du vill ändra EX raiden, ta bort den och skapa en ny. Använd !raid man change"
        );
        i18nMessages.put(new I18nLookup(CHANGE_RAID_HELP, Locale.ENGLISH),
                " Change something that went wrong during raid creation. Type \"!raid man change\" for details."
        );
        i18nMessages.put(new I18nLookup(CHANGE_RAID_HELP, SWEDISH),
                " Ändra något som blev fel vid skapandet av en raid. Skriv \"!raid man change\" för detaljer."
        );

        i18nMessages.put(new I18nLookup(EX_DATE_LIMITS, Locale.ENGLISH),
                "You can't create an EX raid more than 10 days ahead."
        );
        i18nMessages.put(new I18nLookup(EX_DATE_LIMITS, SWEDISH),
                "Du kan inte skapa en EX raid mer än 10 dagar framåt tyvärr."
        );

        i18nMessages.put(new I18nLookup(NO_EMOTES, Locale.ENGLISH),
                "Administrator has not installed pokeraidbot's emotes. " +
                        "Ensure he/she runs the following command: !raid install-emotes"
        );
        i18nMessages.put(new I18nLookup(NO_EMOTES, SWEDISH),
                "Administratören för denna server har inte installerat pokeraidbot's emotes. " +
                        "Se till att hen kör följande kommando: !raid install-emotes"
        );

        i18nMessages.put(new I18nLookup(SIGNED_UP_AT, Locale.ENGLISH),
                "Signed up"
        );
        i18nMessages.put(new I18nLookup(SIGNED_UP_AT, SWEDISH),
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
                "%1 @ %2" //, starts at %3"
        );
        i18nMessages.put(new I18nLookup(GROUP_HEADLINE, SWEDISH),
                "%1 @ %2" //, startar %3"
        );

        i18nMessages.put(new I18nLookup(HANDLE_SIGNUP, Locale.ENGLISH),
                "To sign up, press emotes below for number of people to sign up.");
        i18nMessages.put(new I18nLookup(HANDLE_SIGNUP, SWEDISH),
                "För anmälan, tryck emotes nedan motsvarande antal som kommer.");

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
                "%3 by %2- raid track notification for server %1");
        i18nMessages.put(new I18nLookup(TRACKED_RAID, SWEDISH),
                "%3 av %2 - notifikation från %1");

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
                        "Please check configuration and/or notify administrator! You have imported the gymdata, right?");
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

        i18nMessages.put(new I18nLookup(SIGN_BEFORE_NOW, Locale.ENGLISH),
                "Can't sign up for this raid before current time. Your given time is %1, time is currently %2");
        i18nMessages.put(new I18nLookup(SIGN_BEFORE_NOW, SWEDISH),
                "Du kan inte anmäla dig att anlända innan nuvarande tid. Din ETA är %1, men klockan är %2.");

        i18nMessages.put(new I18nLookup(SIGN_BEFORE_RAID, Locale.ENGLISH),
                "Can't sign up for this raid before raid start. Your given time is %1, raid start is %2");
        i18nMessages.put(new I18nLookup(SIGN_BEFORE_RAID, SWEDISH),
                "Du kan inte anmäla dig att anlända innan raiden börjar. Din ETA är %1, men raiden börjar %2.");

        i18nMessages.put(new I18nLookup(NO_ETA_AFTER_RAID, Locale.ENGLISH),
                "Can't arrive after raid has ended. Your given time is %1, raid ends at %2");
        i18nMessages.put(new I18nLookup(NO_ETA_AFTER_RAID, SWEDISH),
                "Det är väl inte så lämpligt att anlända efter att raiden slutat? Din ETA är %1, raiden slutar %2.");

        i18nMessages.put(new I18nLookup(NO_RAID_TOO_LONG, Locale.ENGLISH),
                "You can't set an end of raid time which is later than %3 hours from the current time " +
                        "%2 except for EX raids - your input would have yielded end time %1.");
        i18nMessages.put(new I18nLookup(NO_RAID_TOO_LONG, SWEDISH),
                "Du kan inte sätta en sluttid för raid senare än %3 timmar från vad klockan är nu (%2), " +
                        "förutom för EX raider. Rapporterad sluttid skulle blivit %1.");

        i18nMessages.put(new I18nLookup(NO_RAIDS_NOW, Locale.ENGLISH),
                "You can't create raids between 22:00 and 06:00 - your time was %1.");
        i18nMessages.put(new I18nLookup(NO_RAIDS_NOW, SWEDISH),
                "Du kan inte skapa en raid som slutar mellan 22.00 och 06:00. Du angav %1.");

        i18nMessages.put(new I18nLookup(TIMEZONE, Locale.ENGLISH),
                "You seem to be living in a different timezone. Your input was %1, while it's currently %2.");
        i18nMessages.put(new I18nLookup(TIMEZONE, SWEDISH),
                "Fel tidszon? " +
                        "Du angav %1, och klockan är just nu %2.");

        i18nMessages.put(new I18nLookup(NO_POKEMON, Locale.ENGLISH), "Could not find a pokemon with name \"%1\".");
        i18nMessages.put(new I18nLookup(NO_POKEMON, SWEDISH), "Kunde inte hitta pokemon med namn \"%1\".");

        i18nMessages.put(new I18nLookup(ALREADY_SIGNED_UP, Locale.ENGLISH), "You're already signed up for: %1 ... " +
                "%2 - remove your current signup then signup again.");
        i18nMessages.put(new I18nLookup(ALREADY_SIGNED_UP, SWEDISH), "Du har redan anmält dig till raid vid %1 ... " +
                "%2 - ta bort din anmälan och gör om som du vill ha det.");

        i18nMessages.put(new I18nLookup(WHERE_GYM_IN_CHAT_HELP, Locale.ENGLISH), "Get map link for gym in chat - !raid mapinchat [Gym name]");
        i18nMessages.put(new I18nLookup(WHERE_GYM_IN_CHAT_HELP, SWEDISH), "Visa karta för gym i chatten - !raid mapinchat [Gym]");

        i18nMessages.put(new I18nLookup(WHERE_GYM_HELP, Locale.ENGLISH), "Get map link for gym - !raid map [Gym name]");
        i18nMessages.put(new I18nLookup(WHERE_GYM_HELP, SWEDISH), "Visa karta för gym - !raid map [Gym]");

        i18nMessages.put(new I18nLookup(SIGNUPS, Locale.ENGLISH), "%1 sign up added to %2. %3");
        i18nMessages.put(new I18nLookup(SIGNUPS, SWEDISH), "Anmälan från %1 registrerad till %2. %3");

        i18nMessages.put(new I18nLookup(UNSIGN, Locale.ENGLISH), "%1 unsign from %2. %3");
        i18nMessages.put(new I18nLookup(UNSIGN, SWEDISH), "%1 avanmälde från %2. %3");

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

        i18nMessages.put(new I18nLookup(NO_SIGNUP_AT_GYM, Locale.ENGLISH), "%1 had no signup to remove for gym %2");
        i18nMessages.put(new I18nLookup(NO_SIGNUP_AT_GYM, SWEDISH), "%1 hade ingen anmälan att ta bort för raid vid %2");

        i18nMessages.put(new I18nLookup(SIGNUP_REMOVED, Locale.ENGLISH), "Signup removed for gym %1: %2");
        i18nMessages.put(new I18nLookup(SIGNUP_REMOVED, SWEDISH), "Tog bort din anmälan för gym %1: %2");

        i18nMessages.put(new I18nLookup(REMOVE_SIGNUP_HELP, Locale.ENGLISH),
                "Remove your signup for this gym: !raid remove [Gym]");
        i18nMessages.put(new I18nLookup(REMOVE_SIGNUP_HELP, SWEDISH),
                "Ta bort din anmälan för raid på ett gym: !raid remove [Gym]");

        i18nMessages.put(new I18nLookup(NO_RAID_AT_GYM, Locale.ENGLISH),
                "Could not find a raid for this gym: \"%1\".");
        i18nMessages.put(new I18nLookup(NO_RAID_AT_GYM, SWEDISH),
                "Kunde inte hitta någon aktuell raid för \"%1\".");

        i18nMessages.put(new I18nLookup(NO_RAID, Locale.ENGLISH),
                "Could not find the target raid.");
        i18nMessages.put(new I18nLookup(NO_RAID, SWEDISH),
                "Kunde inte hitta den aktuella raiden.");

        i18nMessages.put(new I18nLookup(RAIDSTATUS, Locale.ENGLISH), "%1:");
        i18nMessages.put(new I18nLookup(RAIDSTATUS, SWEDISH), "%1:");

        i18nMessages.put(new I18nLookup(RAIDSTATUS_HELP, Locale.ENGLISH),
                "Check status for raid - !raid status [Gym name].");
        i18nMessages.put(new I18nLookup(RAIDSTATUS_HELP, SWEDISH),
                "Se status för raid - !raid status [Gym].");

        i18nMessages.put(new I18nLookup(SIGNED_UP, Locale.ENGLISH), "Signed up");
        i18nMessages.put(new I18nLookup(SIGNED_UP, SWEDISH), "Anmäld(a)");

        i18nMessages.put(new I18nLookup(CURRENT_RAIDS, Locale.ENGLISH), "Current raids");
        i18nMessages.put(new I18nLookup(CURRENT_RAIDS, SWEDISH), "Aktuella raids");

        i18nMessages.put(new I18nLookup(RAID_BETWEEN, Locale.ENGLISH), "%1-%2");
        i18nMessages.put(new I18nLookup(RAID_BETWEEN, SWEDISH), "%1-%2");

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
        i18nMessages.put(new I18nLookup(LIST_HELP, SWEDISH), "Visa aktuella raids - " +
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

        i18nMessages.put(new I18nLookup(RAID_TOSTRING, Locale.ENGLISH), "Raid for %1 at gym %2, from %3 to %4");
        i18nMessages.put(new I18nLookup(RAID_TOSTRING, SWEDISH), "%1 vid %2, från %3 till %4");

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

        i18nMessages.put(new I18nLookup(GYM_NOT_FOUND, Locale.ENGLISH), "Could not find Gym with name \"%1\" in region %2");
        i18nMessages.put(new I18nLookup(GYM_NOT_FOUND, SWEDISH), "Kunde inte hitta gym med namn \"%1\" i regionen %2");

        i18nMessages.put(new I18nLookup(RAID_EXISTS, Locale.ENGLISH),
                "Sorry, %1, a raid at gym %2 already exists (for %3). Sign up for it!");
        i18nMessages.put(new I18nLookup(RAID_EXISTS, SWEDISH),
                "Tyvärr, %1, en raid vid gym %2 finns redan (för %3). Anmäl dig till den?");

        i18nMessages.put(new I18nLookup(HELP_MANUAL_HELP_TEXT, Locale.ENGLISH),
                " Help manual for different topics: !raid man {topic} {optional:chan/dm - " +
                        "used when an admin wants to show a user the syntax of commands}\n" +
                        "Available topics: raid, signup, map, install, change, tracking, group, ALL.\n" +
                        "**Example (to get help about raid commands):** !raid man raid"
        );
        i18nMessages.put(new I18nLookup(HELP_MANUAL_HELP_TEXT, SWEDISH),
                " Hjälpmanual för olika ämnen: !raid man {ämne} {frivilligt:chan/dm - " +
                        "om man t.ex. vill visa hjälpen i en textkanal för en användare}\n" +
                        "Möjliga ämnen: raid, signup, map, install, change, tracking, group, ALL.\n" +
                        "**Exempel (för att få hjälp angående raidkommandon):** !raid man raid"
        );
        i18nMessages.put(new I18nLookup(MANUAL_CONFIG, Locale.ENGLISH),
                "**Note: This command must be executed in a server text channel, not in DM!**\n\n" +
                        "**To find out your configuration:**\n!raid config show\n\n" +
                        "**To change language:**\n!raid config *[param=value]*\n" +
                        "*Example setting Swedish locale:* !raid config locale=sv\n" +
                "*Example setting nickname:* !raid config nick=HelloWorld"
        );
        i18nMessages.put(new I18nLookup(MANUAL_CONFIG, SWEDISH),
                "**OBS: Detta kommando måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**För att få reda på din konfiguration:**\n!raid config show\n\n" +
                        "**För att ändra språk:**\n!raid config *[param=value]*\n" +
                        "*Exempel (set English as language):* !raid config locale=en\n" +
                "*Exempel att sätta smeknamn:* !raid config nick=HelloWorld"
        );
        i18nMessages.put(new I18nLookup(MANUAL_RAID, Locale.ENGLISH),
                "**Note: All of these commands must be executed in a server text channel, not in DM!**\n\n" +
                        "**To register a new raid:**\n!raid new *[Pokemon]* *[Ends at (HH:MM)]* *[Gym name]*\n" +
                        "*Example:* !raid new entei 09:25 Solna Platform\n\n" +
                        "**To register a new EX raid:**\n!raid ex *[Pokemon]* *[Ends at (yyyy-mm-dd HH:MM)]* *[Gym name]*\n" +
                        "*Example:* !raid ex deoxys 2017-10-10 09:25 Solna Platform\n\n" +
                        "**To register a new raid via start time:**\n!raid start *[Pokemon]* " +
                        "*[Starts at (HH:MM)]* *[Gym name]*\n" +
                        "*Example:* !raid start entei 08:40 Solna Platform\n\n" +
                        "**To report a egg being hatched at some point:**\n!raid start *[Egg1-5 depending on raid tier]* " +
                        "*[Starts at (HH:MM)]* *[Gym name]*\n" +
                        "*Example:* !raid start Egg5 08:40 Solna Platform\n\n" +
                        "**To report a reported egg having hatched:**\n!raid hatch *[Pokemon]* " +
                        "*[Gym name]*\n" +
                        "*Example:* !raid hatch entei Solna Platform\n\n" +
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
                        "**För att registrera en EX raid:**\n!raid ex *[Pokemon]* " +
                        "*[Slutar (yyyy-mm-dd HH:MM)]* *[Gym-namn]*\n" +
                        "*Exempel:* !raid ex deoxys 2017-10-10 09:25 Solna Platform\n\n" +
                        "**För att registrera en raid via starttid:**\n!raid start *[Pokemon]* " +
                        "*[Startar klockan (HH:MM)]* *[Gym-namn]*\n" +
                        "*Exempel:* !raid start entei 08:40 Solna Platform\n\n" +
                        "**För att rapportera att ett raidägg kommer kläckas:**\n!raid start " +
                        "*[Egg1-5 beroende på nivå]* " +
                        "*[Startar klockan (HH:MM)]* *[Gym-namn]*\n" +
                        "*Exempel:* !raid start Egg5 08:40 Solna Platform\n\n" +
                        "**För att rapportera att ett rapporterat ägg kläckts:**\n!raid hatch *[Pokemon]* " +
                        "*[Gym-namn]*\n" +
                        "*Exempel:* !raid hatch entei Solna Platform\n\n" +
                        "**Kolla status för en raid:**\n!raid status *[Gym-namn]*\n" +
                        "*Exempel:* !raid status Solna Platform\n\n" +
                        "**Visa alla registrerade raider:**\n!raid list\n" +
                        "*Exempel:* !raid list Entei - visa alla aktuella raider med Entei som boss.\n" +
                        "!raid list - lista alla aktuella raider oavsett boss.\n\n" +
                        "**Information om en raidboss:**\n!raid vs *[Pokemon]*\n" +
                        "*Exempel:* !raid vs Entei"
        );

        i18nMessages.put(new I18nLookup(MANUAL_CHANGE, Locale.ENGLISH),
                "**Note: All of these commands must be executed in a server text channel, not in DM!**\n\n" +
                        "**Change endtime for a raid:** !raid change when *[New end of raid (HH:MM)]* *[Pokestop name]* " +
                        "(Only raid creator or server admins may do this)\n" +
                        "*Example:* !raid change when 09:45 Solna Platform\n\n" +
                        "**Change start time for a raid group:** !raid change group *[New time (HH:MM)]* *[Pokestop name]*" +
                        " (Only raid creator or server admin)\n" +
                        "*Example:* !raid change group 09:35 Solna Platform*\n\n" +
                        "**Remove raid group:** !raid change group remove *[Time (HH:MM)]* *[Pokestop name]*" +
                        " (Only group creator - if no signups - or server admin)\n" +
                        "*Example:* !raid change group remove 09:35 Solna Platform*\n" +
                        "You can also remove the group message to remove the raid group.\n\n" +
                        "**Change raid boss:** !raid change pokemon *[Pokemon]* *[Pokestop name]* " +
                        "(Only raid creator or server admin)\n" +
                        "*Example:* !raid change pokemon Suicune Solna Platform\n\n" +
                        "**Delete a raid:** !raid change remove *[Pokestop name]* (Only server admin)\n" +
                        "*Example:* !raid change remove Solna Platform"
        );
        i18nMessages.put(new I18nLookup(MANUAL_CHANGE, SWEDISH),
                "**OBS: Alla dessa kommandon måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**Ändra en raids sluttid:** !raid change when *[Ny sluttid (HH:MM)]* *[Gym-namn]* " +
                        "(Endast raidskapare eller admin får göra detta)\n" +
                        "*Exempel:* !raid change when 09:45 Solna Platform\n\n" +
                        "**Ändra tid för en raidgrupp:** !raid change group *[Ny tid (HH:MM)]* *[Gym-namn]*" +
                        " (Endast raidskapare eller admin)\n" +
                        "*Exempel:* !raid change group 09:35 Solna Platform*\n\n" +
                        "**Ta bort raidgrupp:** !raid change group remove *[Time (HH:MM)]* *[Pokestop name]*" +
                        " (Bara gruppskapare - om det inte finns anmälningar - eller admin)\n" +
                        "*Exempel:* !raid change group remove 09:35 Solna Platform*\n" +
                        "*Man kan också ta bort gruppmeddelandet så rensas gruppen automatiskt.\n\n" +
                        "**Ändra en raids boss:** !raid change pokemon *[Pokemon]* *[Pokestop name]* " +
                        "(Endast raidskapare eller admin)\n" +
                        "*Exempel:* !raid change pokemon Suicune Solna Platform\n\n" +
                        "**Ta bort en raid:** !raid change remove *[Pokestop name]* (Endast admin)\n" +
                        "*Exempel:* !raid change remove Solna Platform"
        );

        i18nMessages.put(new I18nLookup(MANUAL_GROUPS, Locale.ENGLISH),
                "**Note:This command must be executed in a server text channel, not in DM!**\n\n" +
                        "**Create a group to run a raid at a certain time:** !raid group {time (HH:MM)} {gym name}\n" +
                        "*Example:* !raid group 09:45 Solna Platform\n\n" +
                        "A user can sign up themselves and their group friends via emotes below the raid group message.\n\n" +
                        "1-6 buttons signs up the corresponding amount of people. " +
                        "You can combine numbers to get the correct total. " +
                        "Press the same button(s) again to remove the signups.\n\n" +
                        "The message will be automatically updated with new signups. When the raid group start time" +
                        " is expired, the message will be removed, along with the associated signups.\n\n" +
                        "Since this function is pretty new, feedback on how to improve it is welcome."
        );
        i18nMessages.put(new I18nLookup(MANUAL_GROUPS, SWEDISH),
                "**OBS: Kommandot måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**Skapa en grupp för att köra raid en viss tid:** !raid group {time (HH:MM)} {gym namn}\n" +
                        "*Exempel:* !raid group 09:45 Solna Platform\n\n" +
                        "Man anmäler sig till en raid via emotes som dyker upp under svaret på kommandot ovan.\n\n" +
                        "1-6 anmäler det antal som står på knappen. " +
                        "Tryck samma knapp igen för att ta bort samma antal.\n\n" +
                        "Meddelandet kommer uppdateras var 15:e sekund med alla som anmäler sig. När tiden gått " +
                        "ut för gruppen, kommer meddelandet tas bort, tillsammans med alla relaterade anmälningar.\n\n" +
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
                        "**You can also use an easier way:** \\+{number of people} {ETA (HH:MM)} {Gym name}\n" +
                        "*Example:* +3 09:15 Solna Platform\n\n" +
                        "**Unsign raid:**\n!raid remove *[Gym name]*\n" +
                        "*Example:* !raid remove Solna Platform\n\n" +
                        "**You can also use an easier way:** \\-{number of people} {Gym name}\n" +
                        "*Example:* -2 Solna Platform"
        );
        i18nMessages.put(new I18nLookup(MANUAL_SIGNUP, SWEDISH),
                "**OBS: Alla dessa kommandon måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**Säg att du kommer till en viss raid:**\n!raid add *[antal som kommer] [ETA (HH:MM)] [Gym-namn]*\n" +
                        "*Exempel:* !raid add 3 09:15 Solna Platform\n" +
                        "**Man kan också använda:** \\+{antal} {ETA (HH:MM)} {Gym-namn}\n" +
                        "*Exempel:* +3 09:15 Solna Platform\n\n" +
                        "**Ta bort din signup för en raid:**\n!raid remove *[Gym-namn]*\n" +
                        "*Exempel:* !raid remove Solna Platform\n\n" +
                        "**Man kan också använda:** \\-{antal} {Gym-namn}\n" +
                        "*Exempel:* -2 Solna Platform"
        );

        i18nMessages.put(new I18nLookup(MANUAL_TRACKING, Locale.ENGLISH),
                "**Note: All of these commands must be executed in a server text channel, not in DM!**\n\n" +
                        "**Track new raids for raid boss:**\n" +
                        "!raid track *[Pokemon]*\n" +
                        "*Example:* !raid track Entei\n\n" +
                        "**Untrack raids for raid boss:**\n!raid untrack *[Pokemon]*\n" +
                        "*Example - remove your tracking of Entei:* !raid untrack Entei\n" +
                        "*Example - remove all your tracking:* !raid untrack"
        );
        i18nMessages.put(new I18nLookup(MANUAL_TRACKING, SWEDISH),
                "**OBS: Alla dessa kommandon måste köras i en servers textkanal, inte i DM!**\n\n" +
                        "**Övervakning av nya raids för pokemon:" +
                        "**\n!raid track *[Pokemon]*\n" +
                        "*Exempel:* !raid track Entei\n\n" +
                        "**Ta bort övervakning av nya raids för pokemon:**\n!raid untrack *[Pokemon]*\n" +
                        "*Exempel - ta bort din övervakning för Entei:* !raid untrack Entei\n" +
                        "*Exempel - ta bort alla dina övervakningar:* !raid untrack"
        );
    }

    public Locale getLocaleForUser(User user) {
        if (userConfigRepository == null || user == null || user.getId() == null) {
            return DEFAULT;
        }

        final UserConfig userConfig = userConfigRepository.findOne(user.getId());
        if (userConfig == null) {
            return DEFAULT;
        } else {
            final Locale userLocale = userConfig.getLocale();
            if (userLocale == null) {
                return DEFAULT;
            } else {
                return userLocale;
            }
        }
    }

    public Locale getLocaleForUser(String username) {
        return DEFAULT;
    }

    public void storeMessage(String messageKey, Locale locale, String message) {
        i18nMessages.put(new I18nLookup(messageKey.toUpperCase(), locale), message);
    }

    public String getMessageFor(String messageKey, Locale locale, String ... parameters) {
        String messageWithParameters = getMessageTextToInjectParametersIn(messageKey, locale);
        int i = 1;
        for (String param : parameters) {
            messageWithParameters = messageWithParameters.replaceAll("[%][" + i + "]", param);
            i++;
        }
        return messageWithParameters;
    }

    private String getMessageTextToInjectParametersIn(String messageKey, Locale locale) {
        Locale actualLocale = locale;
        if (locale == null) {
            actualLocale = DEFAULT;
        }
        String message = i18nMessages.get(new I18nLookup(messageKey.toUpperCase(), actualLocale));
        if (message == null || message.length() < 1) {
            message = i18nMessages.get(new I18nLookup(messageKey.toUpperCase(), DEFAULT));
        }
        if (message == null || message.length() < 1) {
            throw new RuntimeException("Could not find text for message key " + messageKey +
                    " - an admin needs to add it to the LocaleService!");
        }
        return message;
    }

    public static boolean isSupportedLocale(Locale locale) {
        return Arrays.asList(SUPPORTED_LOCALES).contains(locale);
    }

    public static String asString(TimeUnit timeUnit, Locale locale) {
        switch (timeUnit) {
            case SECONDS:
                return (locale != null && locale.getLanguage().equals(SWEDISH.getLanguage()) ? "sekund" : "second");
            case MINUTES:
                return (locale != null && locale.getLanguage().equals(SWEDISH.getLanguage()) ? "minut" : "minute");
            default:
                return timeUnit.name().toLowerCase();
        }
    }

    private class I18nLookup {
        private String messageKey;
        private Locale locale;

        I18nLookup(String messageKey, Locale locale) {
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
                    "Getting started guide: " +
                    "https://github.com/magnusmickelsson/pokeraidbot/blob/master/GETTING_STARTED_USER_en.md\n\n" +
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
                    "Kom igång guide: " +
                    "https://github.com/magnusmickelsson/pokeraidbot/blob/master/GETTING_STARTED_USER_sv.md\n\n" +
                    "**Få detaljerad hjälp om hur botten fungerar:**\n!raid man {frivilligt: ämne}\n" +
                    "*Exempel:* !raid man - berättar om hur man använder raid man och vilka hjälpämnen som finns\n\n" +
                    "**För ett exempel på vad botten kan göra, skriv:** !raid map {namn på ett gym i området}\n" +
                    "*Exempel:* !raid map Solna Platform\n\n" +
                    "https://github.com/magnusmickelsson/pokeraidbot för att rapportera fel, önska funktioner, " +
                    "se screenshots av användning etc.\n\n" +
            "**Hur kan jag stödja utveckling av botten?**\n!raid donate\n\n" +
            "**If you want this information in english:**\n!raid usage en";
}
