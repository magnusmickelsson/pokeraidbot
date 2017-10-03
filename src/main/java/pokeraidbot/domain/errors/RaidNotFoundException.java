package pokeraidbot.domain.errors;

import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;

// todo: i18n - should accept user config/locale
public class RaidNotFoundException extends RuntimeException {
    public RaidNotFoundException(Gym gym, LocaleService localeService) {
        super(localeService.getMessageFor(LocaleService.NO_RAID_AT_GYM, LocaleService.DEFAULT, gym.getName()));
    }
}
