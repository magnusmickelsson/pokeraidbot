package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.tracking.TrackingService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

/**
 * !raid track [pokemon name]
 */
public class TrackPokemonCommand extends ConfigAwareCommand {
    private final PokemonRepository pokemonRepository;
    private final TrackingService trackingService;

    public TrackPokemonCommand(ServerConfigRepository serverConfigRepository, LocaleService localeService,
                               PokemonRepository pokemonRepository, TrackingService trackingService,
                               CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.trackingService = trackingService;
        this.pokemonRepository = pokemonRepository;
        this.name = "track";
        this.help = localeService.getMessageFor(LocaleService.TRACK_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String args = commandEvent.getArgs();
        Pokemon pokemon = pokemonRepository.search(args, commandEvent.getAuthor());
        final User user = commandEvent.getAuthor();
        trackingService.add(pokemon, user, config);
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }
}