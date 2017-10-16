package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.printTimeIfSameDay;

/**
 * !raid status [Pokestop name]
 */
public class RaidListCommand extends ConfigAwareCommand {
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;

    public RaidListCommand(RaidRepository raidRepository, LocaleService localeService,
                           ConfigRepository configRepository, PokemonRepository pokemonRepository, CommandListener commandListener) {
        super(configRepository, commandListener, localeService);
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
        String userName = user.getName();
        final String args = commandEvent.getArgs();
        final Locale locale = localeService.getLocaleForUser(userName);
        Set<Raid> raids;
        if (args != null && args.length() > 0) {
            raids = raidRepository.getRaidsInRegionForPokemon(config.getRegion(), pokemonRepository.getByName(args));
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
            stringBuilder.append("**").append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
            if (args != null && args.length() > 0) {
                stringBuilder.append(" (").append(args).append(")");
            }
            stringBuilder.append(":**");
            stringBuilder.append("\n").append(localeService.getMessageFor(LocaleService.RAID_DETAILS,
                    localeService.getLocaleForUser(user))).append("\n");
            final LocalDate today = LocalDate.now();
            Pokemon currentPokemon = null;
            for (Raid raid : raids) {
                final Pokemon raidBoss = raid.getPokemon();
                if (currentPokemon == null || (!currentPokemon.equals(raidBoss))) {
                    currentPokemon = raid.getPokemon();
                    stringBuilder.append("\n**").append(currentPokemon.getName()).append("**\n");
                }
                final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                final Gym raidGym = raid.getGym();
                if (raid.getEndOfRaid().toLocalDate().isEqual(today)) {
                    stringBuilder.append("*").append(raidGym.getName()).append("*");
                    stringBuilder.append("  ")
                    .append(printTimeIfSameDay(raid.getEndOfRaid().minusHours(1))).append(" - ")
                    .append(printTimeIfSameDay(raid.getEndOfRaid()))
                    .append(". ").append(numberOfPeople)
                    .append(" ")
                    .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale)).append("\n");
                }
//                else {
//                    exRaids.append("[").append(raidGym.getName()).append("](")
//                            .append(Utils.getStaticMapUrl(raidGym)).append(") (")
//                            .append(raidBoss.getName()).append(") - ")
//                            .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, locale,
//                                    printTimeIfSameDay(raid.getEndOfRaid().minusHours(1)),
//                                    printTimeIfSameDay(raid.getEndOfRaid())))
//                            .append(". ").append(numberOfPeople)
//                            .append(" ")
//                            .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale))
//                            .append(".\n");
//                }
            }
//            final String exRaidList = exRaids.toString();
//            if (exRaidList.length() > 1) {
////                embedBuilder = new EmbedBuilder();
//                embedBuilder.addField("**Raid-EX:**", exRaidList, true);
//            }
            commandEvent.reply(stringBuilder.toString());
        }
    }
}
