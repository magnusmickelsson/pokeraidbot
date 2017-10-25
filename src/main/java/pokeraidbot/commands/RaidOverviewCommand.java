package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
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
    private final ClockService clockService;

    public RaidOverviewCommand(RaidRepository raidRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository, PokemonRepository pokemonRepository,
                               CommandListener commandListener, ClockService clockService) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.clockService = clockService;
        this.name = "overview";
        // todo: i18n help
        this.help = "!raid overview";
                //localeService.getMessageFor(LocaleService.LIST_HELP, LocaleService.DEFAULT);
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        String msgId = config.getOverviewMessageId();
        if (!StringUtils.isEmpty(msgId)) {
            final Callable<Boolean> refreshEditThreadTask =
                    getMessageRefreshingTaskToSchedule(user, config, msgId, localeService,
                            serverConfigRepository, raidRepository, clockService, commandEvent.getChannel());
            executorService.submit(refreshEditThreadTask);
        } else {
            final String messageString = getOverviewMessage(user, config,
                    localeService, raidRepository, clockService);
            commandEvent.reply(messageString, msg -> {
                final String messageId = msg.getId();
                serverConfigRepository.setOverviewMessageIdForServer(config.getServer(), messageId);
                final Callable<Boolean> refreshEditThreadTask =
                        getMessageRefreshingTaskToSchedule(user, config, messageId,
                                localeService, serverConfigRepository, raidRepository, clockService,
                                commandEvent.getChannel());
                executorService.submit(refreshEditThreadTask);
            });
        }
    }

    public static Callable<Boolean> getMessageRefreshingTaskToSchedule(User user,
                                                                       Config config,
                                                                       String messageId, LocaleService localeService,
                                                                       ServerConfigRepository serverConfigRepository,
                                                                       RaidRepository raidRepository,
                                                                       ClockService clockService,
                                                                       MessageChannel messageChannel) {
        Callable<Boolean> refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                TimeUnit.SECONDS.sleep(60); // Update once a minute
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Thread: " + Thread.currentThread().getId() +
                            " - Updating message with ID " + messageId);
                }
                final String messageString = getOverviewMessage(user, config,
                        localeService, raidRepository, clockService);
                messageChannel.editMessageById(messageId,
                        messageString)
                        .queue(m -> {}, m -> {
                            cleanUp(config, user, messageId, serverConfigRepository, messageChannel);
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

    private static void cleanUp(Config config, User user, String messageId,
                                ServerConfigRepository serverConfigRepository, MessageChannel messageChannel) {
        try {
            if (!StringUtils.isEmpty(messageId)) {
                messageChannel.deleteMessageById(messageId).queue();
            }
        } catch (Throwable t) {
            // Do nothing
        } finally {
            serverConfigRepository.setOverviewMessageIdForServer(config.getServer(), null);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cleaned up message related to this overview.");
            }
            // todo: i18n
            throw new UserMessedUpException(user, "Overview message has been removed by someone. " +
                    "Run *!raid overview* again to create a new one.");
        }
    }

    private static String getOverviewMessage(User user, Config config,
                                             LocaleService localeService,
                                             RaidRepository raidRepository,
                                             ClockService clockService) {
        final Locale locale = localeService.getLocaleForUser(user);
        Set<Raid> raids = raidRepository.getAllRaidsForRegion(config.getRegion());
        final String messageString;
        StringBuilder stringBuilder = new StringBuilder();
        if (raids.size() == 0) {
            stringBuilder.append(localeService.getMessageFor(LocaleService.LIST_NO_RAIDS, locale));
        } else {
            StringBuilder exRaids = new StringBuilder();
            stringBuilder.append("**").append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
            stringBuilder.append(":**");
            stringBuilder.append("\n").append(localeService.getMessageFor(LocaleService.RAID_DETAILS,
                    localeService.getLocaleForUser(user))).append("\n");
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
                printTime(clockService.getCurrentTime())));
        final String message = stringBuilder.toString();
        return message;
    }
}
