package pokeraidbot;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Pokemons;
import pokeraidbot.domain.Raid;
import pokeraidbot.infrastructure.CSVGymDataReader;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidRepositoryTest {
    RaidRepository repo;
    GymRepository gymRepository;

    @Before
    public void setUp() throws Exception {
        repo = new RaidRepository();
        gymRepository = new GymRepository(new CSVGymDataReader("/gyms_uppsala.csv").readAll());
    }

    @Test
    public void testSignUp() throws Exception {
        LocalTime endOfRaid = LocalTime.of(11, 05);
        final Gym hästen = gymRepository.findByName("Hästen");
        Raid enteiRaid = new Raid(Pokemons.ENTEI.getPokemon(), endOfRaid, hästen);
        String raidCreatorName = "testUser1";
        repo.newRaid(raidCreatorName, enteiRaid);
        Raid raid = repo.getRaid(hästen);
        assertThat(raid, is(enteiRaid));
        String userName = "testUser2";
        int howManyPeople = 3;
        LocalTime arrivalTime = LocalTime.of(10, 0);
        raid.signUp(userName, howManyPeople, arrivalTime);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));
    }
}
