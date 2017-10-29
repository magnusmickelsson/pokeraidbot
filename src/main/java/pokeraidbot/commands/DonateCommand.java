package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class DonateCommand extends ConfigAwareCommand {
    private static final String link = "https://pledgie.com/campaigns/34823";

    public DonateCommand(LocaleService localeService, ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.name = "donate";
        this.help = localeService.getMessageFor(LocaleService.DONATE, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        replyBasedOnConfig(config, commandEvent, link);
    }
}
