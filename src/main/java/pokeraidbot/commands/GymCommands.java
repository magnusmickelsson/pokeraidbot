package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Temporary commands for gym management now that map sites have pokemon API issues
 * !raid gym {gymname};{latitude};{longitude};{exgym - true or false}
 * !raid gym get {gymname}
 */
public class GymCommands extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final GymRepository gymRepository;

    public GymCommands(LocaleService localeService,
                       ServerConfigRepository serverConfigRepository,
                       GymRepository gymRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.gymRepository = gymRepository;
        this.name = "gym";
        this.help = localeService.getMessageFor(LocaleService.LIST_HELP, LocaleService.DEFAULT);
        this.aliases = new String[]{"gyms"};
    }

    @Override
    protected void executeWithConfig(CommandEvent event, Config config) {
        final User user = event.getAuthor();
        final String eventArgs = event.getArgs();

        if (!isUserServerMod(event, config) && !isUserAdministrator(event)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_PERMISSION,
                    localeService.getLocaleForUser(user)));
        }
        if (eventArgs.startsWith("get ")) {
            String gymName = eventArgs.replaceAll("get\\s{1,3}", "").trim();
            final Gym gym = gymRepository.findByName(gymName, config.getRegion());
            replyBasedOnConfig(config, event, "Found gym: " + gym.toStringDetails());
        } else {
            String[] gymArgs = eventArgs.replaceAll("gym\\s{1,3}", "").trim().split(";");
            if (gymArgs == null || gymArgs.length < 4 || gymArgs.length > 4) {
                replyBasedOnConfig(config, event, "Bad syntax, should be: !raid gym add {gymname};{latitude};{longitude};{exgym true or false");
                return;
            } else {
                final Random random = new Random();
                random.setSeed(System.currentTimeMillis());
                Gym gym = new Gym(gymArgs[0], "" + random.nextInt(99999999), gymArgs[1], gymArgs[2],
                        config.getRegion(), Boolean.valueOf(gymArgs[3]));
                gymRepository.addTemporary(user, gym, config.getRegion());
                replyBasedOnConfig(config, event, "Gym added temporarily: " + gym.toStringDetails());
            }
        }
    }
}
