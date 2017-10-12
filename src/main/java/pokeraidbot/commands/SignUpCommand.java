package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.Locale;

/**
 * !raid add [number of people] [due time (HH:MM)] [Pokestop name]
 */
public class SignUpCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public SignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                         ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
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
        final String returnMessage = raidRepository.executeSignUpCommand(config, user, localeForUser, args, help);
        ConfigAwareCommand.replyBasedOnConfig(config, commandEvent, returnMessage);
    }
}
