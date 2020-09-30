package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRaidInfo;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

/**
 * !raid hatch [Pokemon] [Pokestop name]
 */
public class EggHatchedCommand extends ConfigAwareCommand {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(EggHatchedCommand.class);
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final PokemonRaidStrategyService raidStrategyService;

    public EggHatchedCommand(GymRepository gymRepository, RaidRepository raidRepository,
                             PokemonRepository pokemonRepository, LocaleService localeService,
                             ServerConfigRepository serverConfigRepository,
                             CommandListener commandListener,
                             PokemonRaidStrategyService raidStrategyService) {
        super(serverConfigRepository, commandListener, localeService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.raidStrategyService = raidStrategyService;
        this.name = "hatch";
        this.aliases = new String[]{"h"};
        this.help = localeService.getMessageFor(LocaleService.EGG_HATCH_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final String[] args = commandEvent.getArgs().split(" ");
        if (args.length < 2) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user),
                            "!raid hatch Ho-Oh solna platform"));
        }
        String pokemonName = args[0].trim().toLowerCase();
        final String[] gymArguments = ArrayUtils.removeAll(args, 0);
        String gymName = StringUtils.join(gymArguments, " ");
        final String region = config.getRegion();
        final Gym gym = gymRepository.search(user, gymName, region);
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, region, user);

        final Pokemon pokemon = pokemonRepository.search(pokemonName, user);
        final PokemonRaidInfo existingRaidInfo = raidStrategyService.getRaidInfo(raid.getPokemon());
        final PokemonRaidInfo hatchRaidInfo = raidStrategyService.getRaidInfo(pokemon);
        // We allow null hatch info if it's a new boss that doesn't have tier/counter data yet
        final int newBossTier = hatchRaidInfo != null ? hatchRaidInfo.getBossTier() : existingRaidInfo.getBossTier();

        if (!raid.getPokemon().isEgg()) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.EGG_ALREADY_HATCHED,
                            localeService.getLocaleForUser(user), raid.getPokemon().toString()));
        }
        // Mega raids could be hatched to whatever in the future so be more lenient here
        if (pokemon.isEgg() || (existingRaidInfo != null && existingRaidInfo.getBossTier() != 6 && newBossTier != existingRaidInfo.getBossTier())) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.EGG_WRONG_TIER,
                    localeService.getLocaleForUser(user)));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to hatch raid " + raid + " into " + pokemonName);
        }
        AlterRaidCommand.changePokemon(gymRepository, localeService, pokemonRepository, raidRepository,
                commandEvent, config, user, userName, pokemonName,
                gymArguments);
    }
}
