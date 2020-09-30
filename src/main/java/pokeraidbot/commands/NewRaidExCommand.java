package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import static pokeraidbot.Utils.*;

/**
 * Create EX raid
 * !raid ex [Pokemon] [Ends at (yyyy-MM-dd HH:mm)] [Pokestop name]
 */
public class NewRaidExCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final PokemonRaidStrategyService strategyService;

    public NewRaidExCommand(GymRepository gymRepository, RaidRepository raidRepository,
                            PokemonRepository pokemonRepository, LocaleService localeService,
                            ServerConfigRepository serverConfigRepository,
                            CommandListener commandListener, PokemonRaidStrategyService strategyService) {
        super(serverConfigRepository, commandListener, localeService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.strategyService = strategyService;
        this.name = "ex";
        this.help = localeService.getMessageFor(LocaleService.NEW_EX_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String[] args = commandEvent.getArgs().replaceAll("\\s{1,3}", " ").split(" ");
        if (args.length < 3) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.BAD_SYNTAX,
                    localeService.getLocaleForUser(user), "!raid ex deoxys 2017-11-11 10:00 solna platform"));
        }
        String pokemonName = args[0];
        final Pokemon pokemon = pokemonRepository.search(pokemonName, user);
        final Locale locale = localeService.getLocaleForUser(user);
        if (!Utils.isRaidExPokemon(pokemon.getName(), strategyService, pokemonRepository)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NOT_EX_RAID,
                    locale, pokemonName));
        }

        String dateString = args[1];
        String timeString = args[2];
        LocalTime endsAtTime = Utils.parseTime(user, timeString, localeService);
        LocalDate endsAtDate = Utils.parseDate(user, dateString, localeService);
        LocalDateTime endsAt = LocalDateTime.of(endsAtDate, endsAtTime);

        assertTimeNotInNoRaidTimespan(user, endsAtTime, localeService);
        if (endsAtDate.isAfter(LocalDate.now().plusDays(20))) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.EX_DATE_LIMITS,
                    locale));
        }
        assertCreateRaidTimeNotBeforeNow(user, endsAt, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = new Raid(pokemon, endsAt, gym, localeService, config.getRegion(), true);
        if (!raid.isExRaid()) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NOT_EX_RAID,
                    locale, pokemonName));
        }
        raidRepository.newRaid(user, raid, commandEvent.getGuild(), config,
                "!raid ex " + raid.getPokemon().getName() + " " + printTimeIfSameDay(raid.getEndOfRaid()) +
        " " + gym.getName());
        replyBasedOnConfigAndRemoveAfter(config, commandEvent, localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
                locale, raid.toString(locale)), BotServerMain.timeToRemoveFeedbackInSeconds);
    }
}
