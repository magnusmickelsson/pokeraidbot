package pokeraidbot.domain.errors;

import pokeraidbot.domain.config.LocaleService;

import java.util.Locale;

public class GymNotFoundException extends RuntimeException {
    public GymNotFoundException(String name, LocaleService localeService, Locale locale, String region) {
        super(localeService.getMessageFor(LocaleService.GYM_NOT_FOUND, locale, name, region));
    }
}
