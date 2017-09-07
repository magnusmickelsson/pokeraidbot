package pokeraidbot.domain;

public enum Pokemons {
    ENTEI(new Pokemon("Entei", new PokemonTypes("Fire"))),
    TYRANITAR(new Pokemon("Tyranitar", new PokemonTypes("Dark", "Rock")));

    private Pokemon pokemon;

    Pokemons(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }
}
