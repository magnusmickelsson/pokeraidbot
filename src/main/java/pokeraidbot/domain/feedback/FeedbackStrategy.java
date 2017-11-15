package pokeraidbot.domain.feedback;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.concurrent.TimeUnit;

public interface FeedbackStrategy {
    default void replyThenDeleteFeedbackAndOriginMessageAfterXTime(CommandEvent commandEvent,
                                                                  MessageEmbed messageEmbed,
                                                                  int timeToRemoveMessages,
                                                                  TimeUnit timeUnit) {
        commandEvent.reply(messageEmbed, msg -> {
            commandEvent.getMessage().delete()
                    .queueAfter(timeToRemoveMessages, timeUnit); // Clean up origin message
            msg.delete()
                    .queueAfter(timeToRemoveMessages, timeUnit); // Clean up feedback after x time
        });
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
    void handleOriginMessage(CommandEvent commandEvent);
    void replyAndKeep(Config config, CommandEvent commandEvent, String message);
}
