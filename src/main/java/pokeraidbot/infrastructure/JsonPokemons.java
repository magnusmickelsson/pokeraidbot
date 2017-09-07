package pokeraidbot.infrastructure;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Set;

public class JsonPokemons {
    private Set<JsonPokemon> pokemons;

    @JsonCreator
    public JsonPokemons(Set<JsonPokemon> pokemons) {
        this.pokemons = pokemons;
    }

    @JsonProperty
    public Set<JsonPokemon> getPokemons() {
        return pokemons;
    }
}
