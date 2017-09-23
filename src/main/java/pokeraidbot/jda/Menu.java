package pokeraidbot.jda;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import pokeraidbot.BotService;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class Menu extends ListenerAdapter {
    /**
     * The controls associated with this menu.
     */
    private final Control[] controls;

    /**
     * The message for the menu to hook onto.
     */
    protected final Message message;
    /**
     * The central Launcher instance.
     */
    private final BotService botService;

    /**
     * The timeout of this message.
     * If the message isn't interacted with for a certain period of time, it will self-delete.
     */
    private final ScheduledExecutorService messageTimeout;
    /**
     * The future of the message timeout.
     * This can be cancelled and refresh when interacted with.
     */
    private ScheduledFuture<?> timeoutFuture;

    public Menu(BotService botService, Message message, Control... controls) {
        this.botService = botService;
        this.controls = controls;
        this.message = message;
        this.messageTimeout = new ScheduledThreadPoolExecutor(1);
        botService.getBot().addEventListener(this);
        addControls();
    }

    /**
     * Adds one control to the message.
     * This is done recursively as to maintain the order of the controls.
     *
     * @param i The index of the control to add
     */
    private void addControl(int i) {
        if (i >= controls.length) {
            refreshTimeout();
            return;
        }

        message.addReaction(controls[i].getUnicode()).queue((success) -> addControl(i + 1));
    }

    /**
     * Adds all controls which need to be in the menu.
     * It does so recursively as to maintain the order of the controls.
     */
    private void addControls() {
        addControl(0);
    }

    /**
     * Destroys the menu and the message along with it.
     */
    protected void destroy() {
        // Stop any message timeouts to prevent deletion after the message has been deleted
        timeoutFuture.cancel(true);
        messageTimeout.shutdown();

        // Prevent any clicks from activating the menu again
        botService.getBot().removeEventListener(this);
        // Delete the message
        message.delete().queue();
    }

    /**
     * Performs an action using the control which was interacted with.
     * @param control The clicked control
     */
    protected abstract void doActionWith(Control control);

    /**
     * Listens for reaction additions and sees if it was a control.
     *
     * @param ev The event that made this method trigger
     */
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent ev) {
        // If the bot added any reactions, don't respond to them
        if (ev.getUser().equals(botService.getBot().getSelfUser())) return;

        // Remove the reaction which was added in this event.
        // Do this with a slight delay to prevent graphical glitches client side.
        ev.getReaction().removeReaction(ev.getUser()).queueAfter(50, TimeUnit.MILLISECONDS);

        // If the one adding a reaction wasn't the command issuer, don't do anything
//        if (!ev.getUser().equals(CommandParser.getLastEvent().getAuthor())) return;

        // Figure out which control was clicked
        Control selectedOption = Arrays.stream(controls)
                .filter(c -> c.getUnicode().equals(ev.getReaction().getEmote().getName()))
                .findFirst().orElse(null);

        // If any control was detected, do things appropriately
        if (selectedOption != null) {
            if (selectedOption == Control.STOP) {
                destroy();
            } else {
                // Call the menu specific response method
                doActionWith(selectedOption);
                // Refresh the message's timeout to prevent preemptive deletion
                refreshTimeout();
            }
        }
    }

    /**
     * Refreshes the message timeout when neccessary.
     * This means that, when this method is called, the timeout period is refreshed preventing the message from being deleted.
     */
    private void refreshTimeout() {
        if (!messageTimeout.isShutdown()) {
            if (timeoutFuture != null && !timeoutFuture.isDone()) {
                timeoutFuture.cancel(true);
            }

            timeoutFuture = messageTimeout.schedule(() -> {
                destroy();
                messageTimeout.shutdown();
            }, 60l, TimeUnit.SECONDS);
        }
    }
}