package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static pokeraidbot.Utils.assertCreateRaidTimeNotBeforeNow;
import static pokeraidbot.Utils.assertTimeNotInNoRaidTimespan;

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
                            ServerConfigRepository serverConfigRepository,
                            CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.name = "ex";
        this.help = localeService.getMessageFor(LocaleService.NEW_EX_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final String[] args = commandEvent.getArgs().split(" ");
        String pokemonName = args[0];
        final Pokemon pokemon = pokemonRepository.getByName(pokemonName);
        String dateString = args[1];
        String timeString = args[2];
        LocalTime endsAtTime = Utils.parseTime(user, timeString, localeService);
        LocalDate endsAtDate = Utils.parseDate(user, dateString, localeService);
        LocalDateTime endsAt = LocalDateTime.of(endsAtDate, endsAtTime);

        assertTimeNotInNoRaidTimespan(user, endsAtTime, localeService);
        if (endsAtDate.isAfter(LocalDate.now().plusDays(7))) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.EX_DATE_LIMITS,
                    localeService.getLocaleForUser(user)));
        }
        assertCreateRaidTimeNotBeforeNow(user, endsAt, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = new Raid(pokemon, endsAt, gym, localeService, config.getRegion());
        raidRepository.newRaid(userName, raid);
        replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
                localeService.getLocaleForUser(user), raid.toString()));
    }
}
