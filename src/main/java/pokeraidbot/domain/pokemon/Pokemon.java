package pokeraidbot.domain.pokemon;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Set;

public class Pokemon {

    private String name;
    private PokemonTypes types;
    private Set<String> weaknesses;
    private Set<String> resistant;
    private Integer number;
    private String about;
    private String buddyDistance;

    public Pokemon(Integer number, String name, String about, PokemonTypes types, String buddyDistance,
                   Set<String> weaknesses, Set<String> resistantTo) {
        Validate.notNull(number, "Number is null!");
        Validate.notNull(types, "Types is null!");
        Validate.notEmpty(name, "Name is empty!");
        if (types != PokemonTypes.NONE) {
            Validate.notEmpty(weaknesses, "Weaknesses is empty!");
            Validate.notEmpty(resistantTo, "ResistantTo is empty!");
        }

        this.number = number;
        this.name = name;
        this.about = about;
        this.types = types;
        this.buddyDistance = buddyDistance;
        this.weaknesses = weaknesses;
        resistant = resistantTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pokemon)) return false;

        Pokemon pokemon = (Pokemon) o;

        if (name != null ? !name.equals(pokemon.name) : pokemon.name != null) return false;
        if (types != null ? !types.equals(pokemon.types) : pokemon.types != null) return false;
        if (weaknesses != null ? !weaknesses.equals(pokemon.weaknesses) : pokemon.weaknesses != null) return false;
        if (resistant != null ? !resistant.equals(pokemon.resistant) : pokemon.resistant != null) return false;
        if (number != null ? !number.equals(pokemon.number) : pokemon.number != null) return false;
        if (about != null ? !about.equals(pokemon.about) : pokemon.about != null) return false;
        return buddyDistance != null ? buddyDistance.equals(pokemon.buddyDistance) : pokemon.buddyDistance == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (types != null ? types.hashCode() : 0);
        result = 31 * result + (weaknesses != null ? weaknesses.hashCode() : 0);
        result = 31 * result + (resistant != null ? resistant.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (about != null ? about.hashCode() : 0);
        result = 31 * result + (buddyDistance != null ? buddyDistance.hashCode() : 0);
        return result;
    }

    public PokemonTypes getTypes() {
        return types;
    }

    public Set<String> getWeaknesses() {
        return weaknesses;
    }

    public Set<String> getResistant() {
        return resistant;
    }

    public Integer getNumber() {
        return number;
    }

    public String getAbout() {
        return about;
    }

    public String getBuddyDistance() {
        return buddyDistance;
    }

    @Override
    public String toString() {
        return name + (types.getTypeSet().size() > 0 ? " (" + types + ")" : "");
    }

    public String getName() {
        return name;
    }

    public boolean isEgg() {
        return StringUtils.containsIgnoreCase(name, "egg") && name.matches("[Ee][Gg][Gg][1-6]");
    }
}
