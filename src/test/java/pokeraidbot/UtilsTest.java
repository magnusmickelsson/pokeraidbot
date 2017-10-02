package pokeraidbot;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UtilsTest {
    private static Set<String> allTypes = new HashSet<>(Arrays.asList(
            "Normal",
            "Fire",
            "Water",
            "Electric",
            "Grass",
            "Ice",
            "Fighting",
            "Poison",
            "Ground",
            "Flying",
            "Psychic",
            "Bug",
            "Rock",
            "Ghost",
            "Dragon",
            "Dark",
            "Steel",
            "Fairy"
    ));

    @Test
    public void testNormalWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Normal", Arrays.asList("Fighting"));
    }

    @Test
    public void testFireWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Fire", Arrays.asList("Water", "Ground", "Rock"));
    }

    @Test
    public void testWaterWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Water", Arrays.asList("Electric", "Grass"));
    }

    @Test
    public void testElectricWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Electric", Arrays.asList("Ground"));
    }

    @Test
    public void testGrassWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Grass", Arrays.asList("Fire", "Ice", "Poison", "Flying", "Bug"));
    }

    @Test
    public void testIceWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Ice", Arrays.asList("Fire", "Fighting", "Rock", "Steel"));
    }

    @Test
    public void testFightingWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Fighting", Arrays.asList("Flying", "Psychic", "Fairy"));
    }

    @Test
    public void testPoisonWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Poison", Arrays.asList("Ground", "Psychic"));
    }

    @Test
    public void testGroundWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Ground", Arrays.asList("Water", "Grass", "Ice"));
    }

    @Test
    public void testFlyingWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Flying", Arrays.asList("Electric", "Ice", "Rock"));
    }

    @Test
    public void testPsychicWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Psychic", Arrays.asList("Bug", "Ghost", "Dark"));
    }

    @Test
    public void testBugWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Bug", Arrays.asList("Fire", "Flying", "Rock"));
    }

    @Test
    public void testRockWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Rock", Arrays.asList("Ground", "Water", "Grass", "Fighting", "Steel"));
    }

    @Test
    public void testGhostWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Ghost", Arrays.asList("Dark", "Ghost"));
    }

    @Test
    public void testDragonWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Dragon", Arrays.asList("Ice", "Dragon", "Fairy"));
    }

    @Test
    public void testDarkWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Dark", Arrays.asList("Fighting", "Bug", "Fairy"));
    }

    @Test
    public void testSteelWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Steel", Arrays.asList("Fire", "Fighting", "Ground"));
    }

    @Test
    public void testFairyWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Fairy", Arrays.asList("Poison", "Steel"));
    }

    @Test
    public void testDoubleWeaknesses() throws Exception {
        PokemonRepository pokemonRepository = new PokemonRepository("/mons.json", new LocaleService());
        Pokemon pokemon;
        String typeToCheck;
        pokemon = pokemonRepository.getByName("Tyranitar");
        typeToCheck = "Fighting";
        assertPokemonIsDoubleWeakAgainst(pokemon, typeToCheck);

        pokemon = pokemonRepository.getByName("Articuno");
        typeToCheck = "Rock";
        assertPokemonIsDoubleWeakAgainst(pokemon, typeToCheck);
    }

    @Test
    public void testPrintWeaknesses() throws Exception {
        PokemonRepository pokemonRepository = new PokemonRepository("/mons.json", new LocaleService());
        Pokemon pokemon = pokemonRepository.getByName("Tyranitar");

        assertThat(Utils.printWeaknesses(pokemon), is("Water, **Fighting**, Bug, Ground, Steel, Fairy"));
    }

    @Test
    public void raidsCollide() throws Exception {
        final ClockService currentTimeService = new ClockService();
        currentTimeService.setMockTime(LocalTime.of(10, 0));
        LocalDateTime startOne = currentTimeService.getCurrentDateTime();
        LocalDateTime endOne = startOne.plusHours(1);
        LocalDateTime startTwo = currentTimeService.getCurrentDateTime().minusMinutes(1);
        LocalDateTime endTwo = startTwo.plusHours(1);
        assertThat(Utils.raidsCollide(endOne, endTwo), is(true));
        assertThat(Utils.raidsCollide(endTwo, endOne), is(true));

        startOne = currentTimeService.getCurrentDateTime();
        endOne = startOne.plusMinutes(10);
        startTwo = currentTimeService.getCurrentDateTime().plusMinutes(11);
        endTwo = startTwo.plusHours(1);
        assertThat(Utils.raidsCollide(endOne, endTwo), is(false));
        assertThat(Utils.raidsCollide(endTwo, endOne), is(false));
    }

    @Test
    public void isSamePokemon() throws Exception {
        assertThat(Utils.isSamePokemon("mew", "mewtwo"), is(false));
        assertThat(Utils.isSamePokemon("mewtwo", "mew"), is(false));
        assertThat(Utils.isSamePokemon("mewtwo", "mewtwo"), is(true));
        assertThat(Utils.isSamePokemon("mewTwO", "mewtwo"), is(true));
        assertThat(Utils.isSamePokemon("mewtwo", "mewTwO"), is(true));
        assertThat(Utils.isSamePokemon(" mewtwo", "mewtwo"), is(false));
    }

    private void assertPokemonIsDoubleWeakAgainst(Pokemon pokemon, String typeToCheck) {
        final Set<String> typeSet = pokemon.getTypes().getTypeSet();
        final Iterator<String> iterator = typeSet.iterator();
        assertThat(Utils.isTypeDoubleStrongVsPokemonWithTwoTypes(iterator.next(), iterator.next(), typeToCheck), is(true));
    }

    private void assertTypeIsWeakAgainstTypesAndOthersAreNot(String pokemonType, List<String> typesItIsWeakAgainst) {
        for (String strongType : typesItIsWeakAgainst) {
            assertThat(strongType + " should be strong vs " + pokemonType, Utils.typeIsStrongVsPokemon(pokemonType, strongType), is(true));
        }

        final Collection<String> leftToCheckThatShouldBeFalse = CollectionUtils.subtract(allTypes, typesItIsWeakAgainst);

        for (String leftToCheck : leftToCheckThatShouldBeFalse) {
            assertThat(leftToCheck + " should not be strong vs " + pokemonType, Utils.typeIsStrongVsPokemon(pokemonType, leftToCheck), is(false));
        }
    }
}
