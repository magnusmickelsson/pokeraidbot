package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.feedback.CleanUpMostFeedbackStrategy;
import pokeraidbot.domain.feedback.DefaultFeedbackStrategy;
import pokeraidbot.domain.feedback.FeedbackStrategy;
import pokeraidbot.domain.feedback.KeepAllFeedbackStrategy;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public abstract class ConfigAwareCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigAwareCommand.class);

    private static final DefaultFeedbackStrategy defaultFeedbackStrategy = new DefaultFeedbackStrategy();
    protected final ServerConfigRepository serverConfigRepository;
    protected final CommandListener commandListener;
    protected final LocaleService localeService;

    public ConfigAwareCommand(ServerConfigRepository serverConfigRepository,
                              CommandListener commandListener,
                              LocaleService localeService) {
        Validate.notNull(serverConfigRepository);
        this.localeService = localeService;
        this.commandListener = commandListener;
        this.serverConfigRepository = serverConfigRepository;
    }

    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, String message) {
        getFeedbackStrategy(config).reply(config, commandEvent, message);
    }

    public static void replyBasedOnConfigButKeep(Config config, CommandEvent commandEvent, String message) {
        getFeedbackStrategy(config).replyAndKeep(config, commandEvent, message);
    }

    private static FeedbackStrategy getFeedbackStrategy(Config config) {
        if (config != null && config.getFeedbackStrategy() != null) {
            switch (config.getFeedbackStrategy()) {
                case REMOVE_ALL_EXCEPT_MAP:
                    return new CleanUpMostFeedbackStrategy();
                case KEEP_ALL:
                    return new KeepAllFeedbackStrategy();
                case DEFAULT:
                default:
                    return defaultFeedbackStrategy;
            }
        } else {
            return defaultFeedbackStrategy;
        }
    }

    public static void removeOriginMessageIfConfigSaysSo(Config config, CommandEvent commandEvent) {
        getFeedbackStrategy(config).handleOriginMessage(commandEvent);
    }

    public static void removeOriginMessageIfConfigSaysSo(Config config, GuildMessageReceivedEvent event) {
        getFeedbackStrategy(config).handleOriginMessage(event);
    }


    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, MessageEmbed message) {
        getFeedbackStrategy(config).reply(config, commandEvent, message);
    }

    public static void replyMapBasedOnConfig(Config config, CommandEvent commandEvent, MessageEmbed message) {
        getFeedbackStrategy(config).replyMap(config, commandEvent, message);
    }

    public static void replyMapInChat(Config config, CommandEvent commandEvent, MessageEmbed message) {
        getFeedbackStrategy(config).replyMapInChat(config, commandEvent, message);
    }

    public static void verifyPermission(LocaleService localeService, CommandEvent commandEvent, User user,
                                        Raid raid, Config config) {
        final boolean isServerMod = isUserServerMod(commandEvent, config);
        final boolean userIsNotAdministrator = !isUserAdministrator(commandEvent) && !isServerMod;
        final boolean userIsNotRaidCreator = !user.getName().equalsIgnoreCase(raid.getCreator());
        if (userIsNotAdministrator && userIsNotRaidCreator) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_PERMISSION,
                    localeService.getLocaleForUser(user)));
        }
    }

    public void replyErrorBasedOnConfig(Config config, final CommandEvent commandEvent, Throwable t) {
        getFeedbackStrategy(config).replyError(config, commandEvent, t, localeService);
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        Config configForServer = null;
        try {
            final Guild guild = commandEvent.getGuild();
            if (guild != null) {
                final String server = guild.getName().trim().toLowerCase();
                configForServer = serverConfigRepository.getConfigForServer(server);
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exception thrown from command " + this.getClass().getSimpleName()
                        + " with input message \"" + commandEvent.getMessage().getContentRaw() + "\"" +
                        (configForServer != null ? " for server " +
                                configForServer.getServer() : "") + ":\n" + t.getMessage());
                if (t.getMessage() == null) {
                    LOGGER.debug("Dumping stacktrace, since exception was null.", t);
                }
            }
            try {
                if (t instanceof IllegalArgumentException) {
                    getFeedbackStrategy(configForServer).replyError(configForServer, commandEvent,
                            new UserMessedUpException(commandEvent.getAuthor().getName(), t.getMessage()), localeService);
                } else {
                    getFeedbackStrategy(configForServer).replyError(configForServer, commandEvent, t, localeService);
                }
                if (commandListener != null) {
                    commandListener.onTerminatedCommand(commandEvent, this);
                }
            } catch (Throwable tt) {
                LOGGER.warn("Exception when trying to give feedback about an error for server " + configForServer +
                        ": " + tt.getMessage());
            }
        }
    }

    ;

    protected abstract void executeWithConfig(CommandEvent commandEvent, Config config);

    public void replyBasedOnConfigAndRemoveAfter(Config config, CommandEvent commandEvent,
                                                 String message, int numberOfSeconds) {
        getFeedbackStrategy(config).reply(config, commandEvent, message, numberOfSeconds, localeService);
    }

    protected static boolean isUserServerMod(CommandEvent commandEvent, Config config) {
        boolean isServerMod = false;
        final String modPermissionGroup = config.getModPermissionGroup();
        if (!StringUtils.isEmpty(modPermissionGroup)) {
            for (Role role : commandEvent.getMember().getRoles()) {
                if (modPermissionGroup.trim().toLowerCase().equalsIgnoreCase(role.getName().toLowerCase())) {
                    isServerMod = true;
                    break;
                }
            }
        }
        return isServerMod;
    }

    protected static boolean isUserAdministrator(CommandEvent commandEvent) {
        return PermissionUtil.checkPermission(commandEvent.getTextChannel(),
                commandEvent.getMember(), Permission.ADMINISTRATOR);
    }
}
