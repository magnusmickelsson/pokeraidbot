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

    public EventLoggingListener() {
    }

    @Override
    public void onEvent(Event event) {
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
