package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.tracking.PokemonTrackingTarget;
import pokeraidbot.domain.tracking.TrackingCommandListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class UnTrackPokemonCommand extends ConfigAwareCommand {
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;
    private final TrackingCommandListener commandListener;

    public UnTrackPokemonCommand(BotService botService, ConfigRepository configRepository, LocaleService localeService,
                                 PokemonRepository pokemonRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.commandListener = botService.getTrackingCommandListener();
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.name = "untrack";
        this.help = localeService.getMessageFor(LocaleService.UNTRACK_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String args = commandEvent.getArgs();
        final String userId = commandEvent.getAuthor().getId();
        final String userName = commandEvent.getAuthor().getName();
        if (args == null || args.length() < 1) {
            commandListener.removeAll(userId);
            commandEvent.reactSuccess();
//            String message =
//                    localeService.getMessageFor(LocaleService.TRACKING_REMOVED, localeService.getLocaleForUser(userName),
//                            "ALL", userName);
//            replyBasedOnConfig(config, commandEvent, message);
        } else {
            Pokemon pokemon = pokemonRepository.getByName(args);
            commandListener.remove(new PokemonTrackingTarget(config.getRegion(), userId, pokemon.getName()), userName);
//            String message =
//                    localeService.getMessageFor(LocaleService.TRACKING_REMOVED, localeService.getLocaleForUser(userName),
//                            pokemon.getName(), userName);
            commandEvent.reactSuccess();
            // replyBasedOnConfig(config, commandEvent, message);
        }
    }
}