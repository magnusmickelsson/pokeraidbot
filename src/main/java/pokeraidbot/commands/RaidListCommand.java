package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.*;

/**
 * !raid list [optional: boss name]
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
                final Set<RaidGroup> groups = raidRepository.getGroups(raid);
                if (!raid.isExRaid()) {
                    if (raidGym.isExGym()) {
                        stringBuilder.append("**").append(raidGym.getName()).append(Emotes.STAR + "**");
                    } else {
                        stringBuilder.append("*").append(raidGym.getName()).append("*");
                    }
                    stringBuilder.append(" ")
                            .append(printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), false)))
                            .append("-")
                            .append(printTime(raid.getEndOfRaid().toLocalTime()));
                    if (groups.size() < 1) {
                        stringBuilder.append(" (**").append(numberOfPeople)
                                .append("**)");
                    } else {
                        stringBuilder.append(raidRepository.listGroupsForRaid(raid, groups));
                    }
                    stringBuilder.append("\n");
                } else {
                    exRaids.append("\n*").append(raidGym.getName());
                    exRaids.append("* ")
                            .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, locale,
                                    printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), true)),
                                    printTime(raid.getEndOfRaid().toLocalTime())));
                    if (groups.size() < 1) {
                        exRaids.append(" (**").append(numberOfPeople)
                                .append("**)");
                    } else {
                        exRaids.append(raidRepository.listGroupsForRaid(raid, groups));
                    }
                }
            }
            final String exRaidList = exRaids.toString();
            if (exRaidList.length() > 1) {
                stringBuilder.append("\n**Raid-EX:**").append(exRaidList);
            }
            replyBasedOnConfig(config, commandEvent, stringBuilder.toString());
        }
    }
}
