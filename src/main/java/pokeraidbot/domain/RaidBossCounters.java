package pokeraidbot.domain;

import pokeraidbot.infrastructure.CounterPokemon;

import java.util.HashSet;
import java.util.Set;

public class RaidBossCounters {
    private Pokemon pokemon;
    private Set<CounterPokemon> supremeCounters = new HashSet<>();
    private Set<CounterPokemon> goodCounters = new HashSet<>();

    public RaidBossCounters(Pokemon pokemon, Set<CounterPokemon> supremeCounters, Set<CounterPokemon> goodCounters) {
        this.pokemon = pokemon;
        this.supremeCounters = supremeCounters;
        this.goodCounters = goodCounters;
    }

    public Set<CounterPokemon> getSupremeCounters() {
        return supremeCounters;
    }

    public Set<CounterPokemon> getGoodCounters() {
        return goodCounters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaidBossCounters)) return false;

        RaidBossCounters that = (RaidBossCounters) o;

        if (pokemon != null ? !pokemon.equals(that.pokemon) : that.pokemon != null) return false;
        if (supremeCounters != null ? !supremeCounters.equals(that.supremeCounters) : that.supremeCounters != null)
            return false;
        return goodCounters != null ? goodCounters.equals(that.goodCounters) : that.goodCounters == null;
    }

    @Override
    public int hashCode() {
        int result = pokemon != null ? pokemon.hashCode() : 0;
        result = 31 * result + (supremeCounters != null ? supremeCounters.hashCode() : 0);
        result = 31 * result + (goodCounters != null ? goodCounters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RaidBossCounters{" +
                "pokemon=" + pokemon +
                ", supremeCounters=" + supremeCounters +
                ", goodCounters=" + goodCounters +
                '}';
    }
}
