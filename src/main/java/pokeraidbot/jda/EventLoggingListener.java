package pokeraidbot.jda;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.commands.RaidOverviewCommand;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventLoggingListener implements EventListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoggingListener.class);
    private ServerConfigRepository serverConfigRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final ClockService clockService;
    protected static final ExecutorService executorService = Executors.newCachedThreadPool();

    public EventLoggingListener(ServerConfigRepository serverConfigRepository,
                                RaidRepository raidRepository, LocaleService localeService,
                                ClockService clockService) {
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.clockService = clockService;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            final List<Guild> guilds = event.getJDA().getGuilds();
            for (Guild guild : guilds) {
                Config config = serverConfigRepository.getConfigForServer(guild.getName().trim().toLowerCase());
                if (config != null) {
                    final String messageId = config.getOverviewMessageId();
                    if (!StringUtils.isEmpty(messageId)) {
                        for (MessageChannel channel : guild.getTextChannels()) {
                            try {
                                if (channel.getMessageById(messageId).complete() != null) {
                                    final Callable<Boolean> overviewTask =
                                            RaidOverviewCommand.getMessageRefreshingTaskToSchedule(
                                                    null, config, messageId, localeService, serverConfigRepository,
                                                    raidRepository, clockService, channel
                                            );
                                    executorService.submit(overviewTask);
                                    LOGGER.info("Found overview message for channel " + channel.getName() +
                                            " (server " + guild.getName() + "). Attaching to it.");
                                    if (guild.getDefaultChannel() != null) {
                                        // todo: i18n
                                        guild.getDefaultChannel().sendMessage(
                                                "Pokeraidbot är här. Raidöversikten uppdateras i kanalen " +
                                                        "#" + channel.getName() +
                                                        ". För info om botten: *!raid usage*").queue();
                                    }
                                    return;
                                }
                            } catch (UserMessedUpException e) {
                                channel.sendMessage(e.getMessage()).queue();
                            } catch (ErrorResponseException e) {
                                // We couldn't find the message in this channel, move to next
                            }
                        }
                    }
                }
            }
        }

        if (LOGGER.isTraceEnabled()) {
            if (event instanceof GuildMessageReactionAddEvent) {
                final GuildMessageReactionAddEvent reactionAddEvent = (GuildMessageReactionAddEvent) event;
                LOGGER.trace("Reaction: " + reactionAddEvent.getUser() + " - " + reactionAddEvent.getReaction() +
                        " - " + reactionAddEvent.getReactionEmote());
            } else if (event instanceof GuildMessageReceivedEvent) {
                final GuildMessageReceivedEvent guildMessageReceivedEvent = (GuildMessageReceivedEvent) event;
                LOGGER.trace("Message from " + guildMessageReceivedEvent.getAuthor() + ": " + guildMessageReceivedEvent.getMessage());
            } else if (event instanceof GuildMessageEmbedEvent) {
                final GuildMessageEmbedEvent guildMessageReceivedEvent = (GuildMessageEmbedEvent) event;
                final List<MessageEmbed> messageEmbeds = guildMessageReceivedEvent.getMessageEmbeds();
                for (MessageEmbed embed : messageEmbeds)
                    LOGGER.trace("Embed message from " + embed.getAuthor() + ": " + String.valueOf(embed.getTitle()) +
                            " - " + String.valueOf(embed.getDescription()));
            }
        }
    }
}
