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

import java.time.LocalTime;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.*;

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
        String msgId = config.getOverviewMessageId();
        if (!StringUtils.isEmpty(msgId)) {
            final Callable<Boolean> refreshEditThreadTask =
                    getMessageRefreshingTaskToSchedule(user, config.getServer(), msgId, localeService,
                            serverConfigRepository, raidRepository, clockService, commandEvent.getChannel(),
                            executorService);
            executorService.submit(refreshEditThreadTask);
        } else {
            final String messageString = getOverviewMessage(config,
                    localeService, raidRepository, clockService);
            commandEvent.reply(messageString, msg -> {
                final String messageId = msg.getId();
                final String server = config.getServer();
                serverConfigRepository.setOverviewMessageIdForServer(server, messageId);
                final Callable<Boolean> refreshEditThreadTask =
                        getMessageRefreshingTaskToSchedule(user, server, messageId,
                                localeService, serverConfigRepository, raidRepository, clockService,
                                commandEvent.getChannel(), executorService);
                executorService.submit(refreshEditThreadTask);
            });
        }
    }

    public static Callable<Boolean> getMessageRefreshingTaskToSchedule(User user,
                                                                       String server,
                                                                       String messageId, LocaleService localeService,
                                                                       ServerConfigRepository serverConfigRepository,
                                                                       RaidRepository raidRepository,
                                                                       ClockService clockService,
                                                                       MessageChannel messageChannel,
                                                                       final ExecutorService executorService) {
        Callable<Boolean> refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                TimeUnit.SECONDS.sleep(60); // Update once a minute
                Config config = serverConfigRepository.getConfigForServer(server);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Thread: " + Thread.currentThread().getId() +
                            " - Updating message with ID " + messageId);
                }
                final String messageString = getOverviewMessage(config,
                        localeService, raidRepository, clockService);
                messageChannel.editMessageById(messageId,
                        messageString)
                        .queue(m -> {}, m -> {
                            LOGGER.warn(m.getClass().getSimpleName() + " thrown: " + m.getMessage());
                            serverConfigRepository.save(config);
                            cleanUp(config, user, messageId, serverConfigRepository, localeService, messageChannel);
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
                                ServerConfigRepository serverConfigRepository, LocaleService localeService,
                                MessageChannel messageChannel) {
        try {
            if (!StringUtils.isEmpty(messageId)) {
                messageChannel.deleteMessageById(messageId).queue();
            }
        } catch (Throwable t) {
            // Do nothing
        } finally {
            try {
                serverConfigRepository.setOverviewMessageIdForServer(config.getServer(), null);
            } catch (Throwable t) {
                LOGGER.warn(t.getClass().getSimpleName() + " while getting overview message: " + t.getMessage());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cleaned up message related to this overview.");
            }
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.OVERVIEW_DELETED,
                    localeService.getLocaleForUser(user)));
        }
    }

    private static String getOverviewMessage(Config config,
                                             LocaleService localeService,
                                             RaidRepository raidRepository,
                                             ClockService clockService) {
        final Locale locale = config.getLocale();
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
                            .append(printTimeIfSameDay(raid.getEndOfRaid().minusHours(1))).append(" - ")
                            .append(printTimeIfSameDay(raid.getEndOfRaid()))
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
                locale,
                printTime(clockService.getCurrentTime())));
        final String message = stringBuilder.toString();
        return message;
    }
}
