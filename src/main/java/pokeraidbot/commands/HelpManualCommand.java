package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Get help: !raid man [topic]
 */
public class HelpManualCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final Map<String, Map<String, String>> helpTopicsMap = new HashMap<>();
    private String helpText;

    public HelpManualCommand(LocaleService localeService, ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.name = "man";
        // todo: i18n
        helpText = " Hjälpmanual för olika ämnen: !raid man {ämne} {frivilligt:chan/dm - " +
                "om man t.ex. vill visa hjälpen i en textkanal för en användare}\n" +
                "Möjliga ämnen: raid, signup, map, install, change, tracking, group (todo).";
        this.help = helpText;
        this.guildOnly = false;
        if (helpTopicsMap.size() == 0) {
            initialize();
        }
    }

    private void initialize() {
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_RAID, "raid");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_SIGNUP, "signup");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_MAP, "map");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_INSTALL, "install");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_CHANGE, "change");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_TRACKING, "tracking");
        addTextsToHelpTopics(LocaleService.SUPPORTED_LOCALES, LocaleService.MANUAL_GROUPS, "groups");
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
        if (args.length < 1 || args.length > 2) {
            commandEvent.replyInDM(helpText);
        } else {
            final Map<String, String> helpTopicTexts = helpTopicsMap.get(args[0]);
            String replyIn = args.length > 1 ? args[1] : null;
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
}
