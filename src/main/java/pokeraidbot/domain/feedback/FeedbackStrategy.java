package pokeraidbot.domain.feedback;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.concurrent.TimeUnit;

public interface FeedbackStrategy {
    Logger LOGGER = LoggerFactory.getLogger(FeedbackStrategy.class);

    default void replyThenDeleteFeedbackAndOriginMessageAfterXTime(CommandEvent commandEvent,
                                                                   MessageEmbed messageEmbed,
                                                                   int timeToRemoveMessages,
                                                                   TimeUnit timeUnit) {
        try {
            commandEvent.reply(messageEmbed, msg -> {
                try {
                    commandEvent.getMessage().delete()
                            .queueAfter(timeToRemoveMessages, timeUnit); // Clean up origin message
                    msg.delete()
                            .queueAfter(timeToRemoveMessages, timeUnit); // Clean up feedback after x time
                } catch (Throwable t) {
                    LOGGER.warn("Exception when deleting messages in server " + msg.getGuild().getName() + ": " +
                            t.getMessage());
                }
            });
        } catch (Throwable t) {
            LOGGER.warn("Exception when replying to raw message " + commandEvent.getMessage().getContentRaw() +
                    " in server " + commandEvent.getGuild().getName() + ": " +
                    t.getMessage());
        }
    }

    default void sendMessageThenDeleteAfterXSeconds(CommandEvent commandEvent, MessageEmbed messageEmbed,
                                                    int timeToRemoveFeedback, TimeUnit timeUnit) {
        commandEvent.reply(messageEmbed, msg -> {
            msg.delete().queueAfter(timeToRemoveFeedback, timeUnit); // Clean up feedback after x time
        });
    }

    void reply(Config config, CommandEvent commandEvent, String message);

    void reply(Config config, CommandEvent commandEvent, MessageEmbed message);

    void replyError(Config config, CommandEvent commandEvent, Throwable throwable, LocaleService localeService);

    void reply(Config config, CommandEvent commandEvent, String message, int numberOfSecondsBeforeRemove,
               LocaleService localeService);

    void replyMap(Config config, CommandEvent commandEvent, MessageEmbed message);

    void replyMapInChat(Config config, CommandEvent commandEvent, MessageEmbed message);

    void handleOriginMessage(CommandEvent commandEvent);

    void handleOriginMessage(GuildMessageReceivedEvent event);

    void replyAndKeep(Config config, CommandEvent commandEvent, String message);
}
