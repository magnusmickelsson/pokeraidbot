package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.Locale;

public interface TrackingTarget {
    boolean canHandle(CommandEvent commandEvent, Command command);
    void handle(CommandEvent commandEvent, Command command, LocaleService localeService, Locale locale, Config config);
    boolean canHandle(GuildMessageReceivedEvent event, Raid raid);
    void handle(GuildMessageReceivedEvent event, Raid raid, LocaleService localeService, Locale locale, Config config);
}
