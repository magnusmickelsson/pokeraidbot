package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.tracking.PokemonTrackingTarget;
import pokeraidbot.domain.tracking.TrackingService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

public class UnTrackPokemonCommand extends ConfigAwareCommand {
    private final PokemonRepository pokemonRepository;
    private final TrackingService trackingService;

    public UnTrackPokemonCommand(ServerConfigRepository serverConfigRepository,
                                 LocaleService localeService,
                                 PokemonRepository pokemonRepository, CommandListener commandListener,
                                 TrackingService trackingService, UserConfigRepository userConfigRepository) {
        super(serverConfigRepository, commandListener, localeService, userConfigRepository);
        this.trackingService = trackingService;
        this.pokemonRepository = pokemonRepository;
        this.name = "untrack";
        this.help = localeService.getMessageFor(LocaleService.UNTRACK_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config, pokeraidbot.domain.User user) {
        String args = commandEvent.getArgs();
        final String userId = commandEvent.getAuthor().getId();
        if (args == null || args.length() < 1) {
            trackingService.removeAllForUser(user);
            commandEvent.reactSuccess();
        } else {
            Pokemon pokemon = pokemonRepository.search(args, user);
            trackingService.removeForUser(new PokemonTrackingTarget(config.getRegion(), userId, pokemon), user);
            commandEvent.reactSuccess();
        }
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }
}