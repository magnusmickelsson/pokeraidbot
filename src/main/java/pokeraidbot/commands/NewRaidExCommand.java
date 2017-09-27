package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.Utils;
import pokeraidbot.domain.*;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static pokeraidbot.Utils.*;

/**
 * !raid new [Pokemon] [Ends at (yyyy-MM-dd HH:mm)] [Pokestop name]
 */
public class NewRaidExCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;

    public NewRaidExCommand(GymRepository gymRepository, RaidRepository raidRepository,
                            PokemonRepository pokemonRepository, LocaleService localeService,
                            ConfigRepository configRepository,
                            CommandListener commandListener) {
        super(configRepository, commandListener);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.name = "ex";
        this.help = localeService.getMessageFor(LocaleService.NEW_EX_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String userName = commandEvent.getAuthor().getName();
        final String[] args = commandEvent.getArgs().split(" ");
        String pokemonName = args[0];
        final Pokemon pokemon = pokemonRepository.getByName(pokemonName);
        String dateString = args[1];
        String timeString = args[2];
        LocalTime endsAtTime = LocalTime.parse(timeString, Utils.timeParseFormatter);
        LocalDate endsAtDate = LocalDate.parse(dateString);
        LocalDateTime endsAt = LocalDateTime.of(endsAtDate, endsAtTime);

        assertTimeNotInNoRaidTimespan(userName, endsAtTime, localeService);
        if (endsAtDate.isAfter(LocalDate.now().plusDays(7))) {
            // todo: i18n
            throw new UserMessedUpException(userName, "Du kan inte skapa en EX raid mer än 7 dagar framåt, då är det hittepå!");
        }
        assertCreateRaidTimeNotBeforeNow(userName, endsAt, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(userName, gymName, config.region);
        final Raid raid = new Raid(pokemon, endsAt, gym, localeService, config.region);
        raidRepository.newRaid(userName, raid);
        replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
                localeService.getLocaleForUser(userName), raid.toString()));
    }
}
