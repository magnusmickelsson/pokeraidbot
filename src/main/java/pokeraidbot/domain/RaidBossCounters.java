package pokeraidbot.domain;

import pokeraidbot.infrastructure.CounterPokemon;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RaidBossCounters {
    private Pokemon pokemon;
    private Set<CounterPokemon> supremeCounters = new LinkedHashSet<>();
    private Set<CounterPokemon> goodCounters = new LinkedHashSet<>();

    public RaidBossCounters(Pokemon pokemon, Set<CounterPokemon> supremeCounters, Set<CounterPokemon> goodCounters) {
        this.pokemon = pokemon;
        this.supremeCounters.addAll(supremeCounters);
        this.goodCounters.addAll(goodCounters);
    }

    public Set<CounterPokemon> getSupremeCounters() {
        return Collections.unmodifiableSet(supremeCounters);
    }

    public Set<CounterPokemon> getGoodCounters() {
        return Collections.unmodifiableSet(goodCounters);
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
