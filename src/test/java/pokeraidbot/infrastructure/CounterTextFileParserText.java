package pokeraidbot.infrastructure;

import org.junit.Test;
import pokeraidbot.domain.PokemonRepository;
import pokeraidbot.domain.LocaleService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CounterTextFileParserText {
    @Test
    public void checkEnteiFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Entei",
                new PokemonRepository("/mons.json", new LocaleService()));
        assertThat(parser.getBestCounters().size(), is(3));
        assertThat(parser.getGoodCounters().size(), is(5));
    }

    @Test
    public void checkFlareonFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Flareon",
                new PokemonRepository("/mons.json", new LocaleService()));
        assertThat(parser.getBestCounters().size(), is(1));
        assertThat(parser.getGoodCounters().size(), is(9));
    }

    @Test
    public void checkMoltresFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Moltres",
                new PokemonRepository("/mons.json", new LocaleService()));
        assertThat(parser.getBestCounters().size(), is(2));
        assertThat(parser.getGoodCounters().size(), is(6));
    }

    @Test
    public void checkTyranitarFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Tyranitar",
                new PokemonRepository("/mons.json", new LocaleService()));
        assertThat(parser.getBestCounters().size(), is(2));
        assertThat(parser.getGoodCounters().size(), is(5));
    }
}
