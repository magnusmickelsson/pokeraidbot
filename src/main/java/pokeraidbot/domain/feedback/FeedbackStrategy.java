package pokeraidbot.domain.feedback;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;

public interface FeedbackStrategy {
    void reply(Config config, CommandEvent commandEvent, String message);
    void reply(Config config, CommandEvent commandEvent, MessageEmbed message);
    void replyError(Config config, CommandEvent commandEvent, Throwable throwable, LocaleService localeService);
    void reply(Config config, CommandEvent commandEvent, String message, int numberOfSecondsBeforeRemove,
               LocaleService localeService);
}
