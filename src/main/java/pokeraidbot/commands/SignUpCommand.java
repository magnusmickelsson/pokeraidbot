package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.WrongNumberOfArgumentsException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import static pokeraidbot.Utils.assertEtaNotAfterRaidEnd;
import static pokeraidbot.Utils.assertSignupTimeNotBeforeNow;

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
        final String userName = user.getName();
        final Locale localeForUser = localeService.getLocaleForUser(userName);
        final String[] args = commandEvent.getArgs().split(" ");
        String people = args[0];
        if (args.length < 3 || args.length > 10) {
            throw new WrongNumberOfArgumentsException(userName, localeService, 3, args.length, this.help);
        }
        Integer numberOfPeople = Utils.assertNotTooManyOrNoNumber(user, localeService, people);

        String timeString = args[1];

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(userName, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());

        LocalTime eta = Utils.parseTime(user, timeString);
        LocalDateTime realEta = LocalDateTime.of(raid.getEndOfRaid().toLocalDate(), eta);

        assertEtaNotAfterRaidEnd(user, raid, realEta, localeService);
        assertSignupTimeNotBeforeNow(user, realEta, localeService);

        raid.signUp(user, numberOfPeople, eta, raidRepository);
        final String currentSignupText = localeService.getMessageFor(LocaleService.CURRENT_SIGNUPS, localeForUser);
        final String signUpText = raid.getSignUps().size() > 1 ? currentSignupText + "\n" + raid.getSignUps() : "";
        replyBasedOnConfig(config, commandEvent,
                localeService.getMessageFor(LocaleService.SIGNUPS, localeForUser, userName,
                gym.getName(), signUpText));
    }
}
