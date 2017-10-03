package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
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
        helpText = "Hjälpmanual för olika ämnen: !raid man {ämne}\nÄmne kan vara t.ex. raid, signup eller install.\n" +
                "**Kommandot måste köras i en servers textkanal, inte i DM.**";
        this.help = helpText;
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
        final String[] args = commandEvent.getArgs().split(" ");
        if (args.length != 1) {
            commandEvent.replyInDM(helpText);
        } else {
            final Map<String, String> helpTopicTexts = helpTopicsMap.get(args[0]);
            if (helpTopicTexts == null) {
                commandEvent.replyInDM(helpText);
            } else {
                final String text = helpTopicTexts.get(config.getLocale().getLanguage());
                if (text == null) {
                    commandEvent.replyInDM(helpText);
                } else {
                    commandEvent.replyInDM(text);
                }
            }
        }
        commandEvent.reactSuccess();
    }
}
