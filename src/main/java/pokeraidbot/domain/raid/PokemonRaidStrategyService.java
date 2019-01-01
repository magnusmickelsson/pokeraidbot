package pokeraidbot.domain.raid;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRaidInfo;
import pokeraidbot.domain.pokemon.PokemonRepository;
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
            "Rhydon".toUpperCase(), "Snorlax".toUpperCase(), "Tyranitar".toUpperCase(),
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
            "Marowak".toUpperCase(), // Note: Alolan is tier 4 and active right now because niantic sucks
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
            // Tier 1
            "Wailmer".toUpperCase(),
            "snorunt".toUpperCase(),
            "swablu".toUpperCase(),
            "duskull".toUpperCase(),
            "shuppet".toUpperCase(),
            "bulbasaur".toUpperCase(),
            "charmander".toUpperCase(),
            "squirtle".toUpperCase(),
            "makuhita".toUpperCase(),
            "meditite".toUpperCase(),
            "shinx".toUpperCase(),
            "buizel".toUpperCase(),
            "mareep".toUpperCase(),
            "magnemite".toUpperCase(),
            // Tier 2
            "Mawile".toUpperCase(),
            "dewgong".toUpperCase(),
            "slowbro".toUpperCase(),
            "Manectric".toUpperCase(),
            "Sneasel".toUpperCase(),
            "Misdreavus".toUpperCase(),
            "lickitung".toUpperCase(),
            "venomoth".toUpperCase(),
            "combusken".toUpperCase(),
            "primeape".toUpperCase(),
            "kirlia".toUpperCase(),
            "roselia".toUpperCase(),
            "lanturn".toUpperCase(),
            // Tier 3
            "azumarill".toUpperCase(),
            "jynx".toUpperCase(),
            "piloswine".toUpperCase(),
            "Aerodactyl".toUpperCase(),
            "Starmie".toUpperCase(),
            "Claydol".toUpperCase(),
            "Granbull".toUpperCase(),
            "Pinsir".toUpperCase(),
            "aerodactyl".toUpperCase(),
            "kabutops".toUpperCase(),
            "onix".toUpperCase(),
            "hitmonlee".toUpperCase(),
            "hitmonchan".toUpperCase(),
            "breloom".toUpperCase(),
            "raichu".toUpperCase(),
            "donphan".toUpperCase(),
            "tangela".toUpperCase(),
            "Sharpedo".toUpperCase(),
            "Skarmory".toUpperCase(),
            "Espeon".toUpperCase(),
            "Umbreon".toUpperCase(),
            // Tier 4
            "feraligatr".toUpperCase(),
            "Absol".toUpperCase(),
            "Salamence".toUpperCase(),
            "Aggron".toUpperCase(),
            "Walrein".toUpperCase(),
            "Houndoom".toUpperCase(),
            "Togetic".toUpperCase(),
            // Tier 5
            "Groudon".toUpperCase(),
            "Kyogre".toUpperCase(),
            "Rayquaza".toUpperCase(),
            "Latios".toUpperCase(),
            "Latias".toUpperCase(),
            "Mew".toUpperCase(),
            "Celebi".toUpperCase(),
            "Regice".toUpperCase(),
            "Regirock".toUpperCase(),
            "Registeel".toUpperCase(),
            "Giratina".toUpperCase(),
            "palkia".toUpperCase(),
            "dialga".toUpperCase(),
            "cresselia".toUpperCase(),
            "heatran".toUpperCase(),
            "deoxys".toUpperCase()
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

        populateRaidInfoForBoss(pokemonRepository, "ELECTABUZZ", "1333", 2);

        populateRaidInfoForBoss(pokemonRepository, "EXEGGUTOR", "1722", 2);

        populateRaidInfoForBoss(pokemonRepository, "MAGMAR", "1288", 2);

        populateRaidInfoForBoss(pokemonRepository, "MUK", "1548", 2);

        populateRaidInfoForBoss(pokemonRepository, "WEEZING", "1247", 2);

        populateRaidInfoForBoss(pokemonRepository, "ALAKAZAM", "1649", 3);

        populateRaidInfoForBoss(pokemonRepository, "ARCANINE", "1622", 3);

        populateRaidInfoForBoss(pokemonRepository, "FLAREON", "1730", 3);

        populateRaidInfoForBoss(pokemonRepository, "GENGAR", "1496", 3);

        populateRaidInfoForBoss(pokemonRepository, "JOLTEON", "1650", 3);

        populateRaidInfoForBoss(pokemonRepository, "MACHAMP", "1650", 3);

        populateRaidInfoForBoss(pokemonRepository, "VAPOREON", "1779", 3);

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
        populateRaidInfoForBoss(pokemonRepository, "Cloyster", "1414", 2);
        populateRaidInfoForBoss(pokemonRepository, "egg2", "1", 2);
        // Normally tier 2 but alolan raid is tier 4 because niantic sucks in not letting different pokemons have different id's etc.
        populateRaidInfoForBoss(pokemonRepository, "Marowak", "1048", 4);
        // Tier 3
        populateRaidInfoForBoss(pokemonRepository, "Ninetales", "1233", 3);
        populateRaidInfoForBoss(pokemonRepository, "Scyther", "1546", 3);
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
        // Tier 1
        populateRaidInfoForBoss(pokemonRepository, "Wailmer", "814", 1);
        populateRaidInfoForBoss(pokemonRepository, "Snorunt", "441", 1);
        populateRaidInfoForBoss(pokemonRepository, "Swablu", "412", 1);
        populateRaidInfoForBoss(pokemonRepository, "Shuppet", "498", 1);
        populateRaidInfoForBoss(pokemonRepository, "Duskull", "299", 1);
        populateRaidInfoForBoss(pokemonRepository, "Bulbasaur", "560", 1);
        populateRaidInfoForBoss(pokemonRepository, "Charmander", "475", 1);
        populateRaidInfoForBoss(pokemonRepository, "Squirtle", "462", 1);
        populateRaidInfoForBoss(pokemonRepository, "Makuhita", "426", 1);
        populateRaidInfoForBoss(pokemonRepository, "Meditite", "317", 1);
        populateRaidInfoForBoss(pokemonRepository, "Shinx", "500", 1);
        populateRaidInfoForBoss(pokemonRepository, "Buizel", "602", 1);
        populateRaidInfoForBoss(pokemonRepository, "Mareep", "566", 1);
        populateRaidInfoForBoss(pokemonRepository, "Magnemite", "778", 1);

        // Tier 2
        populateRaidInfoForBoss(pokemonRepository, "Mawile", "934", 2);
        populateRaidInfoForBoss(pokemonRepository, "Dewgong", "1082", 2);
        populateRaidInfoForBoss(pokemonRepository, "Slowbro", "1418", 2);
        populateRaidInfoForBoss(pokemonRepository, "Manectric", "1337", 2);
        populateRaidInfoForBoss(pokemonRepository, "Sneasel", "1067", 2);
        populateRaidInfoForBoss(pokemonRepository, "Misdreavus", "1018", 2);
        populateRaidInfoForBoss(pokemonRepository, "Lickitung", "755", 2);
        populateRaidInfoForBoss(pokemonRepository, "Venomoth", "1107", 2);
        populateRaidInfoForBoss(pokemonRepository, "Combusken", "841", 2);
        populateRaidInfoForBoss(pokemonRepository, "Primeape", "1203", 2);
        populateRaidInfoForBoss(pokemonRepository, "Kirlia", "481", 2);
        populateRaidInfoForBoss(pokemonRepository, "Roselia", "982", 2);
        populateRaidInfoForBoss(pokemonRepository, "Lanturn", "1191", 2);

        // Tier 3
        populateRaidInfoForBoss(pokemonRepository, "Azumarill", "858", 3);
        populateRaidInfoForBoss(pokemonRepository, "Aerodactyl", "1490", 3);
        populateRaidInfoForBoss(pokemonRepository, "Jynx", "1435", 3);
        populateRaidInfoForBoss(pokemonRepository, "Piloswine", "1305", 3);
        populateRaidInfoForBoss(pokemonRepository, "Starmie", "1316", 3);
        populateRaidInfoForBoss(pokemonRepository, "Claydol", "1018", 3);
        populateRaidInfoForBoss(pokemonRepository, "Pinsir", "1583", 3);
        populateRaidInfoForBoss(pokemonRepository, "Granbull", "1394", 3);
        populateRaidInfoForBoss(pokemonRepository, "Aerodactyl", "1490", 3);
        populateRaidInfoForBoss(pokemonRepository, "Kabutops", "1438", 3);
        populateRaidInfoForBoss(pokemonRepository, "Onix", "572", 3);
        populateRaidInfoForBoss(pokemonRepository, "Breloom", "1375", 3);
        populateRaidInfoForBoss(pokemonRepository, "Hitmonlee", "1375", 3);
        populateRaidInfoForBoss(pokemonRepository, "Hitmonchan", "1199", 3);
        populateRaidInfoForBoss(pokemonRepository, "Raichu", "1247", 3);
        populateRaidInfoForBoss(pokemonRepository, "Donphan", "1727", 3);
        populateRaidInfoForBoss(pokemonRepository, "Tangela", "1262", 3);
        populateRaidInfoForBoss(pokemonRepository, "Sharpedo", "1134", 3);
        populateRaidInfoForBoss(pokemonRepository, "Skarmory", "1204", 3);
        populateRaidInfoForBoss(pokemonRepository, "Espeon", "1811", 3);
        populateRaidInfoForBoss(pokemonRepository, "Umbreon", "1221", 3);

        // Tier 4
        populateRaidInfoForBoss(pokemonRepository, "Feraligatr", "1554", 4);
        populateRaidInfoForBoss(pokemonRepository, "Absol", "1443", 4);
        populateRaidInfoForBoss(pokemonRepository, "Aggron", "1714", 4);
        populateRaidInfoForBoss(pokemonRepository, "Salamence", "2018", 4);
        populateRaidInfoForBoss(pokemonRepository, "Walrein", "1489", 4);
        populateRaidInfoForBoss(pokemonRepository, "Houndoom", "1445", 4);
        populateRaidInfoForBoss(pokemonRepository, "Togetic", "881", 4);

        // Tier 5
        populateRaidInfoForBoss(pokemonRepository, "Groudon", "2328", 5);
        populateRaidInfoForBoss(pokemonRepository, "Latios", "2082", 5);
        populateRaidInfoForBoss(pokemonRepository, "Kyogre", "2328", 5);
        populateRaidInfoForBoss(pokemonRepository, "Registeel", "1292", 5);
        populateRaidInfoForBoss(pokemonRepository, "Regice", "1764", 5);
        populateRaidInfoForBoss(pokemonRepository, "Regirock", "1764", 5);
        populateRaidInfoForBoss(pokemonRepository, "Rayquaza", "2083", 5);
        populateRaidInfoForBoss(pokemonRepository, "Latias", "1929", 5);
        populateRaidInfoForBoss(pokemonRepository, "Mew", "1766", 5);
        populateRaidInfoForBoss(pokemonRepository, "Celebi", "1766", 5);
        populateRaidInfoForBoss(pokemonRepository, "Giratina", "1931", 5);
        populateRaidInfoForBoss(pokemonRepository, "Palkia", "2280", 5);
        populateRaidInfoForBoss(pokemonRepository, "Dialga", "2307", 5);
        populateRaidInfoForBoss(pokemonRepository, "Cresselia", "1633", 5);
        populateRaidInfoForBoss(pokemonRepository, "Heatran", "2145", 5);

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
