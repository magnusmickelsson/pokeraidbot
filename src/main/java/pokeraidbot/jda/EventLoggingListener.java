package pokeraidbot.jda;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventLoggingListener implements EventListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoggingListener.class);

    public EventLoggingListener() {
    }

    @Override
    public void onEvent(GenericEvent event) {
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
