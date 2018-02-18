package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.collections4.CollectionUtils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * !raid potential-ex
 */
public class ExRaidListCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final GymRepository gymRepository;

    public ExRaidListCommand(LocaleService localeService,
                             ServerConfigRepository serverConfigRepository,
                             GymRepository gymRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.gymRepository = gymRepository;
        this.name = "list-ex";
        this.help = localeService.getMessageFor(LocaleService.LIST_HELP, LocaleService.DEFAULT);
        this.aliases = new String[]{"listex", "ex-list", "exgyms"};
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final Locale locale = localeService.getLocaleForUser(user);
        StringBuilder stringBuilder = new StringBuilder();
        // todo: i18n
        stringBuilder.append("Alla EX-gym för regionen ").append(config.getRegion()).append(":\n\n");
//            stringBuilder.append("**").append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
//            if (args != null && args.length() > 0) {
//                stringBuilder.append(" (").append(args).append(")");
//            }
//            stringBuilder.append(":**");
//            stringBuilder.append("\n").append(localeService.getMessageFor(LocaleService.RAID_DETAILS,
//                    localeService.getLocaleForUser(user))).append("\n");
        final Set<String> exGyms = gymRepository.getExGyms(config.getRegion());
        for (String gym : exGyms) {
            stringBuilder.append(gym).append("\n");
        }
        replyBasedOnConfig(config, commandEvent, stringBuilder.toString());
    }
}
