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
            "Mewtwo".toUpperCase()
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

        maxCp.put("Blastoise".toUpperCase(), "1309");

        maxCp.put("Charizard".toUpperCase(), "1535");

        maxCp.put("Lapras".toUpperCase(), "1487");

        maxCp.put("Rhydon".toUpperCase(), "1886");

        maxCp.put("Snorlax".toUpperCase(), "1917");

        maxCp.put("Tyranitar".toUpperCase(), "2097");

        maxCp.put("Venusaur".toUpperCase(), "1467");

        maxCp.put("Entei".toUpperCase(), "1930");

        maxCp.put("Articuno".toUpperCase(), "1676");

        maxCp.put("Moltres".toUpperCase(), "1870");

        maxCp.put("Zapdos".toUpperCase(), "1902");

        maxCp.put("Lugia".toUpperCase(), "2056");

        maxCp.put("Raikou".toUpperCase(), "1913");

        maxCp.put("Suicune".toUpperCase(), "1613");

        maxCp.put("Mewtwo".toUpperCase(), "2275");

        LOGGER.info("Configured " + maxCp.size() + " raid boss max CP entries.");
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
