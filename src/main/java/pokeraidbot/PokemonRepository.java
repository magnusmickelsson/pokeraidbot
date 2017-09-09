package pokeraidbot;

import org.codehaus.jackson.map.ObjectMapper;
import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.PokemonTypes;
import pokeraidbot.infrastructure.JsonPokemon;
import pokeraidbot.infrastructure.JsonPokemons;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PokemonRepository {
    private final LocaleService localeService;
    private Map<String, Pokemon> pokemons = new HashMap<>();

    public PokemonRepository(String resourceName, LocaleService localeService) {
        this.localeService = localeService;
        final InputStream inputStream = PokemonRepository.class.getResourceAsStream(resourceName);
        ObjectMapper mapper = new ObjectMapper();
        try {
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
        throw new RuntimeException(localeService.getMessageFor(LocaleService.NO_POKEMON, LocaleService.DEFAULT, "" + pokemonNumber));
    }
}
