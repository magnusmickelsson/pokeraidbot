package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;

public class SignUpCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public SignUpCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

    }
}
