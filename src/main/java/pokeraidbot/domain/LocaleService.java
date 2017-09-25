package pokeraidbot.domain;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static pokeraidbot.Utils.printTime;

public class LocaleService {
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
    private Map<I18nLookup, String> i18nMessages = new HashMap<>();

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
    public static final String ENDS_AT = "ENDS_AT";
    public static final String CURRENT_RAIDS = "CURRENT_RAIDS";
    public static final String SIGNED_UP = "SIGNED_UP";
    public static final String RAIDSTATUS_HELP = "RAIDSTATUS_HELP";
    public static final String GENERIC_USER_ERROR = "GENERIC_USER_ERROR";

    public static final Locale SWEDISH = new Locale("sv");

    // Change this if you want another default locale, affects the usage texts etc
    public static final Locale DEFAULT = SWEDISH;

    public LocaleService() {
        i18nMessages.put(new I18nLookup(TRACKING_ADDED, Locale.ENGLISH),
                "Added tracking for pokemon %1 for user %2.");
        i18nMessages.put(new I18nLookup(TRACKING_ADDED, SWEDISH),
                "Lade till övervakning av pokemon %1 för %2.");

        i18nMessages.put(new I18nLookup(TRACKED_RAID, Locale.ENGLISH),
                "Raid was created for %1 by %2 - %3");
        i18nMessages.put(new I18nLookup(TRACKED_RAID, SWEDISH),
                "Raid skapades för raidboss %1 av %2 - %3");

        i18nMessages.put(new I18nLookup(TRACKING_EXISTS, Locale.ENGLISH),
                "You're already tracking this: %1");
        i18nMessages.put(new I18nLookup(TRACKING_EXISTS, SWEDISH),
                "Du har redan övervakning satt för detta: %1");

        i18nMessages.put(new I18nLookup(DONATE, Locale.ENGLISH),
                "How to support development of this bot via donating.");
        i18nMessages.put(new I18nLookup(DONATE, SWEDISH),
                "Hur kan man stödja utvecklingen av botten?");

        i18nMessages.put(new I18nLookup(GYM_CONFIG_ERROR, Locale.ENGLISH),
                "There are no gyms for this region. " +
                        "Please check configuration and/or notify administrator!");
        i18nMessages.put(new I18nLookup(GYM_CONFIG_ERROR, SWEDISH),
                "Det finns inga gym för din valda region. Kontrollera konfigurationen av servern och/eller meddela" +
                        " en administratör så de kan hjälpa dig.");
        i18nMessages.put(new I18nLookup(GYM_SEARCH_MANY_RESULTS, Locale.ENGLISH),
                "Could not find one unique gym/pokestop, your query returned 5+ results. Try refine your search.");
        i18nMessages.put(new I18nLookup(GYM_SEARCH_MANY_RESULTS, SWEDISH),
                "Kunde inte hitta ett unikt gym/pokestop, din sökning returnerade mer än 5 resultat. " +
                        "Försök vara mer precis.");

        i18nMessages.put(new I18nLookup(GYM_SEARCH_OPTIONS, Locale.ENGLISH),
                "Could not find one unique gym/pokestop. Did you want any of these? %1");
        i18nMessages.put(new I18nLookup(GYM_SEARCH_OPTIONS, SWEDISH),
                "Kunde inte hitta ett unikt gym/pokestop. Var det någon av dessa du sökte efter? %1");

        i18nMessages.put(new I18nLookup(GYM_SEARCH, Locale.ENGLISH),
                "Empty input for gym name, try giving me a proper name to search for. :(");
        i18nMessages.put(new I18nLookup(GYM_SEARCH, SWEDISH),
                "Tom söksträng för gymnamn, ge mig något skoj att söka efter!");

        i18nMessages.put(new I18nLookup(NO_ETA_AFTER_RAID, Locale.ENGLISH),
                "Can't arrive after raid has ended. Your given time is %1, raid ends at %2");
        i18nMessages.put(new I18nLookup(NO_ETA_AFTER_RAID, SWEDISH),
                "Det är väl inte så lämpligt att anlända efter att raiden slutat? Din ETA är %1, raiden slutar %2.");

        i18nMessages.put(new I18nLookup(NO_RAID_TOO_LONG, Locale.ENGLISH),
                "You can't create raids which are later than 2 hours from the current time %2 - your time was %1.");
        i18nMessages.put(new I18nLookup(NO_RAID_TOO_LONG, SWEDISH),
                "Du kan inte skapa en raid senare än 2 timmar från vad klockan är nu (%2). Tiden du gav var %1.");

        i18nMessages.put(new I18nLookup(NO_RAIDS_NOW, Locale.ENGLISH),
                "You can't create raids between 22:00 and 07:00 - your time was %1.");
        i18nMessages.put(new I18nLookup(NO_RAIDS_NOW, SWEDISH),
                "Du kan inte skapa en raid som slutar mellan 22.00 och 07:00. Tiden du gav var %1.");

        i18nMessages.put(new I18nLookup(TIMEZONE, Locale.ENGLISH),
                "You seem to be living in a different timezone. Your given time is %1, while the current time is %2.");
        i18nMessages.put(new I18nLookup(TIMEZONE, SWEDISH),
                "Du kan inte skapa en raid som slutar innan nuvarande tid. Tiden du gav var %1, men klockan är %2.");

        i18nMessages.put(new I18nLookup(NO_POKEMON, Locale.ENGLISH), "Could not find a pokemon with name \"%1\".");
        i18nMessages.put(new I18nLookup(NO_POKEMON, SWEDISH), "Kunde inte hitta pokemon med namn \"%1\".");

        i18nMessages.put(new I18nLookup(ALREADY_SIGNED_UP, Locale.ENGLISH), "You're already signed up for: %1 ... " +
                "%2 - remove your current signup then signup again.");
        i18nMessages.put(new I18nLookup(ALREADY_SIGNED_UP, SWEDISH), "Du har redan anmält dig till raid vid %1 ... " +
                "%2 - ta bort din anmälan och gör om som du vill ha det.");

        i18nMessages.put(new I18nLookup(WHERE_GYM_HELP, Locale.ENGLISH), "Get map link for gym - !raid map [Gym name]");
        i18nMessages.put(new I18nLookup(WHERE_GYM_HELP, SWEDISH), "Visa karta för gym - !raid map [Gym]");

        i18nMessages.put(new I18nLookup(SIGNUPS, Locale.ENGLISH), "%1 sign up added to raid at %2. %3");
        i18nMessages.put(new I18nLookup(SIGNUPS, SWEDISH), "Anmälan från %1 registrerad för raid vid %2. %3");

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

        i18nMessages.put(new I18nLookup(RAIDSTATUS, Locale.ENGLISH), "Status for raid at %1:");
        i18nMessages.put(new I18nLookup(RAIDSTATUS, SWEDISH), "Status för raid vid %1:");

        i18nMessages.put(new I18nLookup(RAIDSTATUS_HELP, Locale.ENGLISH),
                "Check status for raid - !raid status [Gym name].");
        i18nMessages.put(new I18nLookup(RAIDSTATUS_HELP, SWEDISH),
                "Se status för raid - !raid status [Gym].");

        i18nMessages.put(new I18nLookup(SIGNED_UP, Locale.ENGLISH), "signed up");
        i18nMessages.put(new I18nLookup(SIGNED_UP, SWEDISH), "anmäld(a)");

        i18nMessages.put(new I18nLookup(CURRENT_RAIDS, Locale.ENGLISH), "Current raids");
        i18nMessages.put(new I18nLookup(CURRENT_RAIDS, SWEDISH), "Pågående raids");

        i18nMessages.put(new I18nLookup(ENDS_AT, Locale.ENGLISH), "ends at %1");
        i18nMessages.put(new I18nLookup(ENDS_AT, SWEDISH), "slutar klockan %1");

        i18nMessages.put(new I18nLookup(LIST_NO_RAIDS, Locale.ENGLISH), "There are currently no active raids.");
        i18nMessages.put(new I18nLookup(LIST_NO_RAIDS, SWEDISH), "Det finns just nu inga aktiva raids.");

        i18nMessages.put(new I18nLookup(LIST_HELP, Locale.ENGLISH), "Check current raids - !raid list [optional: Pokemon]");
        i18nMessages.put(new I18nLookup(LIST_HELP, SWEDISH), "Visa aktiva raids - !raid list [Pokemon (filtrering, frivillig)]");

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
        i18nMessages.put(new I18nLookup(RAID_TOSTRING, SWEDISH), "%1 raid vid %2, slut kl. %3");

        i18nMessages.put(new I18nLookup(NEW_RAID_CREATED, Locale.ENGLISH), "Raid created: %1");
        i18nMessages.put(new I18nLookup(NEW_RAID_CREATED, SWEDISH), "Raid skapad: %1");

        i18nMessages.put(new I18nLookup(NEW_RAID_HELP, Locale.ENGLISH),
                "Create new raid - !raid new [Name of Pokemon] [Ends at (HH:MM)] [Gym name]");
        i18nMessages.put(new I18nLookup(NEW_RAID_HELP, SWEDISH),
                "Skapa ny raid - !raid new [Pokemon] [Slutar klockan (HH:MM)] [Gym]");

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
                "Tyvärr, %1, en raid vid gym %2 finns redan (för %3). Anmäl dig till den!");

    }

    // todo: implement saving locale setting for user
    public Locale getLocaleForUser(String username) {
        return SWEDISH;
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

    public static String featuresString_EN = "**To register a new raid:**\n!raid new *[Pokemon]* *[Ends at (HH:MM)]* *[Gym name]*\n" +
            "*Example:* !raid new entei 09:25 Solna Platform\n\n" +
            "**Check status for a raid in a gym:**\n!raid status *[Gym name]*\n" +
            "*Example:* !raid status Solna Platform\n\n" +
            "**Get a list of all active raids:**\n!raid list\n" +
            "*Examples:* !raid list Entei - list all raids for Entei. !raid list - list all active raids.\n\n" +
            "**Get map link for a certain gym:**\n!raid map *[Gym name]*\n" +
            "*Example:* !raid map Solna Platform\n\n" +
            "**Sign up for a certain raid:**\n!raid add *[number of people] [ETA (HH:MM)] [Gym name]*\n" +
            "*Example:* !raid add 3 09:15 Solna Platform\n\n" +
            "**Unsign for a certain raid:**\n!raid remove *[Gym name]*\n" +
            "*Example:* !raid remove Solna Platform\n\n" +
            "**Info about the raid boss:**\n!raid vs *[Pokemon]*\n" +
            "*Example:* !raid vs Entei\n\n" +
            "**Track new raids for raid boss:**\n!raid track *[Pokemon]*\n" +
            "*Example:* !raid track Entei\n\n" +
            "**How do I support development of this bot?**\n!raid donate";
    public static String featuresString_SV = "**För att registrera en raid:**\n!raid new *[Pokemon]* *[Slutar klockan (HH:MM)]* *[Gym-namn]*\n" +
            "*Exempel:* !raid new entei 09:25 Solna Platform\n\n" +
            "**Kolla status för en raid:**\n!raid status *[Gym-namn]*\n" +
            "*Exempel:* !raid status Solna Platform\n\n" +
            "**Visa alla registrerade aktiva raider:**\n!raid list\n" +
            "*Exempel:* !raid list Entei - visa alla aktiva raider med Entei som boss. !raid list - lista alla aktiva raider oavsett boss.\n\n" +
            "**Hämta karta för gym:**\n!raid map *[Gym-namn]*\n" +
            "*Exempel:* !raid map Solna Platform\n\n" +
            "**Säg att du kommer på en viss raid:**\n!raid add *[antal som kommer] [ETA (HH:MM)] [Gym-namn]*\n" +
            "*Exempel:* !raid add 3 09:15 Solna Platform\n\n" +
            "**Ta bort din signup för en raid:**\n!raid remove *[Gym-namn]*\n" +
            "*Exempel:* !raid remove Solna Platform\n\n" +
            "**Information om en raidboss:**\n!raid vs *[Pokemon]*\n" +
            "*Exempel:* !raid vs Entei\n\n" +
            "**Övervakning av nya raids för pokemon:**\n!raid track *[Pokemon]*\n" +
            "*Exempel:* !raid track Entei\n\n" +
            "**Hur kan jag stödja utveckling av botten?**\n!raid donate\n\n" +
            "**If you want this information in english:**\n!raid usage en";
}
