package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.Utils;
import pokeraidbot.domain.*;

import java.time.LocalTime;

import static pokeraidbot.Utils.*;

/**
 * !raid new [Pokemon] [Ends in (HH:MM)] [Pokestop name]
 */
public class NewRaidCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;

    public NewRaidCommand(GymRepository gymRepository, RaidRepository raidRepository,
                          PokemonRepository pokemonRepository, LocaleService localeService,
                          ConfigRepository configRepository) {
        super(configRepository);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.name = "new";
        this.help = localeService.getMessageFor(LocaleService.NEW_RAID_HELP, LocaleService.DEFAULT);

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
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
            final Gym gym = gymRepository.search(userName, gymName, config.region);
            final Raid raid = new Raid(pokemon, endsAt, gym, localeService, config.region);
            raidRepository.newRaid(userName, raid);
            commandEvent.reply(localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
                    localeService.getLocaleForUser(userName), raid.toString()));
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
