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
        commandEvent.reply(
                "To register a new raid:\n!raid new [Pokemon] [Ends at (HH:MM)] [Pokestop/gym name]\n\n" +
                "Check status for a raid in a gym:\n!raid status [Pokestop/gym name]\n\n" +
                "Get a list of all active raids:\n!raid list\n\n" +
                "Get map link for a certain gym:\n!raid map [Pokestop/gym name]\n\n" +
                "Sign up for a certain raid:\n!raid add [number of people] [ETA (HH:MM)] [Pokestop/gym name]\n\n" +
                "Unsign for a certain raid:\n!raid remove [Pokestop/gym name]\n\n" +
                "Info about the raid boss:\n!raid vs [Pokemon]");
    }
}
