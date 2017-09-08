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
public class RaidListCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;

    public RaidListCommand(GymRepository gymRepository, RaidRepository raidRepository) {
        this.name = "list";
        this.help = "Check current raids - !raid list";

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            Set<Raid> raids = raidRepository.getAllRaids();
            if (raids.size() == 0) {
                commandEvent.reply("There are currently no active raids.");
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Current raids:\n");
                for (Raid raid : raids) {
                    final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                    stringBuilder.append(raid.getGym().getName()).append(" (")
                            .append(raid.getPokemon().getName()).append(") - ").append("Ends at: ")
                            .append(printTime(raid.getEndOfRaid())).append(". ").append(numberOfPeople)
                            .append(" signed up.\n");
                }
                commandEvent.reply(stringBuilder.toString());
            }
        } catch (Throwable e) {
            commandEvent.reply(e.getMessage());
        }
    }
}
