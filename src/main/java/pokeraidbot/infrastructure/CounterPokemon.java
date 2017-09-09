package pokeraidbot.infrastructure;

import java.util.Set;

public class CounterPokemon {
    private final String counterPokemonName;
    private final Set<String> moves;

    public CounterPokemon(String counterPokemonName, Set<String> moves) {
        this.counterPokemonName = counterPokemonName;
        this.moves = moves;
    }

    public String getCounterPokemonName() {
        return counterPokemonName;
    }

    public Set<String> getMoves() {
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CounterPokemon)) return false;

        CounterPokemon that = (CounterPokemon) o;

        if (counterPokemonName != null ? !counterPokemonName.equals(that.counterPokemonName) : that.counterPokemonName != null)
            return false;
        return moves != null ? moves.equals(that.moves) : that.moves == null;
    }

    @Override
    public int hashCode() {
        int result = counterPokemonName != null ? counterPokemonName.hashCode() : 0;
        result = 31 * result + (moves != null ? moves.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return counterPokemonName + " " + moves;
    }
}
