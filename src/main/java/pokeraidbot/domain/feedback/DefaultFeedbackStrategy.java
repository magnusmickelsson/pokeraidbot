package pokeraidbot.domain.feedback;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import main.BotServerMain;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.concurrent.TimeUnit;

public class DefaultFeedbackStrategy implements FeedbackStrategy {
    public DefaultFeedbackStrategy() {
    }

    @Override
    public void reply(Config config, CommandEvent commandEvent, String message) {
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

    @Override
    public void reply(Config config, CommandEvent commandEvent, MessageEmbed message) {
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(message);
        }
    }

    @Override
    public void replyError(Config config, CommandEvent commandEvent, Throwable throwable, LocaleService localeService) {
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDM(throwable.getMessage());
            commandEvent.reactError();
        } else {
            commandEvent.reactError();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(throwable.getMessage());
            final String msgRemoveText = localeService.getMessageFor(LocaleService.ERROR_KEEP_CHAT_CLEAN,
                    localeService.getLocaleForUser(commandEvent.getAuthor()),
                    String.valueOf(BotServerMain.timeToRemoveFeedbackInSeconds));
            embedBuilder.setFooter(msgRemoveText, null);
            final MessageEmbed messageEmbed = embedBuilder.build();
            commandEvent.reply(messageEmbed, msg -> {
                commandEvent.getMessage().delete()
                        .queueAfter(BotServerMain.timeToRemoveFeedbackInSeconds, TimeUnit.SECONDS); // Clean up bad message
                msg.delete()
                        .queueAfter(BotServerMain.timeToRemoveFeedbackInSeconds, TimeUnit.SECONDS); // Clean up feedback after x seconds
            });
        }
    }

    @Override
    public void reply(Config config, CommandEvent commandEvent, String message, int numberOfSecondsBeforeRemove,
                      LocaleService localeService) {
        // Give the caller some slack but not much
        Validate.isTrue(numberOfSecondsBeforeRemove > 5 && numberOfSecondsBeforeRemove < 60);
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
                    localeService.getLocaleForUser(commandEvent.getAuthor()), "" + numberOfSecondsBeforeRemove);

            embedBuilder.setFooter(msgRemoveText, null);
            commandEvent.reply(embedBuilder.build(), msg -> {
                msg.delete().queueAfter(numberOfSecondsBeforeRemove, TimeUnit.SECONDS); // Clean up feedback after x seconds
            });
        }
    }
}
