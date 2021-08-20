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
        pokemonRepository = new PokemonRepository("/pokemons.csv", localeService);
    }

    @Test
    public void testGetGen4Mons() throws Exception {
        Pokemon pokemon;
        pokemon = pokemonRepository.search("Palkia", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Water", "Dragon")));

        pokemon = pokemonRepository.search("Arceus", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Normal")));
    }

    @Test
    public void testGetGen7Mons() throws Exception {
        Pokemon pokemon;
        pokemon = pokemonRepository.search("Melmetal", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Steel")));

        pokemon = pokemonRepository.search("Fletchling", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Normal", "Flying")));
    }

    @Test
    public void testGetGen8Mons() throws Exception {
        Pokemon pokemon;
        pokemon = pokemonRepository.search("Zacian", null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes("Fairy")));
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
        assertThat(pokemon.isEgg(), is(false));
    }

    @Test
    public void testGetEggs() throws Exception {
        final String eggName = "Egg";
        final String eggSearchName = "eGg";
        assertEggExistsForTier(eggName, eggSearchName, 1);
        assertEggExistsForTier(eggName, eggSearchName, 2);
        assertEggExistsForTier(eggName, eggSearchName, 3);
        assertEggExistsForTier(eggName, eggSearchName, 4);
        assertEggExistsForTier(eggName, eggSearchName, 5);
        assertEggExistsForTier(eggName, eggSearchName, 6);
    }

    private void assertEggExistsForTier(String eggName, String eggSearchName, int eggTier) {
        Pokemon pokemon = pokemonRepository.getByName(eggName + eggTier);
        assertThat(pokemon != null, is(true));
        pokemon = pokemonRepository.search(eggName + eggTier, null);
        assertThat(pokemon != null, is(true));
        assertThat(pokemon.getTypes(), is(new PokemonTypes()));
        Pokemon search = pokemonRepository.search(eggSearchName + eggTier, null);
        assertThat(search, is(pokemon));
        assertThat(pokemon.isEgg(), is(true));
    }
}
