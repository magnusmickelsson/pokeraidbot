package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
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
    private static final int highLimitForSignUps = 20;
    private final LocaleService localeService;

    public SignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                         ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.name = "add";
        this.help = localeService.getMessageFor(LocaleService.SIGNUP_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String userName = commandEvent.getAuthor().getName();
        final Locale localeForUser = localeService.getLocaleForUser(userName);
        final String[] args = commandEvent.getArgs().split(" ");
        String people = args[0];
        // todo: check number of arguments
        Integer numberOfPeople;
        try {
            numberOfPeople = new Integer(people);
            if (numberOfPeople < 1 || numberOfPeople > highLimitForSignUps) {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.ERROR_PARSE_PLAYERS, localeForUser,
                            people, String.valueOf(highLimitForSignUps)));
        }

        String timeString = args[1];

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(userName, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());

        LocalTime eta = Utils.parseTime(userName, timeString);
        LocalDateTime realEta = LocalDateTime.of(raid.getEndOfRaid().toLocalDate(), eta);

        assertEtaNotAfterRaidEnd(userName, raid, realEta, localeService);
        assertSignupTimeNotBeforeNow(userName, realEta, localeService);

        raid.signUp(userName, numberOfPeople, eta, raidRepository);
        final String currentSignupText = localeService.getMessageFor(LocaleService.CURRENT_SIGNUPS, localeForUser);
        final String signUpText = raid.getSignUps().size() > 1 ? currentSignupText + "\n" + raid.getSignUps() : "";
        replyBasedOnConfig(config, commandEvent,
                localeService.getMessageFor(LocaleService.SIGNUPS, localeForUser, userName,
                gym.getName(), signUpText));
    }

}
