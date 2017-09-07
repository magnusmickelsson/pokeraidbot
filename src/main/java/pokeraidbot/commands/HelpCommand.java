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
        commandEvent.reply("To register a new raid: !raid new [Pokemon] [Ends at (HH:MM)] [Pokestop name]\n" +
                "Check status for a raid in a certain gym: !raid status [Pokestop name]\n" +
                "Get map link for a certain gym: !raid map [Pokestop name]\n" +
                "Sign up for a certain raid: !raid add [number of people] [ETA (HH:MM)] [Pokestop name]\n" +
                "Unsign your signups for a certain raid: !raid remove [Pokestop name]\n" +
                "Note: bot is currently in development and may not work ok. Only two pokemons added; Tyranitar and Entei.");
    }
}
