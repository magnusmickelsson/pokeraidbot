package pokeraidbot.domain.pokemon;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.raid.RaidBossCounters;
import pokeraidbot.infrastructure.CounterTextFileParser;

import java.util.HashMap;
import java.util.Map;

public class PokemonRaidStrategyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonRaidStrategyService.class);

    private Map<String, RaidBossCounters> counters = new HashMap<>();
    private Map<String, String> maxCp = new HashMap<>();
    private static String[] raidBosses = {
            "BAYLEEF",
            "CROCONAW",
            "MAGIKARP",
            "QUILAVA",
            "ELECTABUZZ",
            "EXEGGUTOR",
            "MAGMAR",
            "MUK",
            "WEEZING",
            "ALAKAZAM",
            "ARCANINE",
            "FLAREON",
            "GENGAR",
            "JOLTEON",
            "MACHAMP",
            "VAPOREON",
            "Blastoise".toUpperCase(),
            "Charizard".toUpperCase(),
            "Lapras".toUpperCase(),
            "Lapras".toUpperCase(), "Rhydon".toUpperCase(), "Snorlax".toUpperCase(), "Tyranitar".toUpperCase(),
            "Venusaur".toUpperCase(), "Entei".toUpperCase(), "Articuno".toUpperCase(), "Moltres".toUpperCase(),
            "Zapdos".toUpperCase(), "Lugia".toUpperCase(),
            "Raikou".toUpperCase(), "Suicune".toUpperCase(),
            "Mewtwo".toUpperCase(),
            "Ivysaur".toUpperCase(),
    "Metapod".toUpperCase(),
    "Charmeleon".toUpperCase(),
    "Wartortle".toUpperCase(),
    // Tier 2
    "Magneton".toUpperCase(),
    "Sableye".toUpperCase(),
    "Sandslash".toUpperCase(),
    "Tentacruel".toUpperCase(),
    "Marowak".toUpperCase(),
    "Cloyster".toUpperCase(),
    // Tier 3
    "Ninetales".toUpperCase(),
    "Scyther".toUpperCase(),
    "Omastar".toUpperCase(),
    "Porygon".toUpperCase(),
    // Tier 4
    "Poliwrath".toUpperCase(),
    "Victreebel".toUpperCase(),
    "Golem".toUpperCase(),
    "Nidoking".toUpperCase(),
    "Nidoqueen".toUpperCase()
};

    public PokemonRaidStrategyService(PokemonRepository pokemonRepository) {
        for (String raidBossName : raidBosses) {
            try {
                final CounterTextFileParser parser = new CounterTextFileParser("/counters", raidBossName, pokemonRepository);
                final Pokemon raidBoss = pokemonRepository.getByName(raidBossName);
                if (raidBoss == null) {
                    LOGGER.error("Could not find raidBoss in pokemon repository: " + raidBossName);
                    System.exit(-1);
                }
                final RaidBossCounters raidBossCounters = new RaidBossCounters(raidBoss, parser.getBestCounters(), parser.getGoodCounters());
                counters.put(raidBossName.toUpperCase(), raidBossCounters);
            } catch (RuntimeException e) {
                // No file for this boss, skip it
            }
        }
        LOGGER.info("Parsed " + counters.size() + " raid boss counters.");

        maxCp.put("BAYLEEF", "740");

        maxCp.put("CROCONAW", "913");

        maxCp.put("MAGIKARP", "125");

        maxCp.put("QUILAVA", "847");

        maxCp.put("ELECTABUZZ", "1255");

        maxCp.put("EXEGGUTOR", "1666");

        maxCp.put("MAGMAR", "1288");

        maxCp.put("MUK", "1548");

        maxCp.put("WEEZING", "1247");

        maxCp.put("ALAKAZAM", "1649");

        maxCp.put("ARCANINE", "1622");

        maxCp.put("FLAREON", "1659");

        maxCp.put("GENGAR", "1496");

        maxCp.put("JOLTEON", "1560");

        maxCp.put("MACHAMP", "1650");

        maxCp.put("VAPOREON", "1804");

        addMaxCp("Blastoise", "1309");

        addMaxCp("Charizard", "1535");

        addMaxCp("Lapras", "1487");

        addMaxCp("Rhydon", "1886");

        addMaxCp("Snorlax", "1917");

        addMaxCp("Tyranitar", "2097");

        addMaxCp("Venusaur", "1467");

        addMaxCp("Entei", "1930");

        addMaxCp("Articuno", "1676");

        addMaxCp("Moltres", "1870");

        addMaxCp("Zapdos", "1902");

        addMaxCp("Lugia", "2056");

        addMaxCp("Raikou", "1913");

        addMaxCp("Suicune", "1613");

        addMaxCp("Mewtwo", "2275");

        // New bosses after Niantic surprise attack :p
        // Tier 1
        addMaxCp("Ivysaur", "886");
        addMaxCp("Metapod", "239");
        addMaxCp("Charmeleon", "847");
        addMaxCp("Wartortle", "756");
        // Tier 2
        addMaxCp("Magneton", "1278");
        addMaxCp("Sableye", "745");
        addMaxCp("Sandslash", "1330");
        addMaxCp("Tentacruel", "1356");
        addMaxCp("Marowak", "966");
        addMaxCp("Cloyster", "1414");
        // Tier 3
        addMaxCp("Ninetales", "1233");
        addMaxCp("Scyther", "1408");
        addMaxCp("Omastar", "1534");
        addMaxCp("Porygon", "895");
        // Tier 4
        addMaxCp("Poliwrath", "1395");
        addMaxCp("Victreebel", "1296");
        addMaxCp("Golem", "1666");
        addMaxCp("Nidoking", "1363");
        addMaxCp("Nidoqueen", "1336");

        LOGGER.info("Configured " + maxCp.size() + " raid boss max CP entries.");
    }

    private void addMaxCp(String mewtwo, String cp) {
        maxCp.put(mewtwo.toUpperCase(), cp);
    }

    public RaidBossCounters getCounters(Pokemon pokemon) {
        Validate.notNull(pokemon, "Input pokemon cannot be null!");
        final RaidBossCounters counters = this.counters.get(pokemon.getName().toUpperCase());
        return counters;
    }

    public String getMaxCp(Pokemon pokemon) {
        final String maxCp = this.maxCp.get(pokemon.getName().toUpperCase());
        if (maxCp != null && (!maxCp.isEmpty())) {
            return maxCp;
        } else {
            return null;
        }
    }
}
