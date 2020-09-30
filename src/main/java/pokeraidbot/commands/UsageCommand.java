package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Locale;

/**
 * !raid usage
 */
public class UsageCommand extends ConfigAwareCommand {
    private final LocaleService localeService;

    public UsageCommand(LocaleService localeService, ServerConfigRepository serverConfigRepository,
                        CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
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
            locale = localeService.getLocaleForUser(commandEvent.getAuthor());
        }
        replyBasedOnConfigButKeep(config, commandEvent, localeService.getMessageFor(LocaleService.USAGE, locale));
    }
}
