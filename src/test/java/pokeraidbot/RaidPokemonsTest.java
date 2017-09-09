package pokeraidbot;

import org.junit.Test;
import pokeraidbot.domain.RaidBossPokemons;
import pokeraidbot.infrastructure.CounterTextFileParser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidPokemonsTest {
    @Test
    public void verifyAllRaidBossesInRepo() throws Exception {
        PokemonRepository repo = new PokemonRepository("/mons.json");
        for (RaidBossPokemons raidBoss : RaidBossPokemons.values()) {
            try {
                assertThat(repo.getByName(raidBoss.name()) != null, is(true));
                CounterTextFileParser parser = new CounterTextFileParser("/counters", raidBoss.name(), repo);
                assertThat(parser.getGoodCounters() != null, is(true));
                assertThat(parser.getBestCounters() != null, is(true));
            } catch (RuntimeException e) {
                System.err.println("Problem with pokemon " + raidBoss + ".");
                if (e == null || e.getMessage() == null) {
                    System.err.println("Could not read and parse counter file.");
                } else {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
