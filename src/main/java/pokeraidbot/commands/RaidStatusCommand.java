package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.SignUp;

import java.util.Set;

public class RaidStatusCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.name = "status";
        this.help = "Check status for raid - !raid status [Pokestop/Gym name].";

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            final String[] args = commandEvent.getArgs().split(" ");
            // todo: error handling
            String gymName = args[0];
            final Gym gym = gymRepository.findByName(gymName);
            final Raid raid = raidRepository.getRaid(gym);
            final Set<SignUp> signUps = raid.getSignUps();
            final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
            commandEvent.reply("Status for raid at " + gymName + ":");
            commandEvent.reply("Pokemon: " + raid.getPokemon());
            commandEvent.reply("Ends at: " + raid.getEndOfRaid());
            commandEvent.reply(numberOfPeople + " signed up.");
            commandEvent.reply("" + signUps);
        } catch (Throwable e) {
            commandEvent.reply(e.getMessage());
        }
    }
}
