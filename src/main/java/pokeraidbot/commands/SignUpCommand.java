package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Locale;

/**
 * !raid add [number of people] [due time (HH:MM)] [Pokestop name]
 */
public class SignUpCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public SignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                         ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.name = "add";
        this.help = localeService.getMessageFor(LocaleService.SIGNUP_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
        this.aliases = new String[]{"signup"};
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final Locale localeForUser = localeService.getLocaleForUser(user);
        final String[] args = Utils.prepareArguments(commandEvent);
        if (args.length < 3) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.BAD_SYNTAX,
                    localeService.getLocaleForUser(user), "!raid add 1 10:00 solna platform " +
                            "(alt. *+1 10:00 solna platform*)"));
        }

        final String returnMessage = raidRepository.executeSignUpCommand(config, user, localeForUser, args, help);
        replyBasedOnConfigAndRemoveAfter(config, commandEvent, returnMessage,
                BotServerMain.timeToRemoveFeedbackInSeconds);
    }
}
