package pokeraidbot.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class PokemonTypes {
    private Set<String> types;

    public PokemonTypes(String... types) {
        this.types = new LinkedHashSet<>(Arrays.asList(types));
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

    public Set<String> getTypeSet() {
        return Collections.unmodifiableSet(types);
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
