package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.OverviewException;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRaidInfo;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.net.SocketTimeoutException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static pokeraidbot.Utils.*;

/**
 * !raid overview [Pokestop name]
 */
public class RaidOverviewCommand extends ConcurrencyAndConfigAwareCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaidOverviewCommand.class);

    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;
    private final ClockService clockService;
    private final PokemonRaidStrategyService strategyService;

    public RaidOverviewCommand(RaidRepository raidRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository, PokemonRepository pokemonRepository,
                               CommandListener commandListener, ClockService clockService,
                               ExecutorService executorService, PokemonRaidStrategyService strategyService) {
        super(serverConfigRepository, commandListener, localeService, executorService);
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.clockService = clockService;
        this.strategyService = strategyService;
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
        final String server = config.getServer();
        if (!StringUtils.isEmpty(msgId)) {
            final String args = commandEvent.getArgs();
            if (!StringUtils.isEmpty(args) && args.equalsIgnoreCase("reset")) {
                try {
                    commandEvent.getChannel().getMessageById(msgId).complete().delete().queue();
                } catch (Throwable t) {
                    // Ignore, just means the message couldn't be cleared/deleted and have to be manually purged
                    LOGGER.debug("We couldn't find and delete overview for server " + server + ": " + t.getMessage());
                }
                serverConfigRepository.setOverviewMessageIdForServer(server, null);
                LOGGER.info("Cleared overview message for server " + server + ".");
                replyBasedOnConfig(config, commandEvent,
                        localeService.getMessageFor(LocaleService.OVERVIEW_CLEARED, locale));
            } else {
                LOGGER.info("Server overview message ID not empty. Overview already exists for this server.");
                replyBasedOnConfig(config, commandEvent,
                        localeService.getMessageFor(LocaleService.OVERVIEW_EXISTS, locale));
            }
        } else {
            final String messageString = getOverviewMessage(config,
                    localeService, raidRepository, clockService, locale, strategyService);
            commandEvent.getChannel().sendMessage(messageString).queue(msg -> {
                final String messageId = msg.getId();
                serverConfigRepository.setOverviewMessageIdForServer(server, messageId);
                final Callable<Boolean> refreshEditThreadTask =
                        getMessageRefreshingTaskToSchedule(user, server, messageId,
                                localeService, locale, serverConfigRepository, raidRepository, clockService,
                                commandEvent.getChannel(), executorService, strategyService);
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
                                                                       final ExecutorService executorService,
                                                                       PokemonRaidStrategyService strategyService) {
        final Callable<Boolean> refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                TimeUnit.SECONDS.sleep(60); // Update once a minute
                final Config config = serverConfigRepository.getConfigForServer(server);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Thread: " + Thread.currentThread().getId() +
                            " - Updating for server " + config.getServer() + " with ID " + messageId);
                }
                final Message message = messageChannel.getMessageById(messageId).complete();
                if (config.getOverviewMessageId() != null &&
                        message != null) {
                    final String messageString = getOverviewMessage(config,
                            localeService, raidRepository, clockService, locale, strategyService);
                    messageChannel.editMessageById(messageId,
                            messageString)
                            .queue(m -> {
                            }, m -> {
                                LOGGER.warn(m.getClass().getSimpleName() + " thrown: " + m.getMessage());
                                if (m instanceof SocketTimeoutException) {
                                    LOGGER.debug("We got a socket timeout, which could be that the server is temporarily " +
                                            "down. Let's not clean up things before we know if it works or not.");
                                }
                            });
                    return true;
                } else {
                    LOGGER.warn("Could not find message for overview - config ID: " +
                            config.getOverviewMessageId() + ", message: " +
                            (message == null ? "null" : message.getId()) + ". Cleaning up...");
                    cleanUp(config, messageId, serverConfigRepository,
                            messageChannel);
                    return false;
                }
            };
            boolean overviewOk = true;
            do {
                try {
                    overviewOk = executorService.submit(editTask).get();
                } catch (InterruptedException | ExecutionException | OverviewException e) {
                    LOGGER.warn("Exception when running edit task: " + e.getMessage() + ".");
                    if (Utils.isExceptionOrCauseNetworkIssues(e)) {
                        LOGGER.info("Exception was due to timeout, so trying again later. Could be temporary.");
                        overviewOk = true;
                    } else {
                        LOGGER.info("Exception was not due to timeout, so terminating this overview.");
                        overviewOk = false;
                    }
                }
            } while (overviewOk);
            return false;
        };
        return refreshEditThreadTask;
    }

    private static void cleanUp(Config config, String messageId,
                                ServerConfigRepository serverConfigRepository,
                                MessageChannel messageChannel) {
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
                                             ClockService clockService, Locale locale,
                                             PokemonRaidStrategyService strategyService) {
        Set<Raid> raids = raidRepository.getAllRaidsForRegion(config.getRegion());
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
                    final PokemonRaidInfo raidInfo = strategyService.getRaidInfo(currentPokemon);
                    stringBuilder.append("\n**").append(currentPokemon.getName()).append("**")
                            .append(" (").append(raidInfo.getBossTier()).append(")").append("\n");
                }
                final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                final Gym raidGym = raid.getGym();
                final Set<RaidGroup> groups = raidRepository.getGroups(raid);
                if (!raid.isExRaid()) {
                    stringBuilder.append("*").append(raidGym.getName()).append("*");
                    stringBuilder.append(" ")
                            .append(printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), false))).append("-")
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
