package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Get help: !raid man [topic]
 */
public class HelpManualCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final Map<String, Map<String, String>> helpTopicsMap = new LinkedHashMap<>();
    private String helpText;

    public HelpManualCommand(LocaleService localeService, ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.name = "man";
        // todo: i18n
        helpText = " Hjälpmanual för olika ämnen: !raid man {ämne} {frivilligt:chan/dm - " +
                "om man t.ex. vill visa hjälpen i en textkanal för en användare}\n" +
                "Möjliga ämnen: raid, signup, map, install, change, tracking, group, ALL.\n" +
        "**Exempel (för att få hjälp angående raidkommandon):** !raid man raid";
        this.help = helpText;
        this.guildOnly = false;
        this.aliases = new String[]{"hello"};
        if (helpTopicsMap.size() == 0) {
            initialize();
        }
    }

    private void initialize() {
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_MAP, "map");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_RAID, "raid");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_CHANGE, "change");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_SIGNUP, "signup");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_GROUPS, "group");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_TRACKING, "tracking");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_INSTALL, "install");
    }

    private void addTextsToHelpTopics(Locale[] supportedLocales, String messageKey, String topic) {
        final HashMap<String, String> topicTexts = new HashMap<>();
        for (Locale locale : supportedLocales) {
            topicTexts.put(locale.getLanguage(), localeService.getMessageFor(messageKey, locale));
        }
        helpTopicsMap.put(topic, topicTexts);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String language;

        if (config == null) {
            language = LocaleService.DEFAULT.getLanguage();
        } else {
            language = config.getLocale().getLanguage();
        }
        final String[] args = commandEvent.getArgs().split(" ");

        // If bad arguments
        if (args.length < 1 || args.length > 2) {
            commandEvent.replyInDM(helpText);
        } else { // If user wants full manual (=ALL)
            final String replyIn = args.length > 1 ? args[1] : null;
            if ("ALL".equalsIgnoreCase(args[0])) {
                for (String key : helpTopicsMap.keySet()) {
                    replyWithHelpManualEntryIfAvailable(commandEvent, language, replyIn, helpTopicsMap.get(key));
                }
            } else { // Reply with user's requested help topic
                final Map<String, String> helpTopicTexts = helpTopicsMap.get(args[0]);
                replyWithHelpManualEntryIfAvailable(commandEvent, language, replyIn, helpTopicTexts);
            }
        }
    }

    private void replyWithHelpManualEntryIfAvailable(CommandEvent commandEvent, String language, String replyIn,
                                                     Map<String, String> helpTopicTexts) {
        if (helpTopicTexts == null) {
            commandEvent.replyInDM(helpText);
        } else {
            final String text = helpTopicTexts.get(language);
            if (text == null) {
                commandEvent.replyInDM(helpText);
            } else {
                if (!StringUtils.isEmpty(replyIn) && replyIn.equalsIgnoreCase("dm")) {
                    commandEvent.replyInDM(text);
                } else {
                    commandEvent.reply(text);
                }
            }
        }
    }
}
