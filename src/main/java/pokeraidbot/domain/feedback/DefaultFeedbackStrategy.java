package pokeraidbot.domain.feedback;

import com.jagrosh.jdautilities.command.CommandEvent;
import main.BotServerMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
            commandEvent.replyInDm(message);
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
    public void replyAndKeep(Config config, CommandEvent commandEvent, String message) {
        reply(config, commandEvent, message);
    }

    @Override
    public void reply(Config config, CommandEvent commandEvent, MessageEmbed message) {
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDm(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(message);
        }
    }

    @Override
    public void replyMap(Config config, CommandEvent commandEvent, MessageEmbed message) {
        reply(config, commandEvent, message);
    }

    @Override
    public void replyMapInChat(Config config, CommandEvent commandEvent, MessageEmbed message) {
        commandEvent.reply(message);
    }

    @Override
    public void handleOriginMessage(CommandEvent commandEvent) {
    }

    @Override
    public void handleOriginMessage(GuildMessageReceivedEvent event) {

    }

    @Override
    public void replyError(Config config, CommandEvent commandEvent, Throwable throwable, LocaleService localeService) {
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDm(throwable.getMessage());
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
            replyThenDeleteFeedbackAndOriginMessageAfterXTime(commandEvent, messageEmbed,
                    BotServerMain.timeToRemoveFeedbackInSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public void reply(Config config, CommandEvent commandEvent, String message, int numberOfSecondsBeforeRemove,
                      LocaleService localeService) {
        // Give the caller some slack but not much
        Validate.isTrue(numberOfSecondsBeforeRemove > 5);
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDm(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reactSuccess();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(message);
            final String msgRemoveText = localeService.getMessageFor(LocaleService.KEEP_CHAT_CLEAN,
                    localeService.getLocaleForUser(commandEvent.getAuthor()), "" +
                            numberOfSecondsBeforeRemove);

            embedBuilder.setFooter(msgRemoveText, null);
            sendMessageThenDeleteAfterXSeconds(commandEvent, embedBuilder.build(),
                    numberOfSecondsBeforeRemove, TimeUnit.SECONDS);
        }
    }
}
