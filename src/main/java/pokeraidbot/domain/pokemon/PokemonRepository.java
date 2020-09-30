package pokeraidbot.domain.pokemon;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.CSVPokemonDataReader;

import java.util.*;
import java.util.stream.Collectors;

public class PokemonRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonRepository.class);

    private final LocaleService localeService;
    public static final String EGG_1 = "Egg1";
    public static final String EGG_2 = "Egg2";
    public static final String EGG_3 = "Egg3";
    public static final String EGG_4 = "Egg4";
    public static final String EGG_5 = "Egg5";
    public static final String EGG_6 = "Egg6";
    private Map<String, Pokemon> pokemons = new LinkedHashMap<>();

    public PokemonRepository(String resourceName, LocaleService localeService) {
        this.localeService = localeService;
        try {
            CSVPokemonDataReader csvPokemonDataReader = new CSVPokemonDataReader(resourceName);
            final Set<Pokemon> pokemonsRead = csvPokemonDataReader.readAll();
            for (Pokemon p : pokemonsRead) {
                pokemons.put(p.getName().toUpperCase(), p);
            }
            // So we can handle eggs before they're hatched
            this.pokemons.put("EGG1", new Pokemon(99999, EGG_1, "Tier 1 egg", PokemonTypes.NONE,
                    "", new HashSet<>(),
                    new HashSet<>()));
            this.pokemons.put("EGG2", new Pokemon(99998, EGG_2, "Tier 2 egg", PokemonTypes.NONE,
                    "", new HashSet<>(),
                    new HashSet<>()));
            this.pokemons.put("EGG3", new Pokemon(99997, EGG_3, "Tier 3 egg", PokemonTypes.NONE,
                    "", new HashSet<>(),
                    new HashSet<>()));
            this.pokemons.put("EGG4", new Pokemon(99996, EGG_4, "Tier 4 egg", PokemonTypes.NONE,
                    "", new HashSet<>(),
                    new HashSet<>()));
            this.pokemons.put("EGG5", new Pokemon(99995, EGG_5, "Tier 5 egg", PokemonTypes.NONE,
                    "", new HashSet<>(),
                    new HashSet<>()));
            this.pokemons.put("EGG6", new Pokemon(99994, EGG_6, "MEGA raid", PokemonTypes.NONE,
                    "", new HashSet<>(),
                    new HashSet<>()));
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Pokemon search(String name, User user) {
        final Pokemon pokemon = fuzzySearch(name);
        if (pokemon == null) {
            final Locale locale = user == null ? LocaleService.DEFAULT : localeService.getLocaleForUser(user);
            throw new RuntimeException(localeService.getMessageFor(LocaleService.NO_POKEMON, locale, name));
        }
        return pokemon;
    }

    public Pokemon getByName(String name) {
        if (name == null) {
            return null;
        }
        final String nameToGet = name.trim().toUpperCase();
        return pokemons.get(nameToGet);
    }

    // This method is a getter with fuzzy search that doesn't throw an exception if it doesn't find a pokemon, just returns null
    private Pokemon fuzzySearch(String name) {
        final Collection<Pokemon> allPokemons = pokemons.values();

        final String nameToSearchFor = name.trim().toUpperCase();
        final Optional<Pokemon> pokemon = Optional.ofNullable(pokemons.get(nameToSearchFor));
        if (pokemon.isPresent()) {
            return pokemon.get();
        } else {
            List<ExtractedResult> candidates = FuzzySearch.extractTop(nameToSearchFor,
                    allPokemons.stream().map(p -> p.getName().toUpperCase()).collect(Collectors.toList()), 5, 50);
            if (candidates.size() == 1) {
                return pokemons.get(candidates.iterator().next().getString());
            } else if (candidates.size() < 1) {
                return null;
            } else {
                int score = 0;
                String highestScoreResultName = null;
                for (ExtractedResult result : candidates) {
                    if (result.getScore() > score) {
                        score = result.getScore();
                        highestScoreResultName = result.getString();
                    }
                }
                if (highestScoreResultName != null) {
                    return pokemons.get(highestScoreResultName);
                } else {
                    return null;
                }
            }
        }
    }

    public Set<Pokemon> getAll() {
        return Collections.unmodifiableSet(new HashSet<>(pokemons.values()));
    }

    public Pokemon getByNumber(Integer pokemonNumber) {
        for (Pokemon p : getAll()) {
            if (Objects.equals(p.getNumber(), pokemonNumber)) {
                return p;
            }
        }
        throw new RuntimeException(localeService.getMessageFor(LocaleService.NO_POKEMON, LocaleService.DEFAULT, "" +
                pokemonNumber));
    }
}
