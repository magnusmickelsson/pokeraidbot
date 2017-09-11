package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pokeraidbot.TestServerMain;
import pokeraidbot.Utils;
import pokeraidbot.infrastructure.CSVGymDataReader;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestServerMain.class})
public class RaidRepositoryTest {
    @Autowired
    RaidRepository repo;
    @Autowired
    GymRepository gymRepository;
    @Autowired
    PokemonRepository pokemonRepository;
    @Autowired
    ClockService clockService;
    @Autowired
    LocaleService localeService;

    @Before
    public void setUp() throws Exception {
        Utils.setClockService(clockService);
        gymRepository = new GymRepository(new CSVGymDataReader("/gyms_uppsala.csv").readAll(), localeService);
        pokemonRepository = new PokemonRepository("/mons.json", localeService);
    }

    @Test
    public void testSignUp() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0));
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
        raid.signUp(userName, howManyPeople, arrivalTime, repo);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));

        final Raid raidFromDb = repo.getRaid(gym);
        assertThat(raidFromDb, is(raid));
        assertThat(raidFromDb.getSignUps().size(), is(1));
    }
}
