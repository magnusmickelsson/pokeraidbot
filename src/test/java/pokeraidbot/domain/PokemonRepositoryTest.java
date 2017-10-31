package pokeraidbot.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.pokemon.PokemonTypes;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PokemonRepositoryTest {
    private LocaleService localeService;
    private PokemonRepository pokemonRepository;

    @Before
    public void setUp() throws Exception {
        UserConfigRepository userConfigRepository = Mockito.mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        localeService = new LocaleService("sv", userConfigRepository);
        pokemonRepository = new PokemonRepository("/mons.json", localeService);
    }

    @Test
    public void testGetTyranitarWithFuzzySearch() throws Exception {
        Pokemon pokemon = pokemonRepository.search("Tyranitar", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Dark", "Rock")));
        Pokemon search = pokemonRepository.search("Tryantar", null);
        assertThat(search, is(pokemon));
    }

    @Test
    public void testGetRaikouWithFuzzySearch() throws Exception {
        Pokemon pokemon = pokemonRepository.search("Raikou", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Electric")));
        Pokemon search = pokemonRepository.search("Riakuo", null);
        assertThat(search, is(pokemon));
    }

    @Test
    public void testGetRaikouWithFuzzySearchFirstChars() throws Exception {
        Pokemon pokemon = pokemonRepository.search("Raikou", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Electric")));
        Pokemon search = pokemonRepository.search("Raik", null);
        assertThat(search, is(pokemon));
    }
}
