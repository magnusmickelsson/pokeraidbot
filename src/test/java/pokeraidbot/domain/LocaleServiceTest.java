package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class LocaleServiceTest {
    private LocaleService localeService;

    @Before
    public void setUp() throws Exception {
        UserConfigRepository userConfigRepository = Mockito.mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        localeService = new LocaleService("sv", userConfigRepository);
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
