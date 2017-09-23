package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.Config;
import pokeraidbot.domain.ConfigRepository;
import pokeraidbot.domain.LocaleService;

public class ServerInfoCommand extends ConfigAwareCommand {
    private final LocaleService localeService;

    public ServerInfoCommand(ConfigRepository configRepository, LocaleService localeService) {
        super(configRepository);
        this.localeService = localeService;
        this.name = "server";
        this.help = localeService.getMessageFor(LocaleService.SERVER_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        replyBasedOnConfig(config, commandEvent, String.valueOf(config));
    }
}
