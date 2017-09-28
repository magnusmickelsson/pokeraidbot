package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.Config;
import pokeraidbot.domain.config.ConfigRepository;
import pokeraidbot.domain.errors.UserMessedUpException;

public abstract class ConfigAwareCommand extends Command {
    private final ConfigRepository configRepository;
    private final CommandListener commandListener;

    public ConfigAwareCommand(ConfigRepository configRepository, CommandListener commandListener) {
        Validate.notNull(configRepository);
        this.commandListener = commandListener;
        this.configRepository = configRepository;
    }

    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, String message) {
        if (config != null && config.replyInDmWhenPossible) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(message);
        }
    }

    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, MessageEmbed message) {
        if (config != null && config.replyInDmWhenPossible) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(message);
        }
    }

    public static void replyErrorBasedOnConfig(Config config, CommandEvent commandEvent, Throwable t) {
        if (config != null && config.replyInDmWhenPossible) {
            commandEvent.replyInDM(t.getMessage());
            commandEvent.reactError();
        } else {
            commandEvent.reply(t.getMessage());
        }
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        Config configForServer = null;
        try {
            final Guild guild = commandEvent.getGuild();
            final String server = guild.getName().trim().toLowerCase();
            configForServer = configRepository.getConfigForServer(server);
            executeWithConfig(commandEvent, configForServer);
            if (commandListener != null) {
                commandListener.onCompletedCommand(commandEvent, this);
            }
        } catch (Throwable t) {
            if (t instanceof IllegalArgumentException) {
                replyErrorBasedOnConfig(configForServer, commandEvent,
                        new UserMessedUpException(commandEvent.getAuthor().getName(), t.getMessage()));
            } else {
                replyErrorBasedOnConfig(configForServer, commandEvent, t);
            }
            if (commandListener != null) {
                commandListener.onTerminatedCommand(commandEvent, this);
            }
        }
    };

    protected abstract void executeWithConfig(CommandEvent commandEvent, Config config);
}
