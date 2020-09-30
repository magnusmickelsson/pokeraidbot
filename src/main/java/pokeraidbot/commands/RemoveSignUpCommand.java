package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Locale;

public class RemoveSignUpCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public RemoveSignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.name = "remove";
        this.help = localeService.getMessageFor(LocaleService.REMOVE_SIGNUP_HELP, LocaleService.DEFAULT);
        this.aliases = new String[]{"unsign", "done"};
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final Locale localeForUser = localeService.getLocaleForUser(user);
        String gymName = commandEvent.getArgs();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        final SignUp removed = raid.remove(user, raidRepository);
        if (removed != null) {
            commandEvent.reactSuccess();
            removeOriginMessageIfConfigSaysSo(config, commandEvent);
        } else {
            final String message =
                    localeService.getMessageFor(LocaleService.NO_SIGNUP_AT_GYM, localeForUser, userName, gym.getName());
            replyErrorBasedOnConfig(config, commandEvent, new UserMessedUpException(user, message));
        }
    }
}
