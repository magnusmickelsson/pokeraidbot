package pokeraidbot;

import org.junit.Test;
import pokeraidbot.domain.RaidBossPokemons;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidPokemonsTest {
    @Test
    public void verifyAllRaidBossesInRepo() throws Exception {
        PokemonRepository repo = new PokemonRepository("/mons.json");
        for (RaidBossPokemons raidBoss : RaidBossPokemons.values()) {
            try {
                assertThat(repo.getByName(raidBoss.name()) != null, is(true));
            } catch (RuntimeException e) {
                System.err.println("Problem with pokemon " + raidBoss + ": " + e.getMessage());
            }
        }
    }
}
