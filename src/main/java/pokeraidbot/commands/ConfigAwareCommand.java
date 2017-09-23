package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.Config;
import pokeraidbot.domain.ConfigRepository;

public abstract class ConfigAwareCommand extends Command {
    private final ConfigRepository configRepository;

    public ConfigAwareCommand(ConfigRepository configRepository) {
        Validate.notNull(configRepository);
        this.configRepository = configRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final String server = commandEvent.getGuild().getName().trim().toLowerCase();
        try {
            executeWithConfig(commandEvent, configRepository.getConfigForServer(server));
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    };

    protected abstract void executeWithConfig(CommandEvent commandEvent, Config config);
}
