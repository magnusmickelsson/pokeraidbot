package pokeraidbot.domain.raid;

import net.dv8tion.jda.core.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.TestServerMain;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntity;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestServerMain.class})
@Transactional
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
    @Autowired
    ServerConfigRepository serverConfigRepository;
    @Autowired
    RaidEntityRepository raidEntityRepository;

    @Before
    public void setUp() throws Exception {
        Utils.setClockService(clockService);
        gymRepository = TestServerMain.getGymRepositoryForConfig(localeService, serverConfigRepository);
        pokemonRepository = new PokemonRepository("/mons.json", localeService);
        raidEntityRepository.deleteAllInBatch();
    }

    @Test
    public void testSignUp() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid, gym, localeService, uppsalaRegion);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        try {
            repo.newRaid(user, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }
        User user2 = mock(User.class);
        String userName = "testUser2";
        when(user2.getName()).thenReturn(userName);
        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user2);
        enteiRaid.setId(raid.getId()); // Set to same id for equals comparison
        enteiRaid.setCreator(raid.getCreator()); // Set creator to same for equals comparison
        assertThat(raid, is(enteiRaid));
        int howManyPeople = 3;
        LocalTime arrivalTime = nowTime.plusMinutes(30);
        raid.signUp(user2, howManyPeople, arrivalTime, repo);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));

        final Raid raidFromDb = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user2);
        assertThat(raidFromDb, is(raid));
        assertThat(raidFromDb.getSignUps().size(), is(1));
    }

    @Test
    public void changePokemonWorks() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid, gym, localeService, uppsalaRegion);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        try {
            repo.newRaid(user, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }

        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user);
        Raid changedRaid = repo.changePokemon(raid, pokemonRepository.search("Mewtwo", user));
        assertThat(raid.getEndOfRaid(), is(changedRaid.getEndOfRaid()));
        assertThat(raid.getGym(), is(changedRaid.getGym()));
        assertThat(raid.getSignUps(), is(changedRaid.getSignUps()));
        assertThat(raid.getRegion(), is(changedRaid.getRegion()));
        assertThat(raid.getPokemon().getName(), is("Entei"));
        assertThat(changedRaid.getPokemon().getName(), is("Mewtwo"));
    }

    @Test
    public void changeEndOfRaidWorks() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid, gym, localeService, uppsalaRegion);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        try {
            repo.newRaid(user, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }

        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user);
        Raid changedRaid = repo.changeEndOfRaid(raid.getId(), endOfRaid.plusMinutes(5));
        assertThat(raid.getEndOfRaid(), not(changedRaid.getEndOfRaid()));
        assertThat(changedRaid.getEndOfRaid(), is(raid.getEndOfRaid().plusMinutes(5)));
        assertThat(raid.getGym(), is(changedRaid.getGym()));
        assertThat(raid.getSignUps(), is(changedRaid.getSignUps()));
        assertThat(raid.getRegion(), is(changedRaid.getRegion()));
        assertThat(raid.getPokemon().getName(), is(changedRaid.getPokemon().getName()));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void moveTimeForGroupWorks() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid, gym, localeService, uppsalaRegion);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        try {
            repo.newRaid(user, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }
        List<User> raiders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final User raider = mock(User.class);
            when(raider.getName()).thenReturn("User" + i);
            raiders.add(raider);
        }

        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user);
        final LocalDateTime raidGroupTime = endOfRaid.minusMinutes(10);
        for (User raider : raiders) {
            repo.addSignUp(raider, raid, new SignUp(raider.getName(), 2, raidGroupTime.toLocalTime()));
        }
        final LocalDateTime newRaidGroupTime = raidGroupTime.plusMinutes(2);
        RaidEntity entity = raidEntityRepository.findOne(raid.getId());
        repo.moveAllSignUpsForTimeToNewTime(raid.getId(), raidGroupTime, newRaidGroupTime, user);
        entity = raidEntityRepository.findOne(raid.getId());
        raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user);
        entity = raidEntityRepository.findOne(raid.getId());
        assertThat(raid.getSignUps().size(), is(5));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(10));
        assertThat(raid.getSignUpsAt(newRaidGroupTime.toLocalTime()).size(), is(5));
        assertThat(raid.getSignUpsAt(raidGroupTime.toLocalTime()).size(), is(0));
    }

    // todo: testcases for the intricate rules around EX raids
}
