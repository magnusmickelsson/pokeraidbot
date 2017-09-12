package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.GymRepository;
import pokeraidbot.domain.RaidRepository;
import pokeraidbot.Utils;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;
import java.util.Locale;

import static pokeraidbot.Utils.assertEtaNotAfterRaidEnd;
import static pokeraidbot.Utils.assertGivenTimeNotBeforeNow;

/**
 * !raid add [number of people] [due time (HH:MM)] [Pokestop name]
 */
public class SignUpCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private static final int highLimitForSignUps = 20;
    private final LocaleService localeService;

    public SignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService) {
        this.localeService = localeService;
        this.name = "add";
        this.help = localeService.getMessageFor(LocaleService.SIGNUP_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            final String userName = commandEvent.getAuthor().getName();
            final Locale localeForUser = localeService.getLocaleForUser(userName);
            final String[] args = commandEvent.getArgs().split(" ");
            String people = args[0];
            Integer numberOfPeople;
            try {
                numberOfPeople = new Integer(people);
                if (numberOfPeople < 1 || numberOfPeople > highLimitForSignUps) {
                    throw new RuntimeException();
                }
            } catch (RuntimeException e) {
                throw new UserMessedUpException(userName,
                        localeService.getMessageFor(LocaleService.ERROR_PARSE_PLAYERS, localeForUser, people, String.valueOf(highLimitForSignUps)));
            }

            String timeString = args[1];

            StringBuilder gymNameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                gymNameBuilder.append(args[i]).append(" ");
            }
            String gymName = gymNameBuilder.toString().trim();
            final Gym gym = gymRepository.search(userName, gymName);
            final Raid raid = raidRepository.getRaid(gym);

            LocalTime eta = LocalTime.parse(timeString, Utils.dateTimeParseFormatter);

            assertEtaNotAfterRaidEnd(userName, raid, eta, localeService);
            assertGivenTimeNotBeforeNow(userName, eta, localeService);

            raid.signUp(userName, numberOfPeople, eta, raidRepository);
            final String currentSignupText = localeService.getMessageFor(LocaleService.CURRENT_SIGNUPS, localeForUser);
            final String signUpText = raid.getSignUps().size() > 1 ? currentSignupText + "\n" + raid.getSignUps() : "";
            commandEvent.reply(localeService.getMessageFor(LocaleService.SIGNUPS, localeForUser, userName, gym.getName(), signUpText));
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }

}
