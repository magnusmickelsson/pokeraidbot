package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.time.LocalTime;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.*;

import static pokeraidbot.Utils.getStartOfRaid;
import static pokeraidbot.Utils.printTime;
import static pokeraidbot.Utils.printTimeIfSameDay;

/**
 * !raid overview [Pokestop name]
 */
public class RaidOverviewCommand extends ConcurrencyAndConfigAwareCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaidOverviewCommand.class);

    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;
    private final ClockService clockService;

    public RaidOverviewCommand(RaidRepository raidRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository, PokemonRepository pokemonRepository,
                               CommandListener commandListener, ClockService clockService,
                               ExecutorService executorService) {
        super(serverConfigRepository, commandListener, localeService, executorService);
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.clockService = clockService;
        this.name = "overview";
        this.help = localeService.getMessageFor(LocaleService.OVERVIEW_HELP, LocaleService.DEFAULT);
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final Locale locale = config.getLocale();
        if (!isUserAdministrator(commandEvent) && !isUserServerMod(commandEvent, config)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_PERMISSION,
                    localeService.getLocaleForUser(user)));
        }
        String msgId = config.getOverviewMessageId();
        if (!StringUtils.isEmpty(msgId)) {
            LOGGER.info("Server overview message ID not empty. Overview already exists for this server.");
            replyBasedOnConfig(config, commandEvent,
                    localeService.getMessageFor(LocaleService.OVERVIEW_EXISTS, locale));
        } else {
            final String messageString = getOverviewMessage(config,
                    localeService, raidRepository, clockService, locale);
            commandEvent.reply(messageString, msg -> {
                final String messageId = msg.getId();
                final String server = config.getServer();
                serverConfigRepository.setOverviewMessageIdForServer(server, messageId);
                final Callable<Boolean> refreshEditThreadTask =
                        getMessageRefreshingTaskToSchedule(user, server, messageId,
                                localeService, locale, serverConfigRepository, raidRepository, clockService,
                                commandEvent.getChannel(), executorService);
                executorService.submit(refreshEditThreadTask);
            });
        }
    }

    public static Callable<Boolean> getMessageRefreshingTaskToSchedule(User user,
                                                                       String server,
                                                                       String messageId, LocaleService localeService,
                                                                       Locale locale, ServerConfigRepository serverConfigRepository,
                                                                       RaidRepository raidRepository,
                                                                       ClockService clockService,
                                                                       MessageChannel messageChannel,
                                                                       final ExecutorService executorService) {
        final Callable<Boolean> refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                TimeUnit.SECONDS.sleep(60); // Update once a minute
                Config config = serverConfigRepository.getConfigForServer(server);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Thread: " + Thread.currentThread().getId() +
                            " - Updating message with ID " + messageId);
                }
                final String messageString = getOverviewMessage(config,
                        localeService, raidRepository, clockService, locale);
                messageChannel.editMessageById(messageId,
                        messageString)
                        .queue(m -> {}, m -> {
                            LOGGER.warn(m.getClass().getSimpleName() + " thrown: " + m.getMessage());
                            Config savedConfig = serverConfigRepository.save(config);
                            cleanUp(savedConfig, user, messageId, serverConfigRepository, localeService,
                                    messageChannel, locale);
                        });
                return true;
            };
            do {
                try {
                    executorService.submit(editTask).get();
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.warn("Exception when running edit task: " + e.getMessage() + " - shutting it down.");
                    return false;
                }
            } while (true);
        };
        return refreshEditThreadTask;
    }

    private static void cleanUp(Config config, User user, String messageId,
                                ServerConfigRepository serverConfigRepository, LocaleService localeService,
                                MessageChannel messageChannel, Locale locale) {
        try {
            if (!StringUtils.isEmpty(messageId)) {
                messageChannel.deleteMessageById(messageId).queue(m -> {
                    LOGGER.info("Deleted overview message with ID " + messageId);
                }, m -> {
                    LOGGER.info("Could not delete overview message with ID " + messageId);
                });
            }
        } catch (Throwable t) {
            // Do nothing
            LOGGER.warn("Exception when cleaning up overview: " + t.getMessage());
        } finally {
            try {
                LOGGER.debug("Trying to reset overview message for server: " + config.getServer());
                serverConfigRepository.setOverviewMessageIdForServer(config.getServer(), null);
            } catch (Throwable t) {
                LOGGER.warn(t.getClass().getSimpleName() + " while resetting overview message for server " +
                        config.getServer() + ": " + t.getMessage());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cleaned up message related to this overview.");
            }
        }
    }

    private static String getOverviewMessage(Config config,
                                             LocaleService localeService,
                                             RaidRepository raidRepository,
                                             ClockService clockService, Locale locale) {
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
                    locale)).append("\n");
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
                }
                else {
                    exRaids.append("\n").append(raidGym.getName())
                            .append(" (")
                            .append(raidBoss.getName()).append(") - ")
                            .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, locale,
                                    printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), true)),
                                    printTime(raid.getEndOfRaid().toLocalTime())))
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
        stringBuilder.append("\n\n")
                .append(localeService.getMessageFor(LocaleService.UPDATED_EVERY_X,
                        locale, LocaleService.asString(TimeUnit.SECONDS, locale),
                        String.valueOf(60)))
                .append(" ")
                .append(localeService.getMessageFor(LocaleService.LAST_UPDATE,
                locale,
                printTime(clockService.getCurrentTime())));
        final String message = stringBuilder.toString();
        return message;
    }
}
