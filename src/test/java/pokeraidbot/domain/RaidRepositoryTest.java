package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.Utils;
import pokeraidbot.domain.*;
import pokeraidbot.infrastructure.CSVGymDataReader;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidRepositoryTest {
    RaidRepository repo;
    GymRepository gymRepository;
    PokemonRepository pokemonRepository;
    ClockService clockService = new ClockService();

    @Before
    public void setUp() throws Exception {
        final LocaleService localeService = new LocaleService();
        repo = new RaidRepository(clockService, localeService);
        Utils.setClockService(clockService);
        gymRepository = new GymRepository(new CSVGymDataReader("/gyms_uppsala.csv").readAll(), localeService);
        pokemonRepository = new PokemonRepository("/mons.json", localeService);
    }

    @Test
    public void testSignUp() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalTime now = clockService.getCurrentTime();
        LocalTime endOfRaid = now.plusHours(1);
        final Gym gym = gymRepository.findByName("Blenda");
        Raid enteiRaid = new Raid(pokemonRepository.getByName("Entei"), endOfRaid, gym, new LocaleService());
        String raidCreatorName = "testUser1";
        try {
            repo.newRaid(raidCreatorName, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        Raid raid = repo.getRaid(gym);
        assertThat(raid, is(enteiRaid));
        String userName = "testUser2";
        int howManyPeople = 3;
        LocalTime arrivalTime = now.plusMinutes(30);
        raid.signUp(userName, howManyPeople, arrivalTime);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));
    }
}
