package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

/**
 * !raid hatch [Pokemon] [Pokestop name]
 */
// todo: refactor and clean up
public class EggHatchedCommand extends ConfigAwareCommand {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(EggHatchedCommand.class);
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final BotService botService;

    public EggHatchedCommand(GymRepository gymRepository, RaidRepository raidRepository,
                             PokemonRepository pokemonRepository, LocaleService localeService,
                             ServerConfigRepository serverConfigRepository,
                             CommandListener commandListener, BotService botService) {
        super(serverConfigRepository, commandListener, localeService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.botService = botService;
        this.name = "hatch";
        this.help = "Report a raid egg hatched, and into what.";//localeService.getMessageFor(LocaleService.CHANGE_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final String[] args = commandEvent.getArgs().split(" ");
        if (args.length != 2) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user)));
        }
        String pokemonName = args[0].trim().toLowerCase();

        AlterRaidCommand.changePokemon(this, gymRepository, localeService, pokemonRepository, raidRepository,
                botService.getTrackingCommandListener(), commandEvent, config, user, userName, pokemonName,
                ArrayUtils.removeAll(args, 0));
    }
}
