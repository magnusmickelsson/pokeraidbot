package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalTime;
import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.getStartOfRaid;
import static pokeraidbot.Utils.printTime;
import static pokeraidbot.Utils.printTimeIfSameDay;

/**
 * !raid status [Pokestop name]
 */
public class RaidListCommand extends ConfigAwareCommand {
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;

    public RaidListCommand(RaidRepository raidRepository, LocaleService localeService,
                           ServerConfigRepository serverConfigRepository, PokemonRepository pokemonRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.name = "list";
        this.help = localeService.getMessageFor(LocaleService.LIST_HELP, LocaleService.DEFAULT);
        this.aliases = new String[]{"info"};
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String args = commandEvent.getArgs();
        final Locale locale = localeService.getLocaleForUser(user);
        Set<Raid> raids;
        if (args != null && args.length() > 0) {
            raids = raidRepository.getRaidsInRegionForPokemon(config.getRegion(), pokemonRepository.search(args, user));
        } else {
            raids = raidRepository.getAllRaidsForRegion(config.getRegion());
        }

        if (raids.size() == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(null);
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setDescription(localeService.getMessageFor(LocaleService.LIST_NO_RAIDS, locale));
            commandEvent.reply(embedBuilder.build());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder exRaids = new StringBuilder();
            stringBuilder.append("**").append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
            if (args != null && args.length() > 0) {
                stringBuilder.append(" (").append(args).append(")");
            }
            stringBuilder.append(":**");
            stringBuilder.append("\n").append(localeService.getMessageFor(LocaleService.RAID_DETAILS,
                    localeService.getLocaleForUser(user))).append("\n");
            Pokemon currentPokemon = null;
            for (Raid raid : raids) {
                final Pokemon raidBoss = raid.getPokemon();
                if (!raid.isExRaid() && (currentPokemon == null || (!currentPokemon.equals(raidBoss)))) {
                    currentPokemon = raid.getPokemon();
                    stringBuilder.append("\n**").append(currentPokemon.getName()).append("**\n");
                }
                final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                final Gym raidGym = raid.getGym();
                if (!raid.isExRaid()) {
                    stringBuilder.append("*").append(raidGym.getName()).append("*");
                    stringBuilder.append("  ")
                    .append(printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), false))).append(" - ")
                    .append(printTime(raid.getEndOfRaid().toLocalTime()))
                    .append(". ").append(numberOfPeople)
                    .append(" ")
                    .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale))
                            .append(raid.getNextEta(localeService, locale, LocalTime.now()))
                            .append("\n");
                } else {
                    exRaids.append("\n").append(raidGym.getName())
                            .append(" (")
                            .append(raidBoss.getName()).append(") - ")
                            .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, locale,
                                    printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), true)),
                                    printTime(raid.getEndOfRaid().toLocalTime())))
                            .append(". ").append(numberOfPeople)
                            .append(" ")
                            .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale))
                            .append(raid.getNextEta(localeService, locale, LocalTime.now()))
                            .append(".\n");
                }
            }
            final String exRaidList = exRaids.toString();
            if (exRaidList.length() > 1) {
//                embedBuilder = new EmbedBuilder();
                stringBuilder.append("\n**Raid-EX:**").append(exRaidList);
            }
            commandEvent.reply(stringBuilder.toString());
        }
    }
}
