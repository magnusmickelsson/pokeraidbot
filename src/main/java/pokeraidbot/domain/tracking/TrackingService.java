package pokeraidbot.domain.tracking;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.UserConfig;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TrackingService {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(TrackingService.class);
    private final LocaleService localeService;
    private final UserConfigRepository userConfigRepository;
    private final PokemonRepository pokemonRepository;
    private Set<PokemonTrackingTarget> trackingTargets = new ConcurrentSkipListSet<>();

    public TrackingService(LocaleService localeService,
                           UserConfigRepository userConfigRepository,
                           PokemonRepository pokemonRepository) {
        this.localeService = localeService;
        this.userConfigRepository = userConfigRepository;
        this.pokemonRepository = pokemonRepository;
    }

    public void notifyTrackers(Guild guild, Raid raid, Config configForServer, User user, String rawMessage) {
        Validate.notNull(guild, "Guild is null");
        Validate.notNull(raid, "Raid is null");
        Validate.notNull(configForServer, "Config is null");
        Validate.notNull(user, "User is null");
        final Set<PokemonTrackingTarget> trackingTargets = getTrackingTargets();
        for (TrackingTarget t : trackingTargets) {
            if (t.canHandle(configForServer, user, raid, guild)) {
                try {
                    t.handle(guild, localeService, configForServer, user, raid, rawMessage);
                } catch (Throwable e) {
                    LOGGER.debug("Could not handle tracking message for server " + configForServer.getServer() +
                            " and target " +
                            "" + t + " due to an exception: " + e.getMessage());
                }
            }
        }
    }

    public Set<PokemonTrackingTarget> getTrackingTargets() {
        if (trackingTargets.size() == 0) {
            // We most likely had a server restart, load saved tracking from database
            for (UserConfig config : userConfigRepository.findAll()) {
                if (config.getTracking1() != null) {
                    trackingTargets.add(new PokemonTrackingTarget(config.getId(),
                            pokemonRepository.search(config.getTracking1(), null)));
                }
                if (config.getTracking2() != null) {
                    trackingTargets.add(new PokemonTrackingTarget(config.getId(),
                            pokemonRepository.search(config.getTracking2(), null)));
                }
                if (config.getTracking3() != null) {
                    trackingTargets.add(new PokemonTrackingTarget(config.getId(),
                            pokemonRepository.search(config.getTracking3(), null)));
                }
            }
        }
        return trackingTargets;
    }

    public void clearCache() {
        trackingTargets = new ConcurrentSkipListSet<>();
    }

    public void add(Pokemon pokemon, User user, Config config) {
        Validate.notNull(pokemon, "Pokemon");
        Validate.notNull(user, "User");
        Validate.notNull(config, "Config");

        PokemonTrackingTarget trackingTarget = new PokemonTrackingTarget(user.getId(), pokemon);
        if (getTrackingTargets().contains(trackingTarget)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TRACKING_EXISTS,
                    localeService.getLocaleForUser(user)));
        }
        addToDbAndCollection(trackingTarget, user, config);
    }

    private void addToDbAndCollection(PokemonTrackingTarget trackingTarget, User user, Config config) {
        UserConfig userConfig = userConfigRepository.findOne(user.getId());
        if (userConfig != null) {
            if (userConfig.hasFreeTrackingSpot()) {
                userConfig.setNextTrackingSpot(trackingTarget.getPokemon());
            } else {
                throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TRACKING_NONE_FREE,
                        localeService.getLocaleForUser(user)));
            }
        } else {
            // Per default, let user have the same locale as the server
            userConfig = new UserConfig(user.getId(), trackingTarget.getPokemon(), null, null,
                    config.getLocale());
        }
        userConfigRepository.save(userConfig);
        trackingTargets.add(trackingTarget);
    }

    public void removeForUser(PokemonTrackingTarget trackingTarget, User user) {
        if (!getTrackingTargets().contains(trackingTarget)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TRACKING_NOT_EXISTS,
                    localeService.getLocaleForUser(user)));
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

    public void removeAllForUser(User user) {
        trackingTargets.forEach(t -> {
            if (t.getUserId().equals(user.getId())) {
                removeForUser(t, user);
            }
        });
    }
}
