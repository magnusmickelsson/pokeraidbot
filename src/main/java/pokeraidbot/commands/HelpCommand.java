package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class HelpCommand extends Command {
    public HelpCommand() {
        this.name = "usage";
        this.help = "Shows usage of bot.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.reply("To register a new raid: !raid new [Pokemon] [Ends at (HH:MM)] [Pokestop/gym name]\n" +
                "Check status for a raid in a certain gym: !raid status [Pokestop name]\n" +
                "Get a list of all active raids: !raid list\n" +
                "Get map link for a certain gym: !raid map [Pokestop name]\n" +
                "Sign up for a certain raid (ETA = Estimated Time of Arrival): !raid add [number of people] [ETA (HH:MM)] [Pokestop/gym name]\n" +
                "Unsign your signups for a certain raid: !raid remove [Pokestop/gym name]\n" +
                "To see information about the raid boss you're facing: !raid vs [Pokemon].");
    }
}
