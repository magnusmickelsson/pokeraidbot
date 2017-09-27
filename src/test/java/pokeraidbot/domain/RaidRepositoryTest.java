package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pokeraidbot.TestServerMain;
import pokeraidbot.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestServerMain.class})
public class RaidRepositoryTest {
    private static final String uppsalaRegion = "uppsala";
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
        gymRepository = TestServerMain.getGymRepositoryForConfig(localeService, TestServerMain.configRepositoryForTests());
        pokemonRepository = new PokemonRepository("/mons.json", localeService);
    }

    @Test
    public void testSignUp() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusHours(1);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.getByName("Entei"), endOfRaid, gym, new LocaleService(), uppsalaRegion);
        String raidCreatorName = "testUser1";
        try {
            repo.newRaid(raidCreatorName, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion);
        assertThat(raid, is(enteiRaid));
        String userName = "testUser2";
        int howManyPeople = 3;
        LocalTime arrivalTime = nowTime.plusMinutes(30);
        raid.signUp(userName, howManyPeople, arrivalTime, repo);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));

        final Raid raidFromDb = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion);
        assertThat(raidFromDb, is(raid));
        assertThat(raidFromDb.getSignUps().size(), is(1));
    }
}
