package pokeraidbot.infrastructure;

import org.junit.Test;
import pokeraidbot.PokemonRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CounterTextFileParserText {
    @Test
    public void checkEnteiFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Entei", new PokemonRepository("/mons.json"));
        assertThat(parser.getBestCounters().size() > 0, is(true));
        assertThat(parser.getGoodCounters().size() > 0, is(true));
    }

    @Test
    public void checkFlareonFile() throws Exception {
        CounterTextFileParser parser = new CounterTextFileParser("/counters", "Flareon", new PokemonRepository("/mons.json"));
        assertThat(parser.getBestCounters().size() == 0, is(true));
        assertThat(parser.getGoodCounters().size() > 0, is(true));
    }
}
