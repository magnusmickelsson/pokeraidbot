package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                          ConfigRepository configRepository,
                          CommandListener commandListener) {
        super(configRepository, commandListener);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.name = "new";
        this.help = localeService.getMessageFor(LocaleService.NEW_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String userName = commandEvent.getAuthor().getName();
        final String[] args = commandEvent.getArgs().split(" ");
        String pokemonName = args[0];
        final Pokemon pokemon = pokemonRepository.getByName(pokemonName);
        String timeString = args[1];
        LocalTime endsAtTime = LocalTime.parse(timeString, Utils.timeParseFormatter);
        LocalDateTime endsAt = LocalDateTime.of(LocalDate.now(), endsAtTime);

        assertTimeNotInNoRaidTimespan(userName, endsAtTime, localeService);
        assertTimeNotMoreThanTwoHoursFromNow(userName, endsAtTime, localeService);
        assertCreateRaidTimeNotBeforeNow(userName, endsAt, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(userName, gymName, config.getRegion());
        final Raid raid = new Raid(pokemon, endsAt, gym, localeService, config.getRegion());
        raidRepository.newRaid(userName, raid);
        replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
                localeService.getLocaleForUser(userName), raid.toString()));
    }
}
