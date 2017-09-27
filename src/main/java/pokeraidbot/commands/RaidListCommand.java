package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import pokeraidbot.Utils;
import pokeraidbot.domain.*;

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
        super(configRepository, commandListener);
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
            commandEvent.reply(localeService.getMessageFor(LocaleService.LIST_NO_RAIDS, locale));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("**").append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
            if (args != null && args.length() > 0) {
                stringBuilder.append(" (").append(args).append(")");
            }
            stringBuilder.append(":**\n");

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            StringBuilder exRaids = new StringBuilder();
            final LocalDate today = LocalDate.now();
            for (Raid raid : raids) {
                final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                final Gym raidGym = raid.getGym();
                final Pokemon raidBoss = raid.getPokemon();
                if (raid.getEndOfRaid().toLocalDate().isEqual(today)) {
                    stringBuilder.append("[").append(raidGym.getName()).append("](")
                            .append(Utils.getStaticMapUrl(raidGym)).append(") (")
                            .append(raidBoss.getName()).append(") - ")
                            .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, locale,
                                    printTimeIfSameDay(raid.getEndOfRaid().minusHours(1)),
                                    printTimeIfSameDay(raid.getEndOfRaid())))
                            .append(". ").append(numberOfPeople)
                            .append(" ")
                            .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale))
                            .append(".\n");
                } else {
                    exRaids.append("[").append(raidGym.getName()).append("](")
                            .append(Utils.getStaticMapUrl(raidGym)).append(") (")
                            .append(raidBoss.getName()).append(") - ")
                            .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, locale,
                                    printTimeIfSameDay(raid.getEndOfRaid().minusHours(1)),
                                    printTimeIfSameDay(raid.getEndOfRaid())))
                            .append(". ").append(numberOfPeople)
                            .append(" ")
                            .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale))
                            .append(".\n");
                }
            }
            final String exRaidList = exRaids.toString();
            if (exRaidList.length() > 1) {
                stringBuilder.append("\nRaid-EX:\n").append(exRaidList);
            }
            embedBuilder.setDescription(stringBuilder.toString());
            commandEvent.reply(embedBuilder.build());
        }
    }
}
