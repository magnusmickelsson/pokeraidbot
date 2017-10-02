package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class HelpTopicCommand extends ConfigAwareCommand {
    private final LocaleService localeService;

    public HelpTopicCommand(LocaleService localeService, ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.name = "help-signup";
        this.help = localeService.getMessageFor(LocaleService.USAGE_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        commandEvent.reply("** Denna feature är inte klar och fungerar inte som den ska.** Ge gärna feedback på utseende. Work in progress. ;p");
//        final String args = commandEvent.getArgs();
//        Locale locale;
//        if (args != null && args.length() > 0) {
//            locale = new Locale(args);
//        } else {
//            locale = localeService.getLocaleForUser(commandEvent.getAuthor().getName());
//        }
//        replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.USAGE, locale));
    }
}
