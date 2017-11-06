package pokeraidbot.infrastructure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CounterTextFileParserText {

    private LocaleService localeService;

    @Before
    public void setUp() throws Exception {
        UserConfigRepository userConfigRepository = Mockito.mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        localeService = new LocaleService("sv", userConfigRepository);
    }

    @Test
    public void checkEnteiFile() throws Exception {
        assertPokemonHasCorrectNumberOfCounters("Entei", 5, 3);
    }

    @Test
    public void checkFlareonFile() throws Exception {
        assertPokemonHasCorrectNumberOfCounters("Flareon", 9, 1);
    }

    @Test
    public void checkMoltresFile() throws Exception {
        assertPokemonHasCorrectNumberOfCounters("Moltres", 6, 2);
    }

    @Test
    public void checkTyranitarFile() throws Exception {
        assertPokemonHasCorrectNumberOfCounters("Tyranitar", 5, 2);
    }

    @Test
    public void checkPoliwrathFile() throws Exception {
        assertPokemonHasCorrectNumberOfCounters("Poliwrath", 3, 1);
    }

    @Test
    public void checkPorygonFile() throws Exception {
        assertPokemonHasCorrectNumberOfCounters("Porygon", 6, 2);
    }

    @Test
    public void checkRemainingFiles() throws Exception {
        assertPokemonHasCorrectNumberOfCounters("Golem", 2, 2);
        assertPokemonHasCorrectNumberOfCounters("Nidoking", 3, 1);
        assertPokemonHasCorrectNumberOfCounters("Nidoqueen", 3, 1);
        assertPokemonHasCorrectNumberOfCounters("Ninetales", 6, 1);
        assertPokemonHasCorrectNumberOfCounters("Omastar", 4, 1);
        assertPokemonHasCorrectNumberOfCounters("Scyther", 3, 1);
        assertPokemonHasCorrectNumberOfCounters("Victreebel", 3, 1);
    }

    private void assertPokemonHasCorrectNumberOfCounters(String pokemonName, int goodCounters, int supremeCounters) {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", pokemonName,
                new PokemonRepository("/mons.json", localeService));
        assertThat(parser.getBestCounters().size(), is(supremeCounters));
        assertThat(parser.getGoodCounters().size(), is(goodCounters));
    }
}
