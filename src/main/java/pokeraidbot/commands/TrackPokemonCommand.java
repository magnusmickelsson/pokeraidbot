package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.Config;
import pokeraidbot.domain.config.ConfigRepository;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.tracking.PokemonTrackingTarget;
import pokeraidbot.domain.tracking.TrackingCommandListener;

public class TrackPokemonCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;
    private final TrackingCommandListener commandListener;

    public TrackPokemonCommand(BotService botService, ConfigRepository configRepository, LocaleService localeService,
                               PokemonRepository pokemonRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.commandListener = botService.getTrackingCommandListener();
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.name = "track";
        this.help = localeService.getMessageFor(LocaleService.TRACK_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String args = commandEvent.getArgs();
        Pokemon pokemon = pokemonRepository.getByName(args);
        final String userId = commandEvent.getAuthor().getId();
        final String userName = commandEvent.getAuthor().getName();
        commandListener.add(new PokemonTrackingTarget(config.region, userId, pokemon.getName()), userName);
        String message =
                localeService.getMessageFor(LocaleService.TRACKING_ADDED, localeService.getLocaleForUser(userName),
                        pokemon.getName(), userName);
        replyBasedOnConfig(config, commandEvent, message);
    }
}