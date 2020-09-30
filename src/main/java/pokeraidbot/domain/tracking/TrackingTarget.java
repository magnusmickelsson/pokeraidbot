package pokeraidbot.domain.tracking;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.Config;

public interface TrackingTarget {
    boolean canHandle(Config config, User user, Raid raid, Guild guild);
    void handle(Guild guild, LocaleService localeService, Config config, User user, Raid raid, String inputMessage);
}
