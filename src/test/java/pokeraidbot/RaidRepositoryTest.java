package pokeraidbot;

import org.junit.Before;
import org.junit.Test;
import pokeraidbot.domain.Gyms;
import pokeraidbot.domain.Pokemons;
import pokeraidbot.domain.Raid;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidRepositoryTest {
    RaidRepository repo;

    @Before
    public void setUp() throws Exception {
        repo = new RaidRepository();
    }

    @Test
    public void testSignUp() throws Exception {
        LocalTime endOfRaid = LocalTime.of(11, 05);
        Raid enteiRaid = new Raid(Pokemons.ENTEI.getPokemon(), endOfRaid, Gyms.HÄSTEN);
        String raidCreatorName = "testUser1";
        repo.newRaid(raidCreatorName, enteiRaid);
        Raid raid = repo.getRaid(Gyms.HÄSTEN);
        assertThat(raid, is(enteiRaid));
        String userName = "testUser2";
        int howManyPeople = 3;
        LocalTime arrivalTime = LocalTime.of(10, 0);
        raid.signUp(userName, howManyPeople, arrivalTime);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));
    }
}
