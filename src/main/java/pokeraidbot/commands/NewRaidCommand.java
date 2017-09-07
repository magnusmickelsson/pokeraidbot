package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.Utils;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.Pokemons;
import pokeraidbot.domain.Raid;

import java.time.LocalTime;

/**
 * !raid new [Pokemon] [Ends in (HH:MM)] [Pokestop name]
 */
public class NewRaidCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public NewRaidCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.name = "new";
        this.help = "Create new raid - !raid new [Name of Pokemon] [Ends at (HH:MM)] [Pokestop/Gym name].";

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
            LocalTime endsAt = LocalTime.parse(timeString, Utils.dateTimeFormatter);
            StringBuilder gymNameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                gymNameBuilder.append(args[i]).append(" ");
            }
            String gymName = gymNameBuilder.toString().trim();
            final Raid raid = new Raid(pokemon, endsAt, gymRepository.findByName(gymName));
            raidRepository.newRaid(commandEvent.getAuthor().getId(), raid);
            commandEvent.reply("Raid created: " + raid);
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
