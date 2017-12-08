package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import main.BotServerMain;
import pokeraidbot.Utils;
import pokeraidbot.domain.User;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

public class ServerInfoCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final ClockService clockService;

    public ServerInfoCommand(ServerConfigRepository serverConfigRepository, LocaleService localeService,
                             CommandListener commandListener, ClockService clockService,
                             UserConfigRepository userConfigRepository) {
        super(serverConfigRepository, commandListener, localeService, userConfigRepository);
        this.localeService = localeService;
        this.clockService = clockService;
        this.name = "server";
        this.help = localeService.getMessageFor(LocaleService.SERVER_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config, User user) {
        replyBasedOnConfigAndRemoveAfter(config, commandEvent, Utils.printDateTime(clockService.getCurrentDateTime()) +
                ": " + String.valueOf(config), BotServerMain.timeToRemoveFeedbackInSeconds * 2);
    }
}
