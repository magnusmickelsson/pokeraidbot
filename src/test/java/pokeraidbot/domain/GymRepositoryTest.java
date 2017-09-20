package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.TestServerMain;
import pokeraidbot.infrastructure.CSVGymDataReader;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GymRepositoryTest {
    public static final ConfigRepository configRepository = TestServerMain.configRepositoryForTests();
    GymRepository repo;
    private final Gym gym = new Gym("Hästen", "3690325", "59.844542", "17.63993",
            "https://lh5.ggpht.com/HFkcuwx3HyE3TCiO9M2JvYB8_9wClxmnfQEfp7aLsqISxjQ8C5r89Hr_LIC44zercO6QcIu90hllcMbw7PPq");

    @Before
    public void setUp() throws Exception {
        final HashMap<String, Config> configurationMap = new HashMap<>();
        configurationMap.put("uppsala", new Config("uppsala"));
        configurationMap.put("ängelholm", new Config("ängelholm"));

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
        assertThat(repo.findByName("hasten", "uppsala"), is(gym));
    }

    @Test
    public void findGymById() throws Exception {
        assertThat(repo.findById("3690325", "uppsala"), is(gym));
    }
}
