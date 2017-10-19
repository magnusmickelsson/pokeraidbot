package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.domain.config.LocaleService;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocaleServiceTest {
    private LocaleService localeService;

    @Before
    public void setUp() throws Exception {
        localeService = new LocaleService("sv");
    }

    @Test
    public void getMessage() throws Exception {
        assertThat(localeService.getMessageFor("GENERIC_USER_ERROR", Locale.ENGLISH, "Hej", "Hopp"),
                is("Hej: Hopp"));
    }

    @Test
    public void storeMessage() throws Exception {
        localeService.storeMessage("TEST", Locale.ENGLISH, "Test message %1 %2 %3 %3 wee");
        assertThat(localeService.getMessageFor("TEST", Locale.ENGLISH, "Hej", "Hopp", "Mupp"),
                is("Test message Hej Hopp Mupp Mupp wee"));
    }
}
