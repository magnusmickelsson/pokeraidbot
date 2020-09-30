package pokeraidbot.domain.errors;

import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.raid.Raid;

import java.util.Locale;

public class RaidExistsException extends RuntimeException {
    public RaidExistsException(User user, Raid existingRaid, LocaleService localeService, Locale locale) {
        super(localeService.getMessageFor(LocaleService.RAID_EXISTS, locale, user.getName(),
                existingRaid.getGym().getName(), existingRaid.getPokemon().getName()));
    }
}
