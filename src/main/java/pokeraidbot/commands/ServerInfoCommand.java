package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.config.Config;
import pokeraidbot.domain.config.ConfigRepository;
import pokeraidbot.domain.config.LocaleService;

public class ServerInfoCommand extends ConfigAwareCommand {
    private final LocaleService localeService;

    public ServerInfoCommand(ConfigRepository configRepository, LocaleService localeService, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.name = "server";
        this.help = localeService.getMessageFor(LocaleService.SERVER_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        replyBasedOnConfig(config, commandEvent, String.valueOf(config));
    }
}
