package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfig;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Transactional
public class TrackingCommandListenerBean implements TrackingCommandListener {
    private final ServerConfigRepository serverConfigRepository;
    private final LocaleService localeService;
    private final UserConfigRepository userConfigRepository;
    private final PokemonRepository pokemonRepository;
    private final Set<PokemonTrackingTarget> trackingTargets = new ConcurrentSkipListSet<>();

    public TrackingCommandListenerBean(ServerConfigRepository serverConfigRepository,
                                       LocaleService localeService,
                                       UserConfigRepository userConfigRepository, PokemonRepository pokemonRepository) {
        this.serverConfigRepository = serverConfigRepository;
        this.localeService = localeService;
        this.userConfigRepository = userConfigRepository;
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    public void onCommand(CommandEvent event, Command command) {

    }

    @Override
    public void onCompletedCommand(CommandEvent event, Command command) {
        final String serverName = event.getGuild().getName().toLowerCase();
        final Config configForServer = serverConfigRepository.getConfigForServer(serverName);
        final Locale localeForUser = localeService.getLocaleForUser(event.getAuthor());
        for (TrackingTarget t : getTrackingTargets(configForServer.getRegion())) {
            if (t.canHandle(event, command)) {
                t.handle(event, command, localeService, localeForUser, configForServer);
            }
        }
    }

    private Set<PokemonTrackingTarget> getTrackingTargets(String region) {
        if (trackingTargets.size() == 0) {
            // We most likely had a server restart, load saved tracking from database
            for (UserConfig config : userConfigRepository.findAll()) {
                if (config.getTracking1() != null) {
                    trackingTargets.add(new PokemonTrackingTarget(region, config.getId(),
                            pokemonRepository.getByName(config.getTracking1())));
                }
                if (config.getTracking2() != null) {
                    trackingTargets.add(new PokemonTrackingTarget(region, config.getId(),
                            pokemonRepository.getByName(config.getTracking2())));
                }
                if (config.getTracking3() != null) {
                    trackingTargets.add(new PokemonTrackingTarget(region, config.getId(),
                            pokemonRepository.getByName(config.getTracking3())));
                }
            }
        }
        return trackingTargets;
    }

    @Override
    public void onTerminatedCommand(CommandEvent event, Command command) {

    }

    @Override
    public void onNonCommandMessage(MessageReceivedEvent event) {

    }

    @Override
    public void add(PokemonTrackingTarget trackingTarget, User user) {
        if (getTrackingTargets(trackingTarget.getRegion()).contains(trackingTarget)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TRACKING_EXISTS,
                    localeService.getLocaleForUser(user)));//, trackingTarget.toString()));
        }
        addToDbAndCollection(trackingTarget, user);
    }

    private void addToDbAndCollection(PokemonTrackingTarget trackingTarget, User user) {
        UserConfig userConfig = userConfigRepository.findOne(user.getId());
        if (userConfig != null) {
            if (userConfig.hasFreeTrackingSpot()) {
                userConfig.setNextTrackingSpot(trackingTarget.getPokemon());
            } else {
                throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TRACKING_NONE_FREE,
                        localeService.getLocaleForUser(user)));
            }
        } else {
            userConfig = new UserConfig(user.getId(), trackingTarget.getPokemon(), null, null, null);
        }
        userConfigRepository.save(userConfig);
        trackingTargets.add(trackingTarget);
    }

    @Override
    public void remove(PokemonTrackingTarget trackingTarget, User user) {
        if (!getTrackingTargets(trackingTarget.getRegion()).contains(trackingTarget)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TRACKING_NOT_EXISTS,
                    localeService.getLocaleForUser(user)));//, trackingTarget.toString()));
        }
        removeFromDbAndCollection(trackingTarget, user);
    }

    private void removeFromDbAndCollection(PokemonTrackingTarget trackingTarget, User user) {
        UserConfig userConfig = userConfigRepository.findOne(user.getId());
        if (userConfig != null) {
            userConfig.removeTrackingFor(trackingTarget.getPokemon());
            userConfigRepository.save(userConfig);
        }
        trackingTargets.remove(trackingTarget);
    }

    @Override
    public void removeAll(User user) {
        trackingTargets.forEach(t -> {
            if (t.getUserId().equals(user.getId())) {
                remove(t, user);
            }
        });
    }
}
