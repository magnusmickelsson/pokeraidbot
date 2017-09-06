package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.PokemonName;
import pokeraidbot.domain.Pokemons;
import pokeraidbot.domain.Raid;

import java.time.LocalTime;

public class NewRaidCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public NewRaidCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.name = "new";
        this.help = "Create new raid - !raid new [Name of Pokemon] [Ends in (hours:minutes)] [Pokestop/Gym name].";

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            final String[] args = commandEvent.getArgs().split(" ");
            // todo: error handling
            String pokemonName = args[0];
            final Pokemon pokemon = Pokemons.valueOf(pokemonName.toUpperCase()).getPokemon();
            String timeString = args[1];
            // todo: handle different separators
            // todo: time checking
            final String[] timeStringSplit = timeString.split(":");
            Integer hours = new Integer(timeStringSplit[0]);
            Integer minutes = new Integer(timeStringSplit[1]);
            LocalTime endsAt = LocalTime.now().plusHours(hours).plusMinutes(minutes);
            String gymName = args[2];
            final Raid raid = new Raid(pokemon, endsAt, gymRepository.findByName(gymName));
            raidRepository.newRaid(commandEvent.getAuthor().getId(), raid);
            commandEvent.reply("Raid created: " + raid);
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
