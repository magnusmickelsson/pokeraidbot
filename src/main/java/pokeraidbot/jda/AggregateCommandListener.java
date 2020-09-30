package pokeraidbot.jda;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collection;
import java.util.LinkedList;

public class AggregateCommandListener implements CommandListener {
    private LinkedList<CommandListener> listeners = new LinkedList<>();

    public AggregateCommandListener(Collection<CommandListener> listeners) {
        this.listeners.addAll(listeners);
    }

    @Override
    public void onCommand(CommandEvent event, Command command) {
        for (CommandListener listener : listeners) {
            listener.onCommand(event, command);
        }
    }

    @Override
    public void onCompletedCommand(CommandEvent event, Command command) {
        for (CommandListener listener : listeners) {
            listener.onCompletedCommand(event, command);
        }
    }

    @Override
    public void onTerminatedCommand(CommandEvent event, Command command) {
        for (CommandListener listener : listeners) {
            listener.onTerminatedCommand(event, command);
        }
    }

    @Override
    public void onNonCommandMessage(MessageReceivedEvent event) {
        for (CommandListener listener : listeners) {
            listener.onNonCommandMessage(event);
        }
    }
}
