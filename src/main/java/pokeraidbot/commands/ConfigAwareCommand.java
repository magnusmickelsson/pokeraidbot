package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.concurrent.TimeUnit;

public abstract class ConfigAwareCommand extends Command {
    protected final ConfigRepository configRepository;
    protected final CommandListener commandListener;
    private final LocaleService localeService;

    public ConfigAwareCommand(ConfigRepository configRepository, CommandListener commandListener,
                              LocaleService localeService) {
        Validate.notNull(configRepository);
        this.localeService = localeService;
        this.commandListener = commandListener;
        this.configRepository = configRepository;
    }

    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, String message) {
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(message);
            commandEvent.reply(embedBuilder.build());
        }
    }

    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, MessageEmbed message) {
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(message);
        }
    }

    public void replyErrorBasedOnConfig(Config config, final CommandEvent commandEvent, Throwable t) {
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDM(t.getMessage());
            commandEvent.reactError();
        } else {
            commandEvent.reactError();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(t.getMessage());
            final String msgRemoveText = localeService.getMessageFor(LocaleService.ERROR_KEEP_CHAT_CLEAN,
                    localeService.getLocaleForUser(commandEvent.getAuthor()), "15");
            embedBuilder.setFooter(msgRemoveText, null);
            commandEvent.reply(embedBuilder.build(), msg -> {
                commandEvent.getMessage().delete().queueAfter(15, TimeUnit.SECONDS); // Clean up bad message
                msg.delete().queueAfter(15, TimeUnit.SECONDS); // Clean up feedback after x seconds
            });
        }
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        Config configForServer = null;
        try {
            final Guild guild = commandEvent.getGuild();
            if (guild != null) {
                final String server = guild.getName().trim().toLowerCase();
                configForServer = configRepository.getConfigForServer(server);
                if (configForServer == null) {
                    final String noConfigText = localeService.getMessageFor(LocaleService.NO_CONFIG,
                            localeService.getLocaleForUser(commandEvent.getAuthor()));
                    commandEvent.reply(noConfigText);
                    if (commandListener != null) {
                        commandListener.onCompletedCommand(commandEvent, this);
                    }
                    return;
                }
            }
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

    public void replyBasedOnConfigAndRemoveAfter(Config config, CommandEvent commandEvent,
                                                        String message, int numberOfSeconds) {
        // Give the caller some slack but not much
        Validate.isTrue(numberOfSeconds > 5 && numberOfSeconds < 60);
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reactSuccess();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(message);
            final String msgRemoveText = localeService.getMessageFor(LocaleService.KEEP_CHAT_CLEAN,
                    localeService.getLocaleForUser(commandEvent.getAuthor()), "" + numberOfSeconds);

            embedBuilder.setFooter(msgRemoveText, null);
            commandEvent.reply(embedBuilder.build(), msg -> {
                msg.delete().queueAfter(numberOfSeconds, TimeUnit.SECONDS); // Clean up feedback after x seconds
            });
        }
    }
}
