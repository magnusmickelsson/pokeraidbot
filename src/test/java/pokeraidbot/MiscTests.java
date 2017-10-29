package pokeraidbot;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.jda.SignupWithPlusCommandListener;

import static org.hamcrest.CoreMatchers.is;
import static pokeraidbot.jda.SignupWithPlusCommandListener.*;

public class MiscTests {
    @Test
    public void testRegExpForPlusCommand() throws Exception {
        Assert.assertThat("+1 10:35 Hästen".matches(plusXRegExp),
                is(true));
        Assert.assertThat("+1 anmälning känns vettigt att ha i chatten".matches(plusXRegExp),
                is(false));

        Assert.assertThat("+1 Hästen 10:35".matches(plusXRegExp),
                is(false));
    }

//    @Test
//    public void testExtractArgumentsForPlusCommandWithAnnoyances() throws Exception {
//        final ServerConfigRepository serverConfigRepository = Mockito.mock(ServerConfigRepository.class);
//        final LocaleService localeService = Mockito.mock(LocaleService.class);
//        Assert.assertThat(SignupWithPlusCommandListener.extractArgumentsForPlusCommand(
//                "+1 kommer till Hästen vid 10.35", new GymRepository(serverConfigRepository, localeService)),
//                is(new String[]{"1", "10.35", "Hästen"}));
//        Assert.assertThat(SignupWithPlusCommandListener.extractArgumentsForPlusCommand(
//                "Hästen +5 vid 1035", new GymRepository(serverConfigRepository, localeService)),
//                is(new String[]{"5", "10.35", "Hästen"}));
//        Assert.assertThat(SignupWithPlusCommandListener.extractArgumentsForPlusCommand(
//                "+1 Hästen 10:35", new GymRepository(serverConfigRepository, localeService)),
//                is(new String[]{"1", "10.35", "Hästen"}));
//    }
}
