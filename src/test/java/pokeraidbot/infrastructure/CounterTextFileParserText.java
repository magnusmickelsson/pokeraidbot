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
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Entei",
                new PokemonRepository("/mons.json", localeService));
        assertThat(parser.getBestCounters().size(), is(3));
        assertThat(parser.getGoodCounters().size(), is(5));
    }

    @Test
    public void checkFlareonFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Flareon",
                new PokemonRepository("/mons.json", localeService));
        assertThat(parser.getBestCounters().size(), is(1));
        assertThat(parser.getGoodCounters().size(), is(9));
    }

    @Test
    public void checkMoltresFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Moltres",
                new PokemonRepository("/mons.json", localeService));
        assertThat(parser.getBestCounters().size(), is(2));
        assertThat(parser.getGoodCounters().size(), is(6));
    }

    @Test
    public void checkTyranitarFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Tyranitar",
                new PokemonRepository("/mons.json", localeService));
        assertThat(parser.getBestCounters().size(), is(2));
        assertThat(parser.getGoodCounters().size(), is(5));
    }
}
