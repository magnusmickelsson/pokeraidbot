package pokeraidbot.domain;

public class PokemonTypes {
    private PokemonType type1;
    private PokemonType type2;

    public PokemonTypes(PokemonType type) {
        this.type1 = type;
        this.type2 = PokemonType.NONE;
    }

    public PokemonTypes(PokemonType type1, PokemonType type2) {
        this.type1 = type1;
        this.type2 = type2;
    }

    public boolean isOfType(PokemonType type) {
        if (type == PokemonType.NONE) {
            throw new IllegalArgumentException("What are you trying to do anyway?");
        }
        return type == type1 || type == type2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonTypes)) return false;

        PokemonTypes that = (PokemonTypes) o;

        if (type1 != that.type1) return false;
        return type2 == that.type2;
    }

    @Override
    public int hashCode() {
        int result = type1 != null ? type1.hashCode() : 0;
        result = 31 * result + (type2 != null ? type2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (type2 == PokemonType.NONE) {
            return type1.name();
        } else {
            return type1 + ", " + type2;
        }
    }
}
