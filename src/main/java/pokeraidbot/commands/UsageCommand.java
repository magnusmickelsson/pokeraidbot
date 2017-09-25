package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.Config;
import pokeraidbot.domain.ConfigRepository;
import pokeraidbot.domain.LocaleService;

import java.util.Locale;

public class UsageCommand extends ConfigAwareCommand {
    private final LocaleService localeService;

    public UsageCommand(LocaleService localeService, ConfigRepository configRepository) {
        super(configRepository);
        this.localeService = localeService;
        this.name = "usage";
        this.help = localeService.getMessageFor(LocaleService.USAGE_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String args = commandEvent.getArgs();
        Locale locale;
        if (args != null && args.length() > 0) {
            locale = new Locale(args);
        } else {
            locale = localeService.getLocaleForUser(commandEvent.getAuthor().getName());
        }
        replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.USAGE, locale));
    }
}
