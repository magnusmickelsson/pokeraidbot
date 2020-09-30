package pokeraidbot;

import net.dv8tion.jda.api.entities.User;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.pokemon.PokemonTypes;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTest {
    private static UserConfigRepository userConfigRepository;
    private static LocaleService localeService;
    private PokemonRepository pokemonRepository;

    @BeforeClass
    public static void before() {
        userConfigRepository = mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        localeService = new LocaleService("sv", userConfigRepository);
    }

    @Before
    public void setUp() throws Exception {
        pokemonRepository = new PokemonRepository("/pokemons.csv",
                new LocaleService("sv", userConfigRepository));
    }

    @Test
    public void testNormalWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Normal", Arrays.asList("Fighting"));
    }

    @Test
    public void testFireWeaknesses() throws Exception {
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Fire", Arrays.asList("Water", "Ground", "Rock"));
    }

    @Test
    public void testGetFireWeaknesses() throws Exception {
        assertThat(Utils.getWeaknessesFor(new PokemonTypes("Fire")),
                is(new HashSet<>(Arrays.asList("Water", "Ground", "Rock"))));
    }

    @Test
    public void testGetHoOhWeaknesses() throws Exception {
        assertThat(Utils.getWeaknessesFor(new PokemonTypes("Fire", "Flying")),
                is(new HashSet<>(Arrays.asList("Water", "Rock", "Electric"))));
    }

    @Test
    public void testGetKingdraWeaknesses() throws Exception {
        assertThat(Utils.getWeaknessesFor(new PokemonTypes("Water", "Dragon")),
                is(new HashSet<>(Arrays.asList("Fairy", "Dragon"))));
    }

    @Test
    public void testGetGyaradosWeaknesses() throws Exception {
        assertThat(Utils.getWeaknessesFor(new PokemonTypes("Water", "Flying")),
                is(new HashSet<>(Arrays.asList("Electric", "Rock"))));
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
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Grass",
                Arrays.asList("Fire", "Ice", "Poison", "Flying", "Bug"));
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
        assertTypeIsWeakAgainstTypesAndOthersAreNot("Rock",
                Arrays.asList("Ground", "Water", "Grass", "Fighting", "Steel"));
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
        Pokemon pokemon;
        String typeToCheck;
        pokemon = pokemonRepository.search("Tyranitar", null);
        typeToCheck = "Fighting";
        assertPokemonIsDoubleWeakAgainst(pokemon, typeToCheck);

        pokemon = pokemonRepository.search("Articuno", null);
        typeToCheck = "Rock";
        assertPokemonIsDoubleWeakAgainst(pokemon, typeToCheck);
    }

    @Test
    public void testPrintWeaknesses() throws Exception {
        PokemonRepository pokemonRepository = new PokemonRepository("/pokemons.csv",
                new LocaleService("sv", userConfigRepository));
        Pokemon pokemon = pokemonRepository.search("Tyranitar", null);

        assertThat(Utils.printWeaknesses(pokemon), is("Water, **Fighting**, Ground, Grass, Steel, Bug, Fairy"));
    }

    @Test
    public void raidsCollide() throws Exception {
        final ClockService currentTimeService = new ClockService();
        currentTimeService.setMockTime(LocalTime.of(10, 0));
        LocalDateTime startOne = currentTimeService.getCurrentDateTime();
        LocalDateTime endOne = startOne.plusHours(1);
        LocalDateTime startTwo = currentTimeService.getCurrentDateTime().minusMinutes(1);
        LocalDateTime endTwo = startTwo.plusMinutes(Utils.RAID_DURATION_IN_MINUTES);
        assertThat(Utils.raidsCollide(endOne, false, endTwo, false), is(true));
        assertThat(Utils.raidsCollide(endTwo, false, endOne, false), is(true));

        startOne = currentTimeService.getCurrentDateTime();
        endOne = startOne.plusMinutes(10);
        startTwo = currentTimeService.getCurrentDateTime().plusMinutes(11);
        endTwo = startTwo.plusMinutes(Utils.RAID_DURATION_IN_MINUTES);
        assertThat(Utils.raidsCollide(endOne, false, endTwo, false), is(false));
        assertThat(Utils.raidsCollide(endTwo, false, endOne, false), is(false));
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

    @Test
    public void parseTime() throws Exception {
        User user = mock(User.class);
        when(user.getName()).thenReturn("TheUser");
        assertThat(Utils.parseTime(user, "925", localeService), is(LocalTime.of(9, 25)));
        assertThat(Utils.parseTime(user, "9.25", localeService), is(LocalTime.of(9, 25)));
        assertThat(Utils.parseTime(user, "9:25", localeService), is(LocalTime.of(9, 25)));
        assertThat(Utils.parseTime(user, "025", localeService), is(LocalTime.of(0, 25)));
        assertThat(Utils.parseTime(user, "1925", localeService), is(LocalTime.of(19, 25)));
        assertErrorWhenParsing("12345", user);
        assertErrorWhenParsing("1A45", user);
        assertErrorWhenParsing("10,45", user);
        assertErrorWhenParsing("2545", user);
        assertErrorWhenParsing("999", user);
        assertErrorWhenParsing("969", user);
    }

    @Test
    public void timeInRaidspan() throws Exception {
        User user = mock(User.class);
        when(user.getName()).thenReturn("User");
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime same = localDateTime;
        LocalDateTime before = localDateTime.minusMinutes(1);
        LocalDateTime after = localDateTime.plusMinutes(1);
        LocalDateTime end = localDateTime.plusMinutes(Utils.RAID_DURATION_IN_MINUTES);
        LocalDateTime sameAsEnd = end;
        LocalDateTime beforeEnd = end.minusMinutes(1);
        LocalDateTime afterEnd = end.plusMinutes(1);
        final LocaleService localeService = mock(LocaleService.class);
        when(localeService.getMessageFor(any(), any(), any())).thenReturn("Mupp");
        Raid raid = new Raid(pokemonRepository.getByName("Tyranitar"), end,
                new Gym("Test", "id", "10", "10", null),
                localeService, "Test", false);
        checkWhetherAssertFails(user, same, localeService, raid, false);
        checkWhetherAssertFails(user, after, localeService, raid, false);
        checkWhetherAssertFails(user, before, localeService, raid, true);
        checkWhetherAssertFails(user, sameAsEnd, localeService, raid, false);
        checkWhetherAssertFails(user, afterEnd, localeService, raid, true);
        checkWhetherAssertFails(user, beforeEnd, localeService, raid, false);
    }

    protected void checkWhetherAssertFails(User user, LocalDateTime when, LocaleService localeService,
                                           Raid raid, boolean shouldFail) {
        try {
            Utils.assertTimeInRaidTimespan(user, when, raid, localeService);
            if (shouldFail) {
                fail("Should give exception!");
            }
        } catch (Throwable t) {
            if (!shouldFail) {
                fail("Should not give exception!");
            }
        }
    }

    private void assertErrorWhenParsing(String timeString, User user) {
        try {
            Utils.parseTime(user, timeString, localeService);
            fail("This value should have failed: " + timeString);
        } catch (UserMessedUpException e) {
            // Expected
        }
    }

    private void assertPokemonIsDoubleWeakAgainst(Pokemon pokemon, String typeToCheck) {
        final Set<String> typeSet = pokemon.getTypes().getTypeSet();
        final Iterator<String> iterator = typeSet.iterator();
        assertThat(Utils.isTypeDoubleStrongVsPokemonWithTwoTypes(iterator.next(), iterator.next(), typeToCheck),
                is(true));
    }

    private void assertTypeIsWeakAgainstTypesAndOthersAreNot(String pokemonType, List<String> typesItIsWeakAgainst) {
        for (String strongType : typesItIsWeakAgainst) {
            assertThat(strongType + " should be strong vs " + pokemonType,
                    Utils.typeIsStrongVsPokemon(pokemonType, strongType), is(true));
        }

        final Collection<String> leftToCheckThatShouldBeFalse = CollectionUtils.subtract(PokemonTypes.allTypes, typesItIsWeakAgainst);

        for (String leftToCheck : leftToCheckThatShouldBeFalse) {
            assertThat(leftToCheck + " should not be strong vs " + pokemonType,
                    Utils.typeIsStrongVsPokemon(pokemonType, leftToCheck), is(false));
        }
    }
}
