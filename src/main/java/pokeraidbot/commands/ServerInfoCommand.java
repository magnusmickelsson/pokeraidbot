package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import main.BotServerMain;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class ServerInfoCommand extends ConfigAwareCommand {
    private final ClockService clockService;

    public ServerInfoCommand(ServerConfigRepository serverConfigRepository, LocaleService localeService,
                             CommandListener commandListener, ClockService clockService) {
        super(serverConfigRepository, commandListener, localeService);
        this.clockService = clockService;
        this.name = "server";
        this.help = localeService.getMessageFor(LocaleService.SERVER_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        replyBasedOnConfigAndRemoveAfter(config, commandEvent,
                Utils.printDateTime(clockService.getCurrentDateTime()) +
                ": " + String.valueOf(config), BotServerMain.timeToRemoveFeedbackInSeconds * 2);
    }
}
