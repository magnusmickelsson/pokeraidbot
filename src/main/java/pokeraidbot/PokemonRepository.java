package pokeraidbot;

import org.codehaus.jackson.map.ObjectMapper;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.PokemonTypes;
import pokeraidbot.infrastructure.JsonPokemon;
import pokeraidbot.infrastructure.JsonPokemons;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PokemonRepository {
    private Map<String, Pokemon> pokemons = new HashMap<>();

    public PokemonRepository(String resourceName) {
        final InputStream inputStream = PokemonRepository.class.getResourceAsStream(resourceName);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonPokemons jsonPokemons = mapper.readValue(inputStream, JsonPokemons.class);
            for (JsonPokemon p : jsonPokemons.getPokemons()) {
                // todo: add more attributes to pokemon
                if (p != null && p.getName() != null && p.getTypes() != null) {
                    pokemons.put(p.getName().toUpperCase(), new Pokemon(p.getName(), new PokemonTypes(p.getTypes())));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Pokemon getByName(String name) {
        final Pokemon pokemon = pokemons.get(name.trim().toUpperCase());
        if (pokemon == null) {
            throw new RuntimeException("Could not find a pokemon with name " + name + ".");
        }
        return pokemon;
    }
}
