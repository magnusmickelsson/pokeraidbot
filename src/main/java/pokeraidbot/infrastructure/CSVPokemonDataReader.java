package pokeraidbot.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.Utils;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CSVPokemonDataReader {
    private String resourceName;
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVPokemonDataReader.class);

    public CSVPokemonDataReader(String resourceName) {
        this.resourceName = resourceName;
    }

    public Set<Pokemon> readAll() {
        String line;
        Set<Pokemon> pokemons = new HashSet<>();
        try {
            final InputStream resourceAsStream = CSVPokemonDataReader.class.getResourceAsStream(resourceName);
            if (resourceAsStream == null) {
                throw new FileNotFoundException(resourceName);
            }
            final InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);

            while ((line = br.readLine()) != null) {
                String[] rowElements = line.split(",");
                if (rowElements[0].equalsIgnoreCase("Ndex")) {
                    // This is the header of the file, ignore
                } else {
                    String id = rowElements[0].trim();
                    String name = rowElements[1].trim();
                    String type1 = rowElements[2].trim();
                    String type2 = rowElements[3].trim();
                    List<String> types = new ArrayList<>();
                    if (!type1.equalsIgnoreCase("None")) {
                        types.add(type1);
                    }
                    if (!type2.equalsIgnoreCase("None")) {
                        types.add(type2);
                    }
                    final PokemonTypes pokemonTypes = new PokemonTypes(types);
                    Pokemon pokemon = new Pokemon(Integer.parseInt(id), name, "About not used",
                            pokemonTypes, "", Utils.getWeaknessesFor(pokemonTypes),
                            Utils.getResistantTo(pokemonTypes));
                    pokemons.add(pokemon);
                }
            }

        } catch (IOException e) {
            LOGGER.error("Error while trying to open pokemon file " + resourceName + ": " + e.getMessage());
        }

        LOGGER.info("Parsed " + pokemons.size() + " pokemons from \"" + resourceName + "\".");

        return pokemons;
    }
}
