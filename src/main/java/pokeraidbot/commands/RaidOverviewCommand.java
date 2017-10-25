package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.*;

import static pokeraidbot.Utils.printTime;
import static pokeraidbot.Utils.printTimeIfSameDay;

/**
 * !raid overview [Pokestop name]
 */
public class RaidOverviewCommand extends ConfigAwareCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaidOverviewCommand.class);

    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;

    public RaidOverviewCommand(RaidRepository raidRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository, PokemonRepository pokemonRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.name = "overview";
        this.help = "!raid overview";
                //localeService.getMessageFor(LocaleService.LIST_HELP, LocaleService.DEFAULT);
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String args = commandEvent.getArgs();

        final String messageString = getOverviewMessage(user, config, args);
        commandEvent.reply(messageString, msg -> {
            final String msgId = msg.getId();

            final Callable<Boolean> refreshEditThreadTask =
                    getMessageRefreshingTaskToSchedule(commandEvent, config, msgId);
            executorService.submit(refreshEditThreadTask);
        });
    }

    private Callable<Boolean> getMessageRefreshingTaskToSchedule(CommandEvent commandEvent,
                                                                 Config config,
                                                                 String messageId) {
        final User user = commandEvent.getAuthor();
        final String args = commandEvent.getArgs();
        Callable<Boolean> refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                TimeUnit.SECONDS.sleep(60); // Update once a minute
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Thread: " + Thread.currentThread().getId() +
                            " - Updating message with ID " + messageId);
                }
                final String messageString = getOverviewMessage(user, config, args);
                commandEvent.getMessage().getChannel().editMessageById(messageId,
                        messageString)
                        .queue(m -> {}, m -> {
                            cleanUp(commandEvent, messageId);
                        });
                return true;
            };
            do {
                try {
                    executorService.submit(editTask).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
        };
        return refreshEditThreadTask;
    }

    private void cleanUp(CommandEvent commandEvent, String messageId) {
        try {
            if (!StringUtils.isEmpty(messageId)) {
                commandEvent.getChannel().deleteMessageById(messageId).queue();
            }
        } catch (Throwable t) {
            // Do nothing
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cleaned up message related to this overview.");
            }
        }
    }

    private String getOverviewMessage(User user, Config config, String args) {
        String userName = user.getName();
        final Locale locale = localeService.getLocaleForUser(user);
        Set<Raid> raids;
        if (args != null && args.length() > 0) {
            raids = raidRepository.getRaidsInRegionForPokemon(config.getRegion(), pokemonRepository.getByName(args));
        } else {
            raids = raidRepository.getAllRaidsForRegion(config.getRegion());
        }
        final String messageString;
        StringBuilder stringBuilder = new StringBuilder();
        if (raids.size() == 0) {
            stringBuilder.append(localeService.getMessageFor(LocaleService.LIST_NO_RAIDS, locale));
        } else {
            StringBuilder exRaids = new StringBuilder();
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
                if (!Utils.isRaidEx(raid) && (currentPokemon == null || (!currentPokemon.equals(raidBoss)))) {
                    currentPokemon = raid.getPokemon();
                    stringBuilder.append("\n**").append(currentPokemon.getName()).append("**\n");
                }
                final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                final Gym raidGym = raid.getGym();
                if (!Utils.isRaidEx(raid)) {
                    stringBuilder.append("*").append(raidGym.getName()).append("*");
                    stringBuilder.append("  ")
                            .append(printTimeIfSameDay(raid.getEndOfRaid().minusHours(1))).append(" - ")
                            .append(printTimeIfSameDay(raid.getEndOfRaid()))
                            .append(". ").append(numberOfPeople)
                            .append(" ")
                            .append(localeService.getMessageFor(LocaleService.SIGNED_UP, locale)).append("\n");
                }
                else {
                    exRaids.append("\n").append(raidGym.getName())
                            .append(" (")
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
                stringBuilder.append("\n**Raid-EX:**").append(exRaidList);
            }
        }
        stringBuilder.append("\n\n").append(localeService.getMessageFor(LocaleService.OVERVIEW_UPDATE,
                localeService.getLocaleForUser(user),
                printTime(LocalTime.now())));
        final String message = stringBuilder.toString();
        return message;
    }
}
