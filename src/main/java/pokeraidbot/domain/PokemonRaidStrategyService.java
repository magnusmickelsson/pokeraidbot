package pokeraidbot.domain;

import java.util.*;

public class PokemonRaidStrategyService {
    private static final List<String> VS_WATER = Arrays.asList("Exeggutor", "Venusaur", "Jolteon");
    private static final List<String> VS_GRASS = Arrays.asList("Flareon", "Charizard", "Gengar");
    private static final List<String> VS_FIRE = Arrays.asList("Golem", "Starmie", "Vaporeon");
    private static final List<String> VS_ELECTRIC = Arrays.asList("Gengar", "Dragonite", "Alakazam", "Flareon");
    private static final List<String> VS_POISON = Arrays.asList("Alakazam", "Espeon", "Exeggutor");
    private Map<String, Collection<String>> counters = new HashMap<>();
    private Map<String,String> maxCp = new HashMap<>();

    public PokemonRaidStrategyService() {
        counters.put("BAYLEEF", VS_GRASS);
        maxCp.put("BAYLEEF", "740");

        counters.put("CROCONAW", VS_WATER);
        maxCp.put("CROCONAW", "913");

        counters.put("MAGIKARP", VS_WATER);
        maxCp.put("MAGIKARP", "125");

        counters.put("QUILAVA", VS_FIRE);
        maxCp.put("QUILAVA", "847");

        counters.put("ELECTABUZZ", VS_ELECTRIC);
        maxCp.put("ELECTABUZZ", "1255");

        counters.put("EXEGGUTOR", Arrays.asList("Pinsir", "Gengar", "Heracross"));
        maxCp.put("EXEGGUTOR", "1666");

        counters.put("MAGMAR", VS_FIRE);
        maxCp.put("MAGMAR", "1288");

        counters.put("MUK", VS_POISON);
        maxCp.put("MUK", "1548");

        counters.put("WEEZING", VS_POISON);
        maxCp.put("WEEZING", "1247");

        counters.put("ALAKAZAM", Arrays.asList("Gengar", "Houndoom", "Tyranitar"));
        maxCp.put("ALAKAZAM", "1649");

        counters.put("ARCANINE", VS_FIRE);
        maxCp.put("ARCANINE", "1622");

        counters.put("FLAREON", VS_FIRE);
        maxCp.put("FLAREON", "1659");

        counters.put("GENGAR", Arrays.asList("Gengar", "Alakazam", "Espeon"));
        maxCp.put("GENGAR", "1496");

        counters.put("JOLTEON", VS_ELECTRIC);
        maxCp.put("JOLTEON", "1560");

        counters.put("MACHAMP", Arrays.asList("Alakazam", "Espeon", "Exeggutor"));
        maxCp.put("MACHAMP", "1650");

        counters.put("VAPOREON", VS_WATER);
        maxCp.put("VAPOREON", "1804");

        counters.put("Blastoise".toUpperCase(), VS_WATER);
        maxCp.put("Blastoise".toUpperCase(), "1309");

        counters.put("Charizard".toUpperCase(), Arrays.asList("Golem", "Omastar", "Sudowoodo"));
        maxCp.put("Charizard".toUpperCase(), "1535");

        counters.put("Lapras".toUpperCase(), Arrays.asList("Machamp", "Heracross", "Exeggutor"));
        maxCp.put("Lapras".toUpperCase(), "1487");

        counters.put("Rhydon".toUpperCase(), Arrays.asList("Exeggutor", "Venusaur", "Victreebel"));
        maxCp.put("Rhydon".toUpperCase(), "1886");

        counters.put("Snorlax".toUpperCase(), Arrays.asList("Machamp", "Heracross", "Alakazam"));
        maxCp.put("Snorlax".toUpperCase(), "1917");

        counters.put("Tyranitar".toUpperCase(), Arrays.asList("Machamp", "Poliwrath", "Donphan", "Vaporeon", "Ursaring"));
        maxCp.put("Tyranitar".toUpperCase(), "2097");

        counters.put("Venusaur".toUpperCase(), Arrays.asList("Alakazam", "Flareon", "Espeon"));
        maxCp.put("Venusaur".toUpperCase(), "1467");

        counters.put("Entei".toUpperCase(), Arrays.asList("Golem", "Rhydon", "Vaporeon", "Tyranitar", "Omastar"));
        maxCp.put("Entei".toUpperCase(), "1930");

        counters.put("Articuno".toUpperCase(), Arrays.asList("Golem", "Flareon", "Moltres"));
        maxCp.put("Articuno".toUpperCase(), "1676");

        counters.put("Moltres".toUpperCase(), Arrays.asList("Golem", "Tyranitar", "Rhydon", "Vaporeon", "Omastar"));
        maxCp.put("Moltres".toUpperCase(), "1870");

        counters.put("Zapdos".toUpperCase(), Arrays.asList("Golem", "Tyranitar", "Dragonite"));
        maxCp.put("Zapdos".toUpperCase(), "1902");

        counters.put("Lugia".toUpperCase(), Arrays.asList("Tyranitar", "Houndoom", "Jolteon", "Zapdos"));
        maxCp.put("Lugia".toUpperCase(), "2056");

        counters.put("Raikou".toUpperCase(), VS_ELECTRIC);
        maxCp.put("Raikou".toUpperCase(), "1913");

        counters.put("Suicune".toUpperCase(), VS_WATER);
        maxCp.put("Suicune".toUpperCase(), "1613");

        counters.put("Mewtwo".toUpperCase(), Arrays.asList("Tyranitar", "Houndoom", "Scizor"));
        maxCp.put("Mewtwo".toUpperCase(), "2275");
    }

    public String getCounters(Pokemon pokemon) {
        return "" + counters.get(pokemon.getName().toUpperCase());
    }

    public String getMaxCp(Pokemon pokemon) {
        return maxCp.get(pokemon.getName().toUpperCase());
    }
}
