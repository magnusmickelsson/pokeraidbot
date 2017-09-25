package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pokeraidbot.domain.Config;
import pokeraidbot.domain.ConfigRepository;
import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

// todo: maybe this doesn't have to be a command listener
public class TrackingCommandListener implements CommandListener {
    private final ConfigRepository configRepository;
    private final LocaleService localeService;
    private final Set<PokemonTrackingTarget> trackingTargets = new ConcurrentSkipListSet<>();

    public TrackingCommandListener(ConfigRepository configRepository,
                                   LocaleService localeService) {
        this.configRepository = configRepository;
        this.localeService = localeService;
    }

    @Override
    public void onCommand(CommandEvent event, Command command) {

    }

    @Override
    public void onCompletedCommand(CommandEvent event, Command command) {
        for (TrackingTarget t : trackingTargets) {
            if (t.canHandle(event, command)) {
                final String serverName = event.getGuild().getName().toLowerCase();
                final Config configForServer = configRepository.getConfigForServer(serverName);
                final Locale localeForUser = localeService.getLocaleForUser(event.getAuthor().getName());
                t.handle(event, command, localeService, localeForUser, configForServer);
            }
        }
    }

    @Override
    public void onTerminatedCommand(CommandEvent event, Command command) {

    }

    @Override
    public void onNonCommandMessage(MessageReceivedEvent event) {

    }

    public void add(PokemonTrackingTarget trackingTarget, String userName) {
        if (trackingTargets.contains(trackingTarget)) {
            throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.TRACKING_EXISTS,
                    localeService.getLocaleForUser(userName), trackingTarget.toString()));
        }
        trackingTargets.add(trackingTarget);
    }

    public void remove(PokemonTrackingTarget trackingTarget, String userName) {
        if (!trackingTargets.contains(trackingTarget)) {
            throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.TRACKING_NOT_EXISTS,
                    localeService.getLocaleForUser(userName), trackingTarget.toString()));
        }
        trackingTargets.remove(trackingTarget);
    }

    public void removeAll(String userId) {
        trackingTargets.forEach(t -> {if (t.getUserId().equals(userId)){trackingTargets.remove(t);}});
    }
}
