package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.PokemonRepository;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.PokemonRaidStrategyService;

public class PokemonVsCommand extends Command {
    private final PokemonRaidStrategyService raidInfoService;
    private final PokemonRepository repo;

    public PokemonVsCommand(PokemonRepository repo, PokemonRaidStrategyService raidInfoService) {
        this.raidInfoService = raidInfoService;
        this.name = "vs";
        this.help = "!raid vs [Pokemon] - lists information about a pokemon, it's types, weaknesses etc.";
        this.repo = repo;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            String pokemonName = commandEvent.getArgs();
            final Pokemon pokemon = repo.getByName(pokemonName);
            commandEvent.reply("**" + pokemon + "**\n" +
//                    pokemon.getAbout() + "\n" +
                    "Weaknesses: " + pokemon.getWeaknesses() + "\n" +
                    "Resistant to: " + pokemon.getResistant() + "\n" +
                    "Top DPS counters: " + raidInfoService.getCounters(pokemon) + " **(if correct moveset)**\n" +
                            "Max CP (100% IV): " + raidInfoService.getMaxCp(pokemon) + "\n"
//                    "Buddy distance: " + pokemon.getBuddyDistance() + "\n"
            );
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
