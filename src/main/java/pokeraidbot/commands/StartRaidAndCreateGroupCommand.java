package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
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
import java.util.concurrent.ExecutorService;

import static pokeraidbot.Utils.assertTimeNotInNoRaidTimespan;
import static pokeraidbot.commands.NewRaidGroupCommand.createRaidGroup;

/**
 * !raid start-group [pokemon] [start raid and group at (HH:MM)] [Pokestop name]
 */
public class StartRaidAndCreateGroupCommand extends ConcurrencyAndConfigAwareCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartRaidAndCreateGroupCommand.class);

    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final BotService botService;
    private final ClockService clockService;
    private final PokemonRaidStrategyService pokemonRaidStrategyService;

    public StartRaidAndCreateGroupCommand(GymRepository gymRepository, RaidRepository raidRepository,
                                          PokemonRepository pokemonRepository, LocaleService localeService,
                                          ServerConfigRepository serverConfigRepository,
                                          CommandListener commandListener, BotService botService,
                                          ClockService clockService, ExecutorService executorService,
                                          PokemonRaidStrategyService pokemonRaidStrategyService) {
        super(serverConfigRepository, commandListener, localeService, executorService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.botService = botService;
        this.clockService = clockService;
        this.pokemonRaidStrategyService = pokemonRaidStrategyService;
        this.name = "start-group";
        this.aliases = new String[]{"sg", "gs", "group-start"};
        this.help = localeService.getMessageFor(LocaleService.RAID_CREATE_AND_GROUP_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String[] args = commandEvent.getArgs().split(" ");
        final Locale locale = localeService.getLocaleForUser(user);
        if (args.length < 2) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.BAD_SYNTAX,
                    localeService.getLocaleForUser(user), "!raid start-group groudon 10:00 solna platform"));
        }

        String pokemonName = args[0].trim();
        Pokemon pokemon = pokemonRepository.search(pokemonName, user);

        String timeString = args[1].trim();
        LocalTime startAtTime = Utils.parseTime(user, timeString, localeService);

        assertTimeNotInNoRaidTimespan(user, startAtTime, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final String region = config.getRegion();
        final Gym gym = gymRepository.search(user, gymName, region);
        final Raid raid;
        if (!raidRepository.isActiveOrExRaidAt(gym, region)) {
            final LocalDateTime endOfRaid = LocalDateTime.of(LocalDate.now(),
                    startAtTime.plusMinutes(Utils.RAID_DURATION_IN_MINUTES));
            raid = raidRepository.newRaid(user, new Raid(pokemon, endOfRaid, gym, localeService, region, false),
                    commandEvent.getGuild(), config, commandEvent.getMessage().getContentRaw());
        } else {
            raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, region, user);
        }
        createRaidGroup(commandEvent.getChannel(), commandEvent.getGuild(),
                config, user, locale, startAtTime, raid.getId(),
                localeService, raidRepository, botService, serverConfigRepository, pokemonRepository, gymRepository,
                clockService, executorService, pokemonRaidStrategyService);
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }
}
