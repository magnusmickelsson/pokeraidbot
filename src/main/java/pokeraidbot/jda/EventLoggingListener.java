package pokeraidbot.jda;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventLoggingListener implements EventListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoggingListener.class);

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            final List<Guild> guilds = event.getJDA().getGuilds();
            for (Guild guild : guilds) {
//                // todo: i18n
//                // "Hello, humans. **I'm alive!** Here to help with your pokemon raiding needs. Type: !raid usage"
//                if (guild.getDefaultChannel() != null) {
//                    guild.getDefaultChannel().sendMessage("Hej på er, människor. Pokeraidbot är här. " +
//                            "Skriv följande för att få info om vad jag kan göra: !raid usage").queue();
//                }
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
