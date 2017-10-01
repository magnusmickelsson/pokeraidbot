package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.TestServerMain;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class GymRepositoryTest {
    public static final ConfigRepository configRepository = Mockito.mock(ConfigRepository.class);
    GymRepository repo;
    private final Gym gym = new Gym("Hästen", "3690325", "59.844542", "17.63993",
            "https://lh5.ggpht.com/HFkcuwx3HyE3TCiO9M2JvYB8_9wClxmnfQEfp7aLsqISxjQ8C5r89Hr_LIC44zercO6QcIu90hllcMbw7PPq");

    @Before
    public void setUp() throws Exception {
        final Config uppsalaConfig = new Config("uppsala", "uppsala");
        when(configRepository.getConfigForServer("uppsala")).thenReturn(uppsalaConfig);
        final Config angelholmConfig = new Config("ängelholm", "ängelholm");
        when(configRepository.getConfigForServer("ängelholm")).thenReturn(angelholmConfig);
        final HashMap<String, Config> configMap = new HashMap<>();
        configMap.put("uppsala", uppsalaConfig);
        configMap.put("ängelholm", angelholmConfig);
        when(configRepository.getAllConfig()).thenReturn(configMap);
        repo = TestServerMain.getGymRepositoryForConfig(new LocaleService(), configRepository);
    }

    @Test
    public void allGymsAreRead() {
        assertThat(repo.getAllGymsForRegion("uppsala").size(), is(186));
    }

    @Test
    public void findGymByName() throws Exception {
        assertThat(repo.findByName("Hästen", "uppsala"), is(gym));
    }

    @Test
    public void findGymByFuzzySearch() throws Exception {
        assertThat(repo.search("Greger","hosten", "uppsala"), is(gym));
    }
    
    @Test
    public void findGymById() throws Exception {
        assertThat(repo.findById("3690325", "uppsala"), is(gym));
    }
}
