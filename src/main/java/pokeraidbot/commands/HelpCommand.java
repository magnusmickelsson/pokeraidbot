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
        commandEvent.reply("To register a new raid: !raid new [Pokemon] [Ends in (HH:MM)] [Pokestop name]");
        commandEvent.reply("List all raids for a certain Pokemon: !raid listfor [Pokemon]");
        commandEvent.reply("Check status for a raid in a certain Pokestop: !raid status [Pokestop name]");
        commandEvent.reply("Sign up for a certain raid: !raid add [number of people] [due time (HH:MM)] [Pokestop name]");
        commandEvent.reply("Unsign your signups for a certain raid: !raid remove [Pokestop name]");
    }
}
