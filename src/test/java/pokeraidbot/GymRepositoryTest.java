package pokeraidbot;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.domain.Gym;
import pokeraidbot.infrastructure.CSVGymDataReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GymRepositoryTest {
    GymRepository repo;
    private final Gym gym = new Gym("Hästen", "3690325", "59.844542", "17.63993",
            "https://lh5.ggpht.com/HFkcuwx3HyE3TCiO9M2JvYB8_9wClxmnfQEfp7aLsqISxjQ8C5r89Hr_LIC44zercO6QcIu90hllcMbw7PPq");

    @Before
    public void setUp() throws Exception {
        repo = new GymRepository(new CSVGymDataReader("/gyms_uppsala.csv").readAll());
    }

    @Test
    public void allGymsAreRead() {
        assertThat(repo.getAllGyms().size(), is(373));
    }

    @Test
    public void findGymByName() throws Exception {
        assertThat(repo.findByName("Hästen"), is(gym));
    }

    @Test
    public void findGymByFuzzySearch() throws Exception {
        assertThat(repo.findByName("hasten"), is(gym));
    }

    @Test
    public void findGymById() throws Exception {
        assertThat(repo.findById("3690325"), is(gym));
    }
}
