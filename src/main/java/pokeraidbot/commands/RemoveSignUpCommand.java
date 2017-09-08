package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.SignUp;

public class RemoveSignUpCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public RemoveSignUpCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
        this.name = "remove";
        this.help = "!raid remove [gym] - remove signups for this gym.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String gymName = commandEvent.getArgs();
        final Gym gym = gymRepository.findByName(gymName);
        final Raid raid = raidRepository.getRaid(gym);
        final String user = commandEvent.getAuthor().getName();
        final SignUp removed = raid.remove(user);
        if (removed != null) {
            commandEvent.reply("Signup removed for gym " + gym.getName() + ": " + removed);
        } else {
            commandEvent.reply(user + " had no signup to remove for gym " + gym.getName());
        }
    }
}
