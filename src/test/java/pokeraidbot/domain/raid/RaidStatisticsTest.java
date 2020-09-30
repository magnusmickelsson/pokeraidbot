package pokeraidbot.domain.raid;

import net.dv8tion.jda.api.entities.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.TestServerMain;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import java.time.LocalTime;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestServerMain.class})
@Transactional
public class RaidStatisticsTest {
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

    private LocalTime currentTime = LocalTime.of(9, 0);

    @Before
    public void setUp() throws Exception {
        clockService.setMockTime(currentTime);
        Utils.setClockService(clockService);
        gymRepository = TestServerMain.getGymRepositoryForConfig(localeService, serverConfigRepository);
        pokemonRepository = new PokemonRepository("/pokemons.csv", localeService);
    }

    @Test
    @Ignore //  Not done
    public void updateStatisticsWithRaid() throws Exception {
        RaidStatistics statistics = new RaidStatistics();
        Raid raid = new Raid(pokemonRepository.getByName("Groudon"), clockService.getCurrentDateTime(),
                gymRepository.findByName("Blenda", uppsalaRegion), localeService, uppsalaRegion, false);
        User user = mock(User.class);
        when(user.getName()).thenReturn("test");
        raid.signUp(user, 4, currentTime.plusMinutes(10), repo);
        statistics.updateForRaid(raid);
        Map<String, ServerRaidStatistics> stats = statistics.getAll();
        final ServerRaidStatistics statsForUppsala = stats.get(uppsalaRegion);
        final ServerRaidStatistics expectedStats = new ServerRaidStatistics();
        // TEST NOT DONE YET
        // assertThat(statsForUppsala, is(expectedStats));
    }
}
