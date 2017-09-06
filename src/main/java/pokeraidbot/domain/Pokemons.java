package pokeraidbot.domain;

public enum Pokemons {
    ENTEI(new Pokemon(PokemonName.ENTEI, new PokemonTypes(PokemonType.FIRE))),
    TYRANITAR(new Pokemon(PokemonName.TYRANITAR, new PokemonTypes(PokemonType.DARK, PokemonType.ROCK)));

    private Pokemon pokemon;

    Pokemons(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }
}
