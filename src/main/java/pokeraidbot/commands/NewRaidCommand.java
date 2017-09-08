package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.PokemonRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.Utils;
import pokeraidbot.domain.Pokemon;
import pokeraidbot.domain.Raid;

import java.time.LocalTime;

import static pokeraidbot.Utils.assertGivenTimeNotBeforeNow;
import static pokeraidbot.Utils.assertTimeNotInNoRaidTimespan;
import static pokeraidbot.Utils.assertTimeNotMoreThanTwoHoursFromNow;

/**
 * !raid new [Pokemon] [Ends in (HH:MM)] [Pokestop name]
 */
public class NewRaidCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;

    public NewRaidCommand(GymRepository gymRepository, RaidRepository raidRepository, PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
        this.name = "new";
        this.help = "Create new raid - !raid new [Name of Pokemon] [Ends at (HH:MM)] [Pokestop/Gym name].";

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            final String userName = commandEvent.getAuthor().getName();
            final String[] args = commandEvent.getArgs().split(" ");
            String pokemonName = args[0];
            final Pokemon pokemon = pokemonRepository.getByName(pokemonName);
            String timeString = args[1];
            LocalTime endsAt = LocalTime.parse(timeString, Utils.dateTimeParseFormatter);

            assertTimeNotMoreThanTwoHoursFromNow(userName, endsAt);
            assertTimeNotInNoRaidTimespan(userName, endsAt);
            assertGivenTimeNotBeforeNow(userName, endsAt);

            StringBuilder gymNameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                gymNameBuilder.append(args[i]).append(" ");
            }
            String gymName = gymNameBuilder.toString().trim();
            final Raid raid = new Raid(pokemon, endsAt, gymRepository.search(userName, gymName));
            raidRepository.newRaid(userName, raid);
            commandEvent.reply("Raid created: " + raid);
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
