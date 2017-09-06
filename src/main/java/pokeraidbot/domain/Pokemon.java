package pokeraidbot.domain;

public class Pokemon {

    private PokemonName name;
    private PokemonTypes types;

    public Pokemon(PokemonName name, PokemonTypes types) {
        this.name = name;
        this.types = types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pokemon)) return false;

        Pokemon pokemon = (Pokemon) o;

        if (name != pokemon.name) return false;
        return types != null ? types.equals(pokemon.types) : pokemon.types == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pokemon{" + name + ", types=" + types + '}';
    }

    public PokemonName getName() {
        return name;
    }

    // todo: getWeaknesses(Pokemon)
    // todo: hasWeaknessFor(Type)
}
