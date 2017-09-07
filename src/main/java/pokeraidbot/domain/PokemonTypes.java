package pokeraidbot.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PokemonTypes {
    private Set<String> types;

    public PokemonTypes(String... types) {
        this.types = new HashSet<>(Arrays.asList(types));
    }

    public boolean isOfType(String type) {
        return types.contains(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonTypes)) return false;

        PokemonTypes that = (PokemonTypes) o;

        return types != null ? types.equals(that.types) : that.types == null;
    }

    @Override
    public int hashCode() {
        return types != null ? types.hashCode() : 0;
    }

    @Override
    public String toString() {
        return StringUtils.join(types, ", ");
    }
}
