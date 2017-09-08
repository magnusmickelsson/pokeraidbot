package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.Utils;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;

import static pokeraidbot.Utils.assertEtaNotAfterRaidEnd;
import static pokeraidbot.Utils.assertGivenTimeNotBeforeNow;

/**
 * !raid add [number of people] [due time (HH:MM)] [Pokestop name]
 */
public class SignUpCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private static final int highLimitForSignUps = 20;

    public SignUpCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.name = "add";
        this.help = "Sign up for a raid: !raid add [number of people] [ETA (HH:MM)] [Pokestop name]";
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            final String userName = commandEvent.getAuthor().getName();
            final String[] args = commandEvent.getArgs().split(" ");
            // todo: error handling
            String people = args[0];
            Integer numberOfPeople;
            try {
                numberOfPeople = new Integer(people);
                if (numberOfPeople < 1 || numberOfPeople > highLimitForSignUps) {
                    throw new RuntimeException();
                }
            } catch (RuntimeException e) {
                throw new UserMessedUpException(userName, "Can't parse this number of people: " + people + " - give a valid number 1-" + highLimitForSignUps + ".");
            }

            String timeString = args[1];

            StringBuilder gymNameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                gymNameBuilder.append(args[i]).append(" ");
            }
            String gymName = gymNameBuilder.toString().trim();
            final Gym gym = gymRepository.findByName(gymName);
            final Raid raid = raidRepository.getRaid(gym);

            // todo: handle different separators
            // todo: time checking
            LocalTime eta = LocalTime.parse(timeString, Utils.dateTimeFormatter);

            assertEtaNotAfterRaidEnd(userName, raid, eta);
            assertGivenTimeNotBeforeNow(userName, eta);

            raid.signUp(userName, numberOfPeople, eta);
            commandEvent.reply(userName + " sign up added to raid at " + gym.getName() +
                    ". " + (raid.getSignUps().size() > 1 ? "Current signups: \n" + raid.getSignUps() : ""));
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }

}
