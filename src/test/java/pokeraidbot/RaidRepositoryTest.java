package pokeraidbot;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.infrastructure.CSVGymDataReader;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidRepositoryTest {
    RaidRepository repo;
    GymRepository gymRepository;
    PokemonRepository pokemonRepository;

    @Before
    public void setUp() throws Exception {
        repo = new RaidRepository();
        gymRepository = new GymRepository(new CSVGymDataReader("/gyms_uppsala.csv").readAll());
        pokemonRepository = new PokemonRepository("/mons.json");
    }

    @Test
    public void testSignUp() throws Exception {
        LocalTime endOfRaid = LocalTime.now().plusHours(2);
        final Gym hästen = gymRepository.findByName("Hästen");
        Raid enteiRaid = new Raid(pokemonRepository.getByName("Entei"), endOfRaid, hästen);
        String raidCreatorName = "testUser1";
        repo.newRaid(raidCreatorName, enteiRaid);
        Raid raid = repo.getRaid(hästen);
        assertThat(raid, is(enteiRaid));
        String userName = "testUser2";
        int howManyPeople = 3;
        LocalTime arrivalTime = LocalTime.now().plusHours(1);
        raid.signUp(userName, howManyPeople, arrivalTime);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));
    }
}
