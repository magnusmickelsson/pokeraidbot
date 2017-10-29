package pokeraidbot.domain.pokemon;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.JsonPokemon;
import pokeraidbot.infrastructure.JsonPokemons;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PokemonRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonRepository.class);

    private final LocaleService localeService;
    private Map<String, Pokemon> pokemons = new LinkedHashMap<>();

    public PokemonRepository(String resourceName, LocaleService localeService) {
        this.localeService = localeService;
        try {
            final InputStream inputStream = PokemonRepository.class.getResourceAsStream(resourceName);
            ObjectMapper mapper = new ObjectMapper();
            JsonPokemons jsonPokemons = mapper.readValue(inputStream, JsonPokemons.class);
            for (JsonPokemon p : jsonPokemons.getPokemons()) {
                if (p != null && p.getName() != null && p.getTypes() != null) {
                    final Pokemon pokemon = new Pokemon(p.getNumber(), p.getName(), p.getAbout(),
                            new PokemonTypes(p.getTypes()), p.getBuddyDistance(),
                            new HashSet<>(Arrays.asList(p.getWeaknesses())),
                            new HashSet<>(Arrays.asList(p.getResistant())));
                    pokemons.put(p.getName().toUpperCase(), pokemon);
                }
            }
            LOGGER.info("Parsed " + jsonPokemons.getPokemons().size() + " pokemons.");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Pokemon getByName(String name) {
        final Pokemon pokemon = getPokemon(name);
        if (pokemon == null) {
            throw new RuntimeException(localeService.getMessageFor(LocaleService.NO_POKEMON, LocaleService.DEFAULT, name));
        }
        return pokemon;
    }

    // This method is a getter that doesn't throw an exception if it doesn't find a pokemon, just returns null
    public Pokemon getPokemon(String name) {
        return pokemons.get(name.trim().toUpperCase());
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
