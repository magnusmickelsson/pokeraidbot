package pokeraidbot.infrastructure.botsupport.gymhuntr;

import org.junit.Test;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GymHuntrRaidEventListenerTest {
    @Test
    public void testParseDescriptionIntoArguments() throws Exception {
        final String typeOfMessage = "**Sunnerstakyrkan.**\n" +
                "Ninetales\n" +
                "CP: 14914.\n" +
                "*Raid Ending: 0 hours 15 min 59 sec*";
        final ClockService clockService = new ClockService();
        final List<String> arguments = GymHuntrRaidEventListener.argumentsToCreateRaid(typeOfMessage, clockService);
        assertThat(arguments.size(), is(3));
        final Iterator<String> iterator = arguments.iterator();
        assertThat(iterator.next(), is("Sunnerstakyrkan"));
        assertThat(iterator.next(), is("Ninetales"));
        assertThat(iterator.next(), is(Utils.printTime(clockService.getCurrentTime()
                .plusMinutes(15).plusSeconds(59))));
    }
}
