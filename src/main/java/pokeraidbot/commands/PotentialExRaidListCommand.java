package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.collections4.CollectionUtils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * !raid potential-ex
 */
public class PotentialExRaidListCommand extends ConfigAwareCommand {
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final GymRepository gymRepository;

    public PotentialExRaidListCommand(RaidRepository raidRepository, LocaleService localeService,
                                      ServerConfigRepository serverConfigRepository,
                                      GymRepository gymRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.gymRepository = gymRepository;
        this.name = "potential-ex";
        this.help = localeService.getMessageFor(LocaleService.LIST_HELP, LocaleService.DEFAULT);
        this.aliases = new String[]{"maybe-ex"};
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final Locale locale = localeService.getLocaleForUser(user);
        Set<Raid> raids = raidRepository.getAllRaidsForRegion(config.getRegion());

        StringBuilder stringBuilder = new StringBuilder();
        // todo: i18n
        stringBuilder.append("Potentiella exgym som inte har en kommande EX-raid:\n\n");
//            stringBuilder.append("**").append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
//            if (args != null && args.length() > 0) {
//                stringBuilder.append(" (").append(args).append(")");
//            }
//            stringBuilder.append(":**");
//            stringBuilder.append("\n").append(localeService.getMessageFor(LocaleService.RAID_DETAILS,
//                    localeService.getLocaleForUser(user))).append("\n");
        Set<String> exGymNames = new HashSet<>();
        for (Raid raid : raids) {
            if (raid.isExRaid()) {
                exGymNames.add(raid.getGym().getName());
            }
        }
        final Set<String> exGyms = gymRepository.getExGyms(config.getRegion());
        final Collection<String> leftOverGyms = CollectionUtils.subtract(exGyms, exGymNames);
        for (String gym : leftOverGyms) {
            stringBuilder.append(gym).append("\n");
        }
        replyBasedOnConfig(config, commandEvent, stringBuilder.toString());
    }
}
