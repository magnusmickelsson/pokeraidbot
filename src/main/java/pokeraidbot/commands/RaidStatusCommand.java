package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.SignUp;

import java.util.Set;

import static pokeraidbot.Utils.printTime;

/**
 * !raid status [Pokestop name]
 */
public class RaidStatusCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.name = "status";
        this.help = "Check status for raid - !raid status [Gym name].";

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            // todo: error handling
            String gymName = commandEvent.getArgs();
            final Gym gym = gymRepository.search(commandEvent.getAuthor().getName(), gymName);
            final Raid raid = raidRepository.getRaid(gym);
            final Set<SignUp> signUps = raid.getSignUps();
            final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
            commandEvent.reply("**Status for raid at " + gym.getName() + ":**\n" +
                    "Pokemon: " + raid.getPokemon() + "\n" +
                    "Ends at: " + printTime(raid.getEndOfRaid()) + "\n" +
                    numberOfPeople + " signed up." +
                    (signUps.size() > 0 ? "\n" + signUps : ""));
        } catch (Throwable e) {
            commandEvent.reply(e.getMessage());
        }
    }
}
