package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.tracking.PokemonTrackingTarget;
import pokeraidbot.domain.tracking.TrackingCommandListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class TrackPokemonCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;
    private final TrackingCommandListener commandListener;

    public TrackPokemonCommand(BotService botService, ServerConfigRepository serverConfigRepository, LocaleService localeService,
                               PokemonRepository pokemonRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.commandListener = botService.getTrackingCommandListener();
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.name = "track";
        this.help = localeService.getMessageFor(LocaleService.TRACK_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String args = commandEvent.getArgs();
        Pokemon pokemon = pokemonRepository.search(args, commandEvent.getAuthor());
        final String userId = commandEvent.getAuthor().getId();
        final User user = commandEvent.getAuthor();
        commandListener.add(new PokemonTrackingTarget(config.getRegion(), userId, pokemon), user, config);
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }
}