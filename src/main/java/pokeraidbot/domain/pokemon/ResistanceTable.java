package pokeraidbot.domain.pokemon;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

// Todo: test cases against this class
public class ResistanceTable {
    private Map<String, Map<String, Effect>> resistances = new HashMap<>();

    public ResistanceTable() {
        setResistancesFor("Normal", Arrays.asList(Pair.of("Rock", Effect.MINOR),
                Pair.of("Ghost", Effect.NONE),
                Pair.of("Steel", Effect.MINOR)));
        setResistancesFor("Fire", Arrays.asList(Pair.of("Fire", Effect.MINOR),
                Pair.of("Water", Effect.MINOR),
                Pair.of("Grass", Effect.SUPER), Pair.of("Ice", Effect.SUPER),
                Pair.of("Bug", Effect.SUPER), Pair.of("Rock", Effect.MINOR),
                Pair.of("Dragon", Effect.MINOR), Pair.of("Steel", Effect.SUPER)));
        setResistancesFor("Water", Arrays.asList(Pair.of("Fire", Effect.SUPER),
                Pair.of("Water", Effect.MINOR),
                Pair.of("Grass", Effect.MINOR), Pair.of("Ground", Effect.SUPER),
                Pair.of("Rock", Effect.SUPER), Pair.of("Dragon", Effect.MINOR)));
        setResistancesFor("Electric", Arrays.asList(Pair.of("Water", Effect.SUPER),
                Pair.of("Electric", Effect.MINOR),
                Pair.of("Grass", Effect.MINOR), Pair.of("Ground", Effect.NONE),
                Pair.of("Flying", Effect.SUPER), Pair.of("Dragon", Effect.MINOR)));
        setResistancesFor("Grass", Arrays.asList(Pair.of("Fire", Effect.MINOR),
                Pair.of("Water", Effect.SUPER),
                Pair.of("Grass", Effect.MINOR), Pair.of("Poison", Effect.MINOR),
                Pair.of("Ground", Effect.SUPER), Pair.of("Flying", Effect.MINOR),
                Pair.of("Bug", Effect.MINOR), Pair.of("Rock", Effect.SUPER),
                Pair.of("Dragon", Effect.MINOR), Pair.of("Steel", Effect.MINOR)));
        setResistancesFor("Ice", Arrays.asList(Pair.of("Fire", Effect.MINOR),
                Pair.of("Water", Effect.MINOR),
                Pair.of("Grass", Effect.SUPER), Pair.of("Ice", Effect.MINOR),
                Pair.of("Ground", Effect.SUPER), Pair.of("Flying", Effect.SUPER),
                Pair.of("Dragon", Effect.SUPER), Pair.of("Steel", Effect.MINOR)));
        setResistancesFor("Fighting", Arrays.asList(Pair.of("Normal", Effect.SUPER),
                Pair.of("Ice", Effect.SUPER),
                Pair.of("Poison", Effect.MINOR), Pair.of("Flying", Effect.MINOR),
                Pair.of("Psychic", Effect.MINOR), Pair.of("Bug", Effect.MINOR),
                Pair.of("Rock", Effect.SUPER), Pair.of("Ghost", Effect.NONE),
                Pair.of("Dark", Effect.SUPER), Pair.of("Steel", Effect.SUPER),
                Pair.of("Fairy", Effect.MINOR)));
        setResistancesFor("Poison", Arrays.asList(Pair.of("Grass", Effect.SUPER),
                Pair.of("Poison", Effect.MINOR),
                Pair.of("Ground", Effect.MINOR), Pair.of("Rock", Effect.MINOR),
                Pair.of("Ghost", Effect.MINOR), Pair.of("Steel", Effect.NONE),
                Pair.of("Fairy", Effect.SUPER)));
        setResistancesFor("Ground", Arrays.asList(Pair.of("Fire", Effect.SUPER),
                Pair.of("Electric", Effect.SUPER),
                Pair.of("Grass", Effect.MINOR), Pair.of("Poison", Effect.SUPER),
                Pair.of("Flying", Effect.NONE), Pair.of("Bug", Effect.MINOR),
                Pair.of("Rock", Effect.SUPER), Pair.of("Steel", Effect.SUPER)));
        setResistancesFor("Flying", Arrays.asList(Pair.of("Grass", Effect.SUPER),
                Pair.of("Electric", Effect.MINOR),
                Pair.of("Fighting", Effect.SUPER), Pair.of("Bug", Effect.SUPER),
                Pair.of("Rock", Effect.MINOR), Pair.of("Steel", Effect.MINOR)));
        setResistancesFor("Psychic", Arrays.asList(Pair.of("Fighting", Effect.SUPER),
                Pair.of("Poison", Effect.SUPER),
                Pair.of("Psychic", Effect.MINOR), Pair.of("Dark", Effect.NONE),
                Pair.of("Steel", Effect.MINOR)));
        setResistancesFor("Bug", Arrays.asList(Pair.of("Grass", Effect.SUPER),
                Pair.of("Fire", Effect.MINOR),
                Pair.of("Fighting", Effect.MINOR), Pair.of("Poison", Effect.MINOR),
                Pair.of("Flying", Effect.MINOR), Pair.of("Psychic", Effect.SUPER),
                Pair.of("Ghost", Effect.MINOR), Pair.of("Dark", Effect.SUPER),
                Pair.of("Steel", Effect.MINOR), Pair.of("Fairy", Effect.MINOR)));
        setResistancesFor("Rock", Arrays.asList(Pair.of("Fire", Effect.SUPER),
                Pair.of("Ice", Effect.SUPER),
                Pair.of("Fighting", Effect.MINOR), Pair.of("Ground", Effect.MINOR),
                Pair.of("Flying", Effect.SUPER), Pair.of("Bug", Effect.SUPER),
                Pair.of("Steel", Effect.MINOR)));
        setResistancesFor("Ghost", Arrays.asList(Pair.of("Normal", Effect.NONE),
                Pair.of("Psychic", Effect.SUPER),
                Pair.of("Ghost", Effect.SUPER), Pair.of("Dark", Effect.MINOR)));
        setResistancesFor("Dragon", Arrays.asList(Pair.of("Dragon", Effect.SUPER),
                Pair.of("Steel", Effect.MINOR),
                Pair.of("Fairy", Effect.NONE)));
        setResistancesFor("Dark", Arrays.asList(Pair.of("Psychic", Effect.SUPER),
                Pair.of("Fighting", Effect.MINOR),
                Pair.of("Ghost", Effect.SUPER), Pair.of("Dark", Effect.MINOR),
                Pair.of("Fairy", Effect.MINOR)));
        setResistancesFor("Steel", Arrays.asList(Pair.of("Fire", Effect.MINOR),
                Pair.of("Water", Effect.MINOR),
                Pair.of("Electric", Effect.MINOR), Pair.of("Ice", Effect.SUPER),
                Pair.of("Rock", Effect.SUPER), Pair.of("Steel", Effect.MINOR),
                Pair.of("Fairy", Effect.SUPER)));
        setResistancesFor("Fairy", Arrays.asList(Pair.of("Fire", Effect.MINOR),
                Pair.of("Fighting", Effect.SUPER),
                Pair.of("Poison", Effect.MINOR), Pair.of("Steel", Effect.MINOR),
                Pair.of("Dark", Effect.SUPER), Pair.of("Dragon", Effect.SUPER)));
    }

    private void setResistancesFor(String type, List<Pair<String, Effect>> resistanceValues) {
        final List<String> types = resistanceValues.stream().map(Pair::getLeft).collect(Collectors.toList());
        final List<String> remainingTypes =
                PokemonTypes.allTypes.stream().filter(t -> (!types.contains(t))).collect(Collectors.toList());
        Map<String, Effect> resistancesMap = new HashMap<>();
        for (String t : remainingTypes) {
            resistancesMap.put(t, Effect.NORMAL);
        }
        for (Pair<String, Effect> typeEffect : resistanceValues) {
            resistancesMap.put(typeEffect.getLeft(), typeEffect.getRight());
        }
        resistances.put(type, resistancesMap);
    }

    public boolean typeIsStrongVsPokemon(String pokemonType, String typeToCheck) {
        final float typeStrengthValue = typeStrengthValue(typeToCheck, pokemonType);
        return typeStrengthValue > 1.0f;
    }

    public float typeStrengthValue(String pokemonType, String typeToCheck) {
        final Map<String, Effect> stringEffectMap = resistances.get(pokemonType);
        if (stringEffectMap == null) {
            throw new IllegalArgumentException("No resistances for pokemon type: " + pokemonType);
        }
        final Effect effect = stringEffectMap.get(typeToCheck);
        if (effect == null) {
            throw new IllegalArgumentException("No effect found for type to check: " + typeToCheck);
        }
        return effect.effectValue;
    }

    public Set<String> getStrengths(PokemonTypes pokemonTypes) {
        final LinkedHashSet<String> results = new LinkedHashSet<>();
        for (String type : PokemonTypes.allTypes) {
            float multiplier = 1f;
            for (String pokemonType : pokemonTypes.getTypeSet()) {
                multiplier *= typeStrengthValue(pokemonType, type);
            }
            if (multiplier > 1f) {
                results.add(type);
            }
        }
        return results;
    }

    public Set<String> getWeaknesses(PokemonTypes pokemonTypes) {
        final LinkedHashSet<String> results = new LinkedHashSet<>();
        for (String type : PokemonTypes.allTypes) {
            float multiplier = 1f;
            for (String pokemonType : pokemonTypes.getTypeSet()) {
                multiplier *= typeStrengthValue(type, pokemonType);
            }
            if (multiplier > 1f) {
                results.add(type);
            }
        }
        return results;
    }

    public Set<String> getResistantTo(PokemonTypes pokemonTypes) {
        final LinkedHashSet<String> results = new LinkedHashSet<>();
        for (String type : PokemonTypes.allTypes) {
            float multiplier = 1f;
            for (String pokemonType : pokemonTypes.getTypeSet()) {
                multiplier *= typeStrengthValue(pokemonType, type);
            }
            if (multiplier < 1f) {
                results.add(type);
            }
        }
        return results;
    }

    public enum Effect {
        NONE(0f), MINOR(0.5f), NORMAL(1f), SUPER(2f);
        private float effectValue;

        Effect(float value) {
            this.effectValue = value;
        }

        public float getEffectValue() {
            return effectValue;
        }
    }
}
