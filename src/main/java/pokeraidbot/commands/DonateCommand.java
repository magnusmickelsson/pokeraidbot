package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class DonateCommand extends ConfigAwareCommand {
    private static final String link = "https://www.paypal.com/pools/c/821lf4bmi6";

    public DonateCommand(LocaleService localeService, ServerConfigRepository serverConfigRepository,
                         CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.name = "donate";
        this.help = localeService.getMessageFor(LocaleService.DONATE, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        replyBasedOnConfig(config, commandEvent, link);
    }
}
