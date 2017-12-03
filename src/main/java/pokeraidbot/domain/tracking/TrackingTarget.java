package pokeraidbot.domain.tracking;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.Config;

public interface TrackingTarget {
//    boolean canHandle(CommandEvent commandEvent, Command command, Raid raid);
//    void handle(CommandEvent commandEvent, Command command, LocaleService localeService, Locale locale, Config config,
//                Raid raid);
//    boolean canHandle(GuildMessageReceivedEvent event, Raid raid);
//    void handle(GuildMessageReceivedEvent event, Raid raid, LocaleService localeService, Locale locale, Config config);
    boolean canHandle(Config config, User user, Raid raid);
    void handle(Guild guild, LocaleService localeService, Config config, User user, Raid raid);
}
