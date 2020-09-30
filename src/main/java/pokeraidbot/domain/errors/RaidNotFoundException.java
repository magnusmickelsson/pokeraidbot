package pokeraidbot.domain.errors;

import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;

public class RaidNotFoundException extends RuntimeException {
    public RaidNotFoundException(Gym gym, LocaleService localeService, User user) {
        super(localeService.getMessageFor(LocaleService.NO_RAID_AT_GYM, localeService.getLocaleForUser(user),
                gym.getName()));
    }
}
