package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.*;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.printTime;

/**
 * !raid status [Pokestop name]
 */
public class RaidListCommand extends ConfigAwareCommand {
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;

    public RaidListCommand(RaidRepository raidRepository, LocaleService localeService,
                           ConfigRepository configRepository, PokemonRepository pokemonRepository) {
        super(configRepository);
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.name = "list";
        this.help = localeService.getMessageFor(LocaleService.LIST_HELP, LocaleService.DEFAULT);
        this.aliases = new String[]{"info"};
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String userName = commandEvent.getAuthor().getName();
        final String args = commandEvent.getArgs();
        final Locale locale = localeService.getLocaleForUser(userName);
        Set<Raid> raids;
        if (args != null && args.length() > 0) {
            raids = raidRepository.getRaidsInRegionForPokemon(config.region, pokemonRepository.getByName(args));
        } else {
            raids = raidRepository.getAllRaidsForRegion(config.region);
        }

        if (raids.size() == 0) {
            replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.LIST_NO_RAIDS, locale));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("**").append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
            if (args != null && args.length() > 0) {
                stringBuilder.append(" (").append(args).append(")");
            }
            stringBuilder.append(":**\n");

            for (Raid raid : raids) {
                final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                stringBuilder.append(raid.getGym().getName()).append(" (")
                        .append(raid.getPokemon().getName()).append(") - ")
                        .append(localeService.getMessageFor(LocaleService.ENDS_AT, locale, printTime(raid.getEndOfRaid())))
                        .append(". ").append(numberOfPeople)
                        .append(" ")
                        .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale))
                        .append(".\n");
            }
            commandEvent.reply(stringBuilder.toString());
        }
    }
}
