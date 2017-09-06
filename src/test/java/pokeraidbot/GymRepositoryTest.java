package pokeraidbot;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.domain.Gym;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GymRepositoryTest {
    GymRepository repo;

    @Before
    public void setUp() throws Exception {
        repo = new GymRepository();
    }

    @Test
    public void findGymByName() throws Exception {
        Gym hästen = new Gym("HÄSTEN", "3690325");
        assertThat(repo.findByName("Hästen"), is(hästen));
    }

//    @Test
//    public void findGymByFuzzySearch() throws Exception {
//        Gym hästen = new Gym("Hästen", "3690325");
//        assertThat(repo.findByName("hasten"), is(hästen));
//    }

    @Test
    public void findGymById() throws Exception {
        Gym hästen = new Gym("HÄSTEN", "3690325");
        assertThat(repo.findById("3690325"), is(hästen));
    }
}
