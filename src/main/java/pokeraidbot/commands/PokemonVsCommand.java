package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.PokemonRepository;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.PokemonRaidStrategyService;
import pokeraidbot.domain.RaidBossCounters;
import pokeraidbot.infrastructure.CounterPokemon;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PokemonVsCommand extends Command {
    private final PokemonRaidStrategyService raidInfoService;
    private final PokemonRepository repo;

    public PokemonVsCommand(PokemonRepository repo, PokemonRaidStrategyService raidInfoService) {
        this.raidInfoService = raidInfoService;
        this.name = "vs";
        this.help = "List information about a pokemon, it's types, weaknesses etc. - !raid vs [Pokemon]";
        this.repo = repo;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            String pokemonName = commandEvent.getArgs();
            final Pokemon pokemon = repo.getByName(pokemonName);
            final RaidBossCounters counters = raidInfoService.getCounters(pokemon);
            final String maxCp = raidInfoService.getMaxCp(pokemon);
//                    pokemon.getAbout() + "\n" +
//                    "Buddy distance: " + pokemon.getBuddyDistance() + "\n"
            StringBuilder builder = new StringBuilder();
            builder.append("**").append(pokemon).append("**\n").append("Weaknesses: ")
                    .append(pokemon.getWeaknesses()).append("\n").append("Resistant to: ")
                    .append(pokemon.getResistant()).append("\n");
            if (counters != null && counters.getSupremeCounters().size() > 0) {
                builder.append("Best counter: ");
                final Optional<CounterPokemon> bestCounterPokemon = counters.getSupremeCounters().stream().findFirst();
                builder.append(bestCounterPokemon.get());
                if (counters.getSupremeCounters().size() > 1 || counters.getGoodCounters().size() > 0) {
                    final LinkedList<CounterPokemon> totalCounters = new LinkedList<>(counters.getSupremeCounters());
                    totalCounters.addAll(counters.getGoodCounters());
                    List<String> otherCounters = totalCounters.stream().skip(1).map(CounterPokemon::getCounterPokemonName).collect(Collectors.toList());
                    builder.append("\nOther counters: [");
                    builder.append(StringUtils.join(otherCounters.toArray(), ", "));
                    builder.append("] **(if correct moveset)**\n");
                }
            }

            if (maxCp != null) {
                builder.append("Max CP (100% IV): ").append(maxCp).append("\n");
            }

            commandEvent.reply(builder.toString());
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
