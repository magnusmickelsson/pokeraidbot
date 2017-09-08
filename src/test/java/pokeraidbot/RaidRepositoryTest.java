package pokeraidbot;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.domain.ClockService;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
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
        repo = new RaidRepository(clockService);
        Utils.setClockService(clockService);
        gymRepository = new GymRepository(new CSVGymDataReader("/gyms_uppsala.csv").readAll());
        pokemonRepository = new PokemonRepository("/mons.json");
    }

    @Test
    public void testSignUp() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0));
        final LocalTime now = clockService.getCurrentTime();
        LocalTime endOfRaid = now.plusHours(1);
        final Gym gym = gymRepository.findByName("Blenda");
        Raid enteiRaid = new Raid(pokemonRepository.getByName("Entei"), endOfRaid, gym);
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
