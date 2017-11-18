package pokeraidbot.domain.pokemon;

import org.apache.commons.lang3.Validate;

public class PokemonRaidInfo {
    private Pokemon pokemon;
    private int maxCp;
    private int bossTier;

    public PokemonRaidInfo(Pokemon pokemon, String maxCp, int bossTier) {
        this(pokemon, Integer.parseInt(maxCp), bossTier);
    }

    public PokemonRaidInfo(Pokemon pokemon, int maxCp, int bossTier) {
        Validate.notNull(pokemon, "Pokemon");
        Validate.isTrue(maxCp > 0 && maxCp < 3000, "Max CP for raidboss is in interval 0 to 3000");
        Validate.isTrue(bossTier > 0 && bossTier < 6, "Raid bosses are in tier 1 to 5");
        this.pokemon = pokemon;
        this.maxCp = maxCp;
        this.bossTier = bossTier;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public int getMaxCp() {
        return maxCp;
    }

    public int getBossTier() {
        return bossTier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonRaidInfo)) return false;

        PokemonRaidInfo that = (PokemonRaidInfo) o;

        if (maxCp != that.maxCp) return false;
        if (bossTier != that.bossTier) return false;
        return pokemon != null ? pokemon.equals(that.pokemon) : that.pokemon == null;
    }

    @Override
    public int hashCode() {
        int result = pokemon != null ? pokemon.hashCode() : 0;
        result = 31 * result + maxCp;
        result = 31 * result + bossTier;
        return result;
    }

    @Override
    public String toString() {
        return "PokemonRaidInfo{" +
                "pokemon=" + pokemon +
                ", maxCp=" + maxCp +
                ", bossTier=" + bossTier +
                '}';
    }
}
