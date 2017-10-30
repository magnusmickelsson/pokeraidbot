package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.infrastructure.jpa.config.Config;

public interface TrackingCommandListener extends CommandListener {
    void add(PokemonTrackingTarget trackingTarget, User user, Config config);

    void remove(PokemonTrackingTarget trackingTarget, User user);

    void removeAll(User user);
}
