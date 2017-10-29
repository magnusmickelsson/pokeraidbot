package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfig;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

// todo: Not done yet
public class UserConfigCommand extends ConfigAwareCommand {
    private final UserConfigRepository userConfigRepository;

    public UserConfigCommand(ServerConfigRepository serverConfigRepository, CommandListener commandListener,
                             LocaleService localeService, UserConfigRepository userConfigRepository) {
        super(serverConfigRepository, commandListener, localeService);
        this.userConfigRepository = userConfigRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final UserConfig userConfig = userConfigRepository.findOne(commandEvent.getAuthor().getId());
        // todo: allow user to set their locale and other config
    }
}
