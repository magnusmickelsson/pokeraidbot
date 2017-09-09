package pokeraidbot.domain.errors;

import pokeraidbot.domain.Gym;
import pokeraidbot.domain.LocaleService;

public class RaidNotFoundException extends RuntimeException {
    public RaidNotFoundException(Gym gym, LocaleService localeService) {
        super(localeService.getMessageFor(LocaleService.NO_RAID_AT_GYM, LocaleService.DEFAULT, gym.getName()));
    }
}
