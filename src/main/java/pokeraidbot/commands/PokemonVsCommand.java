package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.PokemonRepository;
import pokeraidbot.domain.Pokemon;

public class PokemonVsCommand extends Command {
    private PokemonRepository repo;

    public PokemonVsCommand(PokemonRepository repo) {
        this.name = "vs";
        this.help = "!raid vs [Pokemon] - lists information about a pokemon, it's types, weaknesses etc.";
        this.repo = repo;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            String pokemonName = commandEvent.getArgs();
            final Pokemon pokemon = repo.getByName(pokemonName);
            commandEvent.reply("" + pokemon + "\n" +
//                    pokemon.getAbout() + "\n" +
                    "Weaknesses: " + pokemon.getWeaknesses() + "\n" +
                    "Resistant to: " + pokemon.getResistant() + "\n"
//                    "Buddy distance: " + pokemon.getBuddyDistance() + "\n"
            );
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
