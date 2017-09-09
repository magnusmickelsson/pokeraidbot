package pokeraidbot.domain;

import org.junit.Test;
import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.PokemonRepository;
import pokeraidbot.domain.RaidBossPokemons;
import pokeraidbot.infrastructure.CounterTextFileParser;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidPokemonsTest {
    @Test
    public void verifyAllRaidBossesInRepo() throws Exception {
        PokemonRepository repo = new PokemonRepository("/mons.json", new LocaleService());
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

    @Test
    public void verifyAllPokemonsInPokemonGoInRepo() throws Exception {
        Set<Integer> numbers = new HashSet<>();
        PokemonRepository repo = new PokemonRepository("/mons.json", new LocaleService());
        try {
            for (int n = 1; n < 252; n++) {
                numbers.add(n);
            }
            for (Pokemon pokemon : repo.getAll()) {
                numbers.remove(pokemon.getNumber());
            }
            assertThat("" + numbers, numbers.size(), is(0));
        } catch (Throwable e) {
            for (Integer pokemonNumber : numbers) {
                System.out.println(repo.getByNumber(pokemonNumber));
            }
            throw e;
        }
    }
}
