package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class ServerInfoCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final ClockService clockService;

    public ServerInfoCommand(ConfigRepository configRepository, LocaleService localeService,
                             CommandListener commandListener, ClockService clockService) {
        super(configRepository, commandListener, localeService);
        this.localeService = localeService;
        this.clockService = clockService;
        this.name = "server";
        this.help = localeService.getMessageFor(LocaleService.SERVER_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        replyBasedOnConfig(config, commandEvent, Utils.printDateTime(clockService.getCurrentDateTime()) +
                ": " + String.valueOf(config));
    }
}
