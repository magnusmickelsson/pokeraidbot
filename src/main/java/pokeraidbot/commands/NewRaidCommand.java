package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.GymRepository;
import pokeraidbot.domain.PokemonRepository;
import pokeraidbot.domain.RaidRepository;
import pokeraidbot.Utils;
import pokeraidbot.domain.LocaleService;
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
    private final LocaleService localeService;

    public NewRaidCommand(GymRepository gymRepository, RaidRepository raidRepository, PokemonRepository pokemonRepository, LocaleService localeService) {
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.name = "new";
        this.help = localeService.getMessageFor(LocaleService.NEW_RAID_HELP, LocaleService.DEFAULT);

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

            assertTimeNotInNoRaidTimespan(userName, endsAt, localeService);
            assertTimeNotMoreThanTwoHoursFromNow(userName, endsAt, localeService);
            assertGivenTimeNotBeforeNow(userName, endsAt, localeService);

            StringBuilder gymNameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                gymNameBuilder.append(args[i]).append(" ");
            }
            String gymName = gymNameBuilder.toString().trim();
            final Raid raid = new Raid(pokemon, endsAt, gymRepository.search(userName, gymName), localeService);
            raidRepository.newRaid(userName, raid);
            commandEvent.reply(localeService.getMessageFor(LocaleService.NEW_RAID_CREATED, localeService.getLocaleForUser(userName), raid.toString()));
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
