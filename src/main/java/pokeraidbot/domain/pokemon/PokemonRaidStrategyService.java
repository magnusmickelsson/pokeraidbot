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
    private Map<String, PokemonRaidInfo> pokemonRaidInfo = new HashMap<>();
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
            // Tier 1
            "Ivysaur".toUpperCase(),
            "Metapod".toUpperCase(),
            "Charmeleon".toUpperCase(),
            "Wartortle".toUpperCase(),
            "egg1".toUpperCase(),
            // Tier 2
            "Magneton".toUpperCase(),
            "Sableye".toUpperCase(),
            "Sandslash".toUpperCase(),
            "Tentacruel".toUpperCase(),
            "Marowak".toUpperCase(),
            "Cloyster".toUpperCase(),
            "egg2".toUpperCase(),
            // Tier 3
            "Ninetales".toUpperCase(),
            "Scyther".toUpperCase(),
            "Omastar".toUpperCase(),
            "Porygon".toUpperCase(),
            "egg3".toUpperCase(),
            // Tier 4
            "Poliwrath".toUpperCase(),
            "Victreebel".toUpperCase(),
            "Golem".toUpperCase(),
            "Nidoking".toUpperCase(),
            "Nidoqueen".toUpperCase(),
            "egg4".toUpperCase(),
            // Tier 5 /EX
            "egg5".toUpperCase(),
            "Ho-oh".toUpperCase(),
            // ==== Generation 3 ====
            // Tier 2
            "Mawile".toUpperCase(),
            // Tier 4
            "Absol".toUpperCase(),
            "Salamence".toUpperCase(),
            // Tier 5
            "Groudon".toUpperCase()
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

        populateRaidInfoForBoss(pokemonRepository, "BAYLEEF", "740", 1);

        populateRaidInfoForBoss(pokemonRepository, "CROCONAW", "913", 1);

        populateRaidInfoForBoss(pokemonRepository, "MAGIKARP", "125", 1);

        populateRaidInfoForBoss(pokemonRepository, "QUILAVA", "847", 1);

        populateRaidInfoForBoss(pokemonRepository, "ELECTABUZZ", "1255", 2);

        populateRaidInfoForBoss(pokemonRepository, "EXEGGUTOR", "1666", 2);

        populateRaidInfoForBoss(pokemonRepository, "MAGMAR", "1288", 2);

        populateRaidInfoForBoss(pokemonRepository, "MUK", "1548", 2);

        populateRaidInfoForBoss(pokemonRepository, "WEEZING", "1247", 2);

        populateRaidInfoForBoss(pokemonRepository, "ALAKAZAM", "1649", 3);

        populateRaidInfoForBoss(pokemonRepository, "ARCANINE", "1622", 3);

        populateRaidInfoForBoss(pokemonRepository, "FLAREON", "1659", 3);

        populateRaidInfoForBoss(pokemonRepository, "GENGAR", "1496", 3);

        populateRaidInfoForBoss(pokemonRepository, "JOLTEON", "1560", 3);

        populateRaidInfoForBoss(pokemonRepository, "MACHAMP", "1650", 3);

        populateRaidInfoForBoss(pokemonRepository, "VAPOREON", "1804", 3);

        populateRaidInfoForBoss(pokemonRepository, "Blastoise", "1309", 4);

        populateRaidInfoForBoss(pokemonRepository, "Charizard", "1535", 4);

        populateRaidInfoForBoss(pokemonRepository, "Lapras", "1487", 4);

        populateRaidInfoForBoss(pokemonRepository, "Rhydon", "1886", 4);

        populateRaidInfoForBoss(pokemonRepository, "Snorlax", "1917", 4);

        populateRaidInfoForBoss(pokemonRepository, "Tyranitar", "2097", 4);

        populateRaidInfoForBoss(pokemonRepository, "Venusaur", "1467", 4);

        populateRaidInfoForBoss(pokemonRepository, "Entei", "1930", 5);

        populateRaidInfoForBoss(pokemonRepository, "Articuno", "1676", 5);

        populateRaidInfoForBoss(pokemonRepository, "Moltres", "1870", 5);

        populateRaidInfoForBoss(pokemonRepository, "Zapdos", "1902", 5);

        populateRaidInfoForBoss(pokemonRepository, "Lugia", "2056", 5);

        populateRaidInfoForBoss(pokemonRepository, "Raikou", "1913", 5);

        populateRaidInfoForBoss(pokemonRepository, "Suicune", "1613", 5);

        populateRaidInfoForBoss(pokemonRepository, "Mewtwo", "2275", 5);

        // New bosses after Niantic surprise attack :p
        // Tier 1
        populateRaidInfoForBoss(pokemonRepository, "Ivysaur", "886", 1);
        populateRaidInfoForBoss(pokemonRepository, "Metapod", "239", 1);
        populateRaidInfoForBoss(pokemonRepository, "Charmeleon", "847", 1);
        populateRaidInfoForBoss(pokemonRepository, "Wartortle", "756", 1);
        populateRaidInfoForBoss(pokemonRepository, "egg1", "1", 1);
        // Tier 2
        populateRaidInfoForBoss(pokemonRepository, "Magneton", "1278", 2);
        populateRaidInfoForBoss(pokemonRepository, "Sableye", "745", 2);
        populateRaidInfoForBoss(pokemonRepository, "Sandslash", "1330", 2);
        populateRaidInfoForBoss(pokemonRepository, "Tentacruel", "1356", 2);
        populateRaidInfoForBoss(pokemonRepository, "Marowak", "966", 2);
        populateRaidInfoForBoss(pokemonRepository, "Cloyster", "1414", 2);
        populateRaidInfoForBoss(pokemonRepository, "egg2", "1", 2);
        // Tier 3
        populateRaidInfoForBoss(pokemonRepository, "Ninetales", "1233", 3);
        populateRaidInfoForBoss(pokemonRepository, "Scyther", "1408", 3);
        populateRaidInfoForBoss(pokemonRepository, "Omastar", "1534", 3);
        populateRaidInfoForBoss(pokemonRepository, "Porygon", "895", 3);
        populateRaidInfoForBoss(pokemonRepository, "egg3", "1", 3);

        // Tier 4
        populateRaidInfoForBoss(pokemonRepository, "Poliwrath", "1395", 4);
        populateRaidInfoForBoss(pokemonRepository, "Victreebel", "1296", 4);
        populateRaidInfoForBoss(pokemonRepository, "Golem", "1666", 4);
        populateRaidInfoForBoss(pokemonRepository, "Nidoking", "1363", 4);
        populateRaidInfoForBoss(pokemonRepository, "Nidoqueen", "1336", 4);
        populateRaidInfoForBoss(pokemonRepository, "egg4", "1", 4);

        // Tier 5 / EX
        populateRaidInfoForBoss(pokemonRepository, "egg5", "1", 5);
        populateRaidInfoForBoss(pokemonRepository, "Ho-oh", "2222", 5);

        // ==== Gen 3 ====

        // Tier 2
        populateRaidInfoForBoss(pokemonRepository, "Mawile", "848", 2);

        // Tier 4
        populateRaidInfoForBoss(pokemonRepository, "Absol", "1303", 4);
        populateRaidInfoForBoss(pokemonRepository, "Salamence", "2018", 4);
        // Tier 5
        populateRaidInfoForBoss(pokemonRepository, "Groudon", "2328", 5);

        LOGGER.info("Configured " + pokemonRaidInfo.size() + " raid boss information entries.");
    }

    private void populateRaidInfoForBoss(PokemonRepository pokemonRepository, String pokemonName, String maxCp, int bossTier) {
        final Pokemon pokemon = pokemonRepository.getByName(pokemonName.toUpperCase());
        if (pokemon == null) {
            LOGGER.warn("Exception when getting pokemon by name " + pokemonName + " - needs to be added to repo data file.");
            return;
        }
        pokemonRaidInfo.put(pokemonName.toUpperCase(), new PokemonRaidInfo(pokemon, maxCp, bossTier));
    }

    public RaidBossCounters getCounters(Pokemon pokemon) {
        Validate.notNull(pokemon, "Input pokemon cannot be null!");
        final RaidBossCounters counters = this.counters.get(pokemon.getName().toUpperCase());
        return counters;
    }

    public String getMaxCp(Pokemon pokemon) {
        final PokemonRaidInfo pokemonRaidInfo = this.pokemonRaidInfo.get(pokemon.getName().toUpperCase());
        if (pokemonRaidInfo == null) {
            return null;
        }
        final String maxCp = String.valueOf(pokemonRaidInfo.getMaxCp());
        if (maxCp != null && (!maxCp.isEmpty())) {
            return maxCp;
        } else {
            return null;
        }
    }

    public PokemonRaidInfo getRaidInfo(Pokemon pokemon) {
        final String pokemonName = pokemon.getName().toUpperCase();
        return pokemonRaidInfo.get(pokemonName);
    }
}
