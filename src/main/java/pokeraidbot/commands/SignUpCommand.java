package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.Utils;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;

import java.time.LocalTime;

/**
 * !raid add [number of people] [due time (HH:MM)] [Pokestop name]
 */
public class SignUpCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public SignUpCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.name = "add";
        this.help = "Sign up for a raid: !raid add [number of people] [ETA (HH:MM)] [Pokestop name]";
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            final String[] args = commandEvent.getArgs().split(" ");
            // todo: error handling
            String people = args[0];
            Integer numberOfPeople = new Integer(people);
            String timeString = args[1];
            // todo: handle different separators
            // todo: time checking
            LocalTime eta = LocalTime.parse(timeString, Utils.dateTimeFormatter);
            StringBuilder gymNameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                gymNameBuilder.append(args[i]).append(" ");
            }
            String gymName = gymNameBuilder.toString().trim();
            final Gym gym = gymRepository.findByName(gymName);
            final Raid raid = raidRepository.getRaid(gym);
            raid.signUp(commandEvent.getAuthor().getName(), numberOfPeople, eta);
            commandEvent.reply(commandEvent.getAuthor().getName() + " sign up added to raid at " + gym.getName() + ". Current signups: \n" + raid.getSignUps());
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
