package pokeraidbot.domain.raid;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRaidInfo;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.infrastructure.CounterTextFileParser;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class RaidPokemonsTest {
    private LocaleService localeService;
    private PokemonRepository pokemonRepository;
    private PokemonRaidStrategyService strategyService;

    @Before
    public void setUp() throws Exception {
        UserConfigRepository userConfigRepository = Mockito.mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        localeService = new LocaleService("sv", userConfigRepository);
        pokemonRepository = new PokemonRepository("/pokemons.csv", localeService);
        strategyService = new PokemonRaidStrategyService(pokemonRepository);
    }

    @Test
    public void verifyAllRaidBossesInRepo() throws Exception {
        for (RaidBossPokemons raidBoss : RaidBossPokemons.values()) {
            try {
                assertThat(pokemonRepository.search(raidBoss.name(), null) != null, is(true));
                CounterTextFileParser parser = new CounterTextFileParser("/counters", raidBoss.name(), pokemonRepository);
                assertThat(parser.getGoodCounters() != null, is(true));
                assertThat(parser.getBestCounters() != null, is(true));
            } catch (RuntimeException e) {
                System.err.println("Problem with pokemon " + raidBoss + ".");
                if (e == null || e.getMessage() == null) {
                    System.err.println("Could not read and parse counter file: " + e);
                } else {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    @Test
    public void verifyTyranitarBestCounterIsMachamp() throws Exception {
        final RaidBossCounters raidBossCounters = strategyService.getCounters(pokemonRepository.search("Tyranitar", null));
        final String tyranitarBestCounter = raidBossCounters
                .getSupremeCounters().iterator().next().getCounterPokemonName();
        assertThat(tyranitarBestCounter, is("Machamp"));
    }

    @Test
    public void verifyAllPokemonsInPokemonGoInRepo() throws Exception {
        Set<Integer> numbers = new HashSet<>();
        try {
            for (int n = 1; n < 387; n++) {
                numbers.add(n);
            }
            for (Pokemon pokemon : pokemonRepository.getAll()) {
                numbers.remove(pokemon.getNumber());
            }
            assertThat("" + numbers, numbers.size(), is(0));
        } catch (Throwable e) {
            for (Integer pokemonNumber : numbers) {
                System.out.println(pokemonRepository.getByNumber(pokemonNumber));
            }
            throw e;
        }
    }

    @Test
    public void verifyAllLegendaryPokemonsAreTier5() {
        final PokemonRaidInfo raikou = strategyService.getRaidInfo(pokemonRepository.search("raikou", null));
        assertNotNull(raikou);
        assertThat(raikou.getBossTier(), is(5));
        final PokemonRaidInfo hooh = strategyService.getRaidInfo(pokemonRepository.search("ho-oh", null));
        assertNotNull(hooh);
        assertThat(hooh.getBossTier(), is(5));
    }

    @Test
    public void verifyRaidbosses() {
        PokemonRaidInfo raidInfo = strategyService.getRaidInfo(pokemonRepository.search("machamp", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("wailmer", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(1));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("kyogre", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("latias", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("latios", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("rayquaza", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("registeel", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
    }
}
