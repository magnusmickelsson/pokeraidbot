package pokeraidbot.infrastructure.botsupport.gymhuntr;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;

import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GymHuntrRaidEventListenerTest {

    private ClockService clockService;

    @Before
    public void setUp() throws Exception {
        clockService = new ClockService();
    }

    @Test
    public void testParseGymhuntrMessageForBossIntoArguments() throws Exception {
        final String typeOfMessage = "**Sunnerstakyrkan.**\n" +
                "Ninetales\n" +
                "CP: 14914.\n" +
                "*Raid Ending: 0 hours 15 min 59 sec*";
        final List<String> arguments = GymHuntrRaidEventListener.gymhuntrArgumentsToCreateRaid(
                "Level 3 Raid has started!", typeOfMessage, clockService);
        assertThat(arguments.size(), is(3));
        final Iterator<String> iterator = arguments.iterator();
        assertThat(iterator.next(), is("Sunnerstakyrkan"));
        assertThat(iterator.next(), is("Ninetales"));
        assertThat(iterator.next(), is(Utils.printTime(clockService.getCurrentTime()
                .plusMinutes(15).plusSeconds(59))));
    }

    @Test
    public void testParseGymhuntrMessageForTier5EggIntoArguments() throws Exception {
        final String typeOfMessage = "**Staty utanför Svedbergs Laboratoriet .**\n" +
                "*Raid Starting: 0 hours 15 min 59 sec*";
        final List<String> arguments = GymHuntrRaidEventListener.gymhuntrArgumentsToCreateRaid(
                "Level 5 Raid is starting soon!", typeOfMessage, clockService);
        assertThat(arguments.size(), is(3));
        final Iterator<String> iterator = arguments.iterator();
        assertThat(iterator.next(), is("Staty utanför Svedbergs Laboratoriet"));
        assertThat(iterator.next(), is("Ho-Oh"));
        assertThat(iterator.next(), is(Utils.printTime(clockService.getCurrentTime()
                .plusMinutes(15).plusSeconds(59).plusMinutes(Utils.RAID_DURATION_IN_MINUTES))));
    }

    @Test
    public void testParsePokeAlarmBossMessageIntoArguments() throws Exception {
        clockService.setMockTime(LocalTime.of(19, 0, 0));
        final List<String> arguments = GymHuntrRaidEventListener.pokeAlarmArgumentsToCreateRaid(
                "T3 Center raid is available against Raikou!",
                "Level 5 is available until 19:16:10 (46m 2s).", clockService);
        assertThat(arguments.size(), is(3));
        final Iterator<String> iterator = arguments.iterator();
        assertThat(iterator.next(), is("T3 Center"));
        assertThat(iterator.next(), is("Raikou"));
        assertThat(iterator.next(), is(Utils.printTime(clockService.getCurrentTime()
                .plusMinutes(16).plusSeconds(10))));
    }

    @Test
    public void testParsePokeAlarmTier5EggMessageIntoArguments() throws Exception {
        clockService.setMockTime(LocalTime.of(18, 0, 0));
        final List<String> arguments = GymHuntrRaidEventListener.pokeAlarmArgumentsToCreateRaid(
                "T3 Center has a level 5", "Raid will hatch 18:31:10 (54m 32s).", clockService);
        assertThat(arguments.size(), is(3));
        final Iterator<String> iterator = arguments.iterator();
        assertThat(iterator.next(), is("T3 Center"));
        assertThat(iterator.next(), is("Ho-Oh"));
        assertThat(iterator.next(), is(Utils.printTime(clockService.getCurrentTime()
                .plusMinutes(31).plusSeconds(10).plusMinutes(Utils.RAID_DURATION_IN_MINUTES))));
    }
}
