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
        RaidBossCounters counters;
        PokemonRaidInfo raidInfo = strategyService.getRaidInfo(pokemonRepository.search("machamp", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(3));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(7));
        assertThat(counters.getSupremeCounters().size(), is(2));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("wailmer", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(1));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters == null, is(true));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("kyogre", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(4));
        assertThat(counters.getSupremeCounters().size(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("latias", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(0));
        assertThat(counters.getSupremeCounters().size(), is(5));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("latios", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(0));
        assertThat(counters.getSupremeCounters().size(), is(5));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("rayquaza", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(3));
        assertThat(counters.getSupremeCounters().size(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("registeel", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(3));
        assertThat(counters.getSupremeCounters().size(), is(4));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("aggron", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(4));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(4));
        assertThat(counters.getSupremeCounters().size(), is(1));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("celebi", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getMaxCp(), is(1766));
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(6));
        assertThat(counters.getSupremeCounters().size(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("mew", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        assertThat(raidInfo.getMaxCp(), is(1766));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(7));
        assertThat(counters.getSupremeCounters().size(), is(1));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("snorunt", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(1));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("swablu", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(1));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("dewgong", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(2));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("azumarill", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("jynx", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("piloswine", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("feraligatr", null));
        assertNotNull(raidInfo);
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(raidInfo.getBossTier(), is(4));
        assertThat(counters.getGoodCounters().size(), is(4));
        assertThat(counters.getSupremeCounters().size(), is(3));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("giratina", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(3));
        assertThat(counters.getSupremeCounters().size(), is(1));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("palkia", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(2));
        assertThat(counters.getSupremeCounters().size(), is(1));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("dialga", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(4));
        assertThat(counters.getSupremeCounters().size(), is(2));

        raidInfo = strategyService.getRaidInfo(pokemonRepository.search("cresselia", null));
        assertNotNull(raidInfo);
        assertThat(raidInfo.getBossTier(), is(5));
        counters = strategyService.getCounters(raidInfo.getPokemon());
        assertThat(counters.getGoodCounters().size(), is(2));
        assertThat(counters.getSupremeCounters().size(), is(2));
    }
}
