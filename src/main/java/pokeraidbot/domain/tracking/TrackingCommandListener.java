package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.Set;

public interface TrackingCommandListener extends CommandListener {
    Set<PokemonTrackingTarget> getTrackingTargets(String region);

    void clearCache();

    void add(PokemonTrackingTarget trackingTarget, User user, Config config);

    void remove(PokemonTrackingTarget trackingTarget, User user);

    void removeAll(User user);
}
