package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Locale;
import java.util.Set;

/**
 * !raid list-ex
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
        stringBuilder.append(localeService.getMessageFor(LocaleService.ALL_EX, locale))
                .append(config.getRegion()).append(":\n\n");
        final Set<String> exGyms = gymRepository.getExGyms(config.getRegion());
        for (String gym : exGyms) {
            stringBuilder.append(gym).append("\n");
        }
        replyBasedOnConfig(config, commandEvent, stringBuilder.toString());
    }
}
