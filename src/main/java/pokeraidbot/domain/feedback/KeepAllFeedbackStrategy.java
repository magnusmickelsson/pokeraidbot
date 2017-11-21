package pokeraidbot.domain.feedback;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import main.BotServerMain;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.concurrent.TimeUnit;

public class KeepAllFeedbackStrategy implements FeedbackStrategy {
    public KeepAllFeedbackStrategy() {
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
    public void replyAndKeep(Config config, CommandEvent commandEvent, String message) {
        reply(config, commandEvent, message);
    }

    @Override
    public void replyMap(Config config, CommandEvent commandEvent, MessageEmbed message) {
        reply(config, commandEvent, message);
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
            commandEvent.replyInDM(throwable.getMessage());
            commandEvent.reactError();
        } else {
            commandEvent.reactError();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(throwable.getMessage());
            final MessageEmbed messageEmbed = embedBuilder.build();
            commandEvent.reply(messageEmbed);
        }
    }

    @Override
    public void reply(Config config, CommandEvent commandEvent, String message, int numberOfSecondsBeforeRemove,
                      LocaleService localeService) {
        Validate.isTrue(numberOfSecondsBeforeRemove > 5);
        if (config != null && config.getReplyInDmWhenPossible()) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reactSuccess();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(message);
            commandEvent.reply(embedBuilder.build());
        }
    }
}
