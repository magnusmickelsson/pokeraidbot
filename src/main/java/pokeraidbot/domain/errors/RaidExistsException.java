package pokeraidbot.domain.errors;

import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.Raid;

import java.util.Locale;

public class RaidExistsException extends RuntimeException {
    public RaidExistsException(String raidCreatorName, Raid existingRaid, LocaleService localeService, Locale locale) {
        super(localeService.getMessageFor(LocaleService.RAID_EXISTS, locale, raidCreatorName,
                existingRaid.getGym().getName(), existingRaid.getPokemon().getName()));
    }
}
