package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

public class DonateCommand extends ConfigAwareCommand {
    private static final String link = "https://pledgie.com/campaigns/34823";

    public DonateCommand(LocaleService localeService, ServerConfigRepository serverConfigRepository,
                         CommandListener commandListener, UserConfigRepository userConfigRepository) {
        super(serverConfigRepository, commandListener, localeService, userConfigRepository);
        this.name = "donate";
        this.help = localeService.getMessageFor(LocaleService.DONATE, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config, User user) {
        replyBasedOnConfig(config, commandEvent, link);
    }
}
