package pokeraidbot.infrastructure;

import pokeraidbot.PokemonRepository;
import pokeraidbot.domain.Pokemon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class CounterTextFileParser {
    private Set<CounterPokemon> bestCounters = new HashSet<>();
    private Set<CounterPokemon> goodCounters = new HashSet<>();

    public CounterTextFileParser(String path, String pokemonName, PokemonRepository pokemonRepository) {
        try {
            System.out.println("Parsing counters for " + pokemonName + " ...");
            final InputStream inputStream = CounterTextFileParser.class.getResourceAsStream(path + "/" + pokemonName.toLowerCase() + ".txt");
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = br.readLine();
            if (!line.contains("Supreme Counters")) {
                throw new IllegalStateException("Not properly formatted file!");
            }
            boolean supreme = true;
            boolean supremeDone = false;
            br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                line = br.readLine();
                if (line == null) {
                    break;
                }
                String counterPokemonName = line.trim();
                // Ensure we can get the pokemon from the repository
                final Pokemon p = pokemonRepository.getByName(counterPokemonName);
                if (p == null) {
                    throw new IllegalStateException("Could not find pokemon in repository: " + counterPokemonName);
                }
                if (counterPokemonName != null && counterPokemonName.length() > 0) {
                    Set<String> moves = new HashSet<>();
                    while (((line = br.readLine()) != null) && !(line.equals(""))) {
                        final String trimmedLine = line.trim();
                        final Pokemon pokemon = pokemonRepository.getPokemon(trimmedLine);
                        if (pokemon != null) {
                            break;
                        }
                        if ((!trimmedLine.contains("Counters")) && (!trimmedLine.contains("Quick Move")) && (!trimmedLine.contains("Charge Move"))) {
                            moves.add(trimmedLine);
                        }
                        if (trimmedLine.contains("Good Counters")) {
                            line = br.readLine();
                            supremeDone = true;
                            break;
                        }
                    }
                    CounterPokemon counterPokemon = new CounterPokemon(counterPokemonName, moves);
                    if (supreme) {
                        System.out.println("Supreme counter found: " + counterPokemon);
                        bestCounters.add(counterPokemon);
                    } else {
                        System.out.println("Good counter found: " + counterPokemon);
                        goodCounters.add(counterPokemon);
                    }
                    if (supremeDone) {
                        supreme = false;
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Set<CounterPokemon> getBestCounters() {
        return bestCounters;
    }

    public Set<CounterPokemon> getGoodCounters() {
        return goodCounters;
    }
}
