package pokeraidbot.domain.raid;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
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
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntity;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.time.LocalDate;
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
        pokemonRepository = new PokemonRepository("/pokemons.csv", localeService);
        raidEntityRepository.deleteAllInBatch();
    }

    @Test
    public void testCreateGetAndDeleteGroup() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid,
                gym, localeService, uppsalaRegion, false);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        Guild guild = mock(Guild.class);
        Config config = mock(Config.class);
        Raid enteiRaid1 = enteiRaid;
        try {
            enteiRaid1 = repo.newRaid(user, enteiRaid1, guild, config, "test");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }
        enteiRaid = enteiRaid1;
        User user2 = mock(User.class);
        String userName = "testUser2";
        when(user2.getName()).thenReturn(userName);
        LocalTime arrivalTime = nowTime.plusMinutes(30);
        RaidGroup group = new RaidGroup("testserver", "channel", "infoId", "emoteId", "userId",
                LocalDateTime.of(LocalDate.now(), arrivalTime));
        group = repo.newGroupForRaid(user2, group, enteiRaid, guild, config);
        List<RaidGroup> groupsForServer = repo.getGroupsForServer("testserver");
        assertThat(group != null, is(true));
        assertThat(groupsForServer.size(), is(1));
        assertThat(groupsForServer.iterator().next(), is(group));

        RaidGroup deleted = repo.deleteGroup(enteiRaid.getId(), group.getId());
        assertThat(deleted != null, is(true));
        groupsForServer = repo.getGroupsForServer("testserver");
        assertThat(groupsForServer.size(), is(0));
    }

    @Test
    public void testSignUp() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid,
                gym, localeService, uppsalaRegion, false);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        Guild guild = mock(Guild.class);
        Config config = mock(Config.class);

        try {
            repo.newRaid(user, enteiRaid, guild, config, "test");
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
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid,
                gym, localeService, uppsalaRegion, false);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        Guild guild = mock(Guild.class);
        Config config = mock(Config.class);

        when(user.getName()).thenReturn(raidCreatorName);
        try {
            repo.newRaid(user, enteiRaid, guild, config, "test");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }

        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user);
        Raid changedRaid = repo.changePokemon(raid, pokemonRepository.search("Mewtwo", user), guild,
                config, user, "test");
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
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid,
                gym, localeService, uppsalaRegion, false);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        Guild guild = mock(Guild.class);
        Config config = mock(Config.class);

        try {
            repo.newRaid(user, enteiRaid, guild, config, "test");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }

        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion, user);
        Raid changedRaid = repo.changeEndOfRaid(raid.getId(), endOfRaid.plusMinutes(5), guild, config, user, "test");
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
        Raid enteiRaid = new Raid(pokemonRepository.search("Entei", null), endOfRaid,
                gym, localeService, uppsalaRegion, false);
        String raidCreatorName = "testUser1";
        User user = mock(User.class);
        when(user.getName()).thenReturn(raidCreatorName);
        Guild guild = mock(Guild.class);
        Config config = mock(Config.class);

        try {
            repo.newRaid(user, enteiRaid, guild, config, "test");
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
