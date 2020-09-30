package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.errors.OverviewException;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRaidInfo;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.util.*;
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
    private final ClockService clockService;
    private final PokemonRaidStrategyService strategyService;

    public RaidOverviewCommand(RaidRepository raidRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository,
                               CommandListener commandListener, ClockService clockService,
                               ExecutorService executorService, PokemonRaidStrategyService strategyService) {
        super(serverConfigRepository, commandListener, localeService, executorService);
        this.localeService = localeService;
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
                    commandEvent.getChannel().retrieveMessageById(msgId).complete().delete().queue();
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
            final Map<String, String> messages = getOverviewMessagesMap(config,
                    localeService, raidRepository, clockService, locale, strategyService);
            final EmbedBuilder embedBuilder = new EmbedBuilder();
            int messageSize = 0;
            boolean isOverLimit = false;
            for (String boss : messages.keySet()) {
                String bossFieldContent = messages.get(boss);
                if (embedBuilder.length() < 4500) {
                    embedBuilder.addField(boss, bossFieldContent, false);
                    messageSize = messageSize + boss.length() + bossFieldContent.length();
                } else {
                    isOverLimit = true;
                }
            }
            if (isOverLimit) {
                // TODO: i18n
                embedBuilder.addField("Varning:", "Tyvärr överskrider översikten maxstorleken för " +
                        "Discord-meddelanden och behöver därför kortas ner tills vidare. " +
                        "Alla raider kommer inte listas.", false);
            }
            final MessageEmbed messageEmbed = embedBuilder.build();
            commandEvent.getChannel().sendMessage(messageEmbed).queue(msg -> {
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
                final Message message = messageChannel.retrieveMessageById(messageId).complete();
                if (config.getOverviewMessageId() != null &&
                        message != null) {
                    final Map<String, String> messages = getOverviewMessagesMap(config,
                            localeService, raidRepository, clockService, locale, strategyService);
                    final EmbedBuilder embedBuilder = new EmbedBuilder();
                    for (String boss : messages.keySet()) {
                        final String bossMessage = messages.get(boss);
                        addFieldSplitMessageIfNeeded(embedBuilder, boss, bossMessage);
                    }
                    final MessageEmbed newEmbed = embedBuilder.build();
                    if (newEmbed.getLength() < 5900) {
                        messageChannel.editMessageById(messageId, newEmbed)
                                .queue(m -> {
                                }, m -> {
                                    LOGGER.warn(m.getClass().getSimpleName() + " thrown: " + m.getMessage());
                                    if (m instanceof SocketTimeoutException) {
                                        LOGGER.debug("We got a socket timeout, which could be that the server is temporarily " +
                                                "down. Let's not clean up things before we know if it works or not.");
                                    }
                                });
                    } else {
                        LOGGER.warn("Update message is too big for server " + server + ", so skipping the update.");
                    }
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

    protected static void addFieldSplitMessageIfNeeded(final EmbedBuilder embedBuilder, final String boss, final String bossMessage) {
        if (bossMessage.length() < 1000) {
            embedBuilder.addField(boss, bossMessage, false);
        } else {
            // Split into more fields because the message has a risk of being too big (1024 char limit in discord API for field size)
            Map<Integer, String> fields = new HashMap<>();
            int currentPosition = 0;
            int currentSearchPosition = 900;
            int counter = 0;
            do {
                counter++;
                int splitAt = bossMessage.indexOf("\n", currentSearchPosition);
                String currentFieldString = bossMessage.substring(currentPosition, splitAt);
                fields.put(counter, currentFieldString);
                currentPosition = splitAt + 1;
                currentSearchPosition = splitAt + 900;
            } while (currentSearchPosition < bossMessage.length() && embedBuilder.length() < 4500);

            fields.put(counter + 1, bossMessage.substring(currentPosition));

            final int fieldsSize = fields.size();
            for (Integer fieldKey : fields.keySet()) {
                if (embedBuilder.length() + fields.get(fieldKey).length() < 4700) {
                    embedBuilder.addField(boss + " " + fieldKey + "/" + fieldsSize, fields.get(fieldKey), false);
                }
            }
        }
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

    private static Map<String, String> getOverviewMessagesMap(Config config,
                                                              LocaleService localeService,
                                                              RaidRepository raidRepository,
                                                              ClockService clockService, Locale locale,
                                                              PokemonRaidStrategyService strategyService) {
        Set<Raid> raids = raidRepository.getAllRaidsForRegion(config.getRegion());
        final Map<String, String> overviewMessagePerBoss = new LinkedHashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        if (raids.size() == 0) {
            stringBuilder.append(localeService.getMessageFor(LocaleService.LIST_NO_RAIDS, locale));
            overviewMessagePerBoss.put("RAIDS", stringBuilder.toString());
        } else {
            StringBuilder exRaids = new StringBuilder();
            stringBuilder.append(localeService.getMessageFor(LocaleService.CURRENT_RAIDS, locale));
            stringBuilder.append(":");
            final String raidHeadline = stringBuilder.toString();
            stringBuilder = new StringBuilder();
//            stringBuilder.append(localeService.getMessageFor(LocaleService.RAID_DETAILS,
//                    locale));
            overviewMessagePerBoss.put(raidHeadline, stringBuilder.toString());

            for (Raid raid : raids) {
                final Pokemon raidBoss = raid.getPokemon();
                final PokemonRaidInfo raidInfo = strategyService.getRaidInfo(raidBoss);
                String pokemonName = "**" + raidBoss.getName();
                if (raidInfo != null && raidInfo.getBossTier() > 0) {
                    pokemonName = pokemonName + " (" + raidInfo.getBossTier() + ")";
                }
                pokemonName += "**";
                StringBuilder bossStringBuilder = new StringBuilder();
                // If not an EX raid, add boss name
                boolean isExRaid = raid.isExRaid();
                if (!isExRaid) {
                    overviewMessagePerBoss.putIfAbsent(pokemonName, "");
                }
                final int numberOfPeople = raid.getNumberOfPeopleSignedUp();
                final Gym raidGym = raid.getGym();
                final Set<RaidGroup> groups = raidRepository.getGroups(raid);
                if (!isExRaid) {
                    if (raidGym.isExGym()) {
                        bossStringBuilder.append("*").append(raidGym.getName()).append(Emotes.STAR + "*");
                    } else {
                        bossStringBuilder.append("*").append(raidGym.getName()).append("*");
                    }
                    bossStringBuilder.append(" ")
                            .append(printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), false))).append("-")
                            .append(printTime(raid.getEndOfRaid().toLocalTime()));
                    if (groups.size() < 1) {
                        bossStringBuilder.append(" (**").append(numberOfPeople)
                                .append("**)");
                    } else {
                        bossStringBuilder.append(raidRepository.listGroupsForRaid(raid, groups));
                    }
                    bossStringBuilder.append("\n");
                    overviewMessagePerBoss.put(pokemonName,
                            overviewMessagePerBoss.get(pokemonName) + bossStringBuilder.toString());
                } else {
                    exRaids.append("\n*").append(raidGym.getName());
                    exRaids.append("* ")
                            .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, locale,
                                    printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), raid.isExRaid())),
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
                overviewMessagePerBoss.put("\n**EX-raid:**", exRaidList);
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("\n")
                .append(localeService.getMessageFor(LocaleService.UPDATED_EVERY_X,
                        locale, LocaleService.asString(TimeUnit.SECONDS, locale),
                        String.valueOf(60)))
                .append(" ")
                .append(localeService.getMessageFor(LocaleService.LAST_UPDATE,
                        locale,
                        printTime(clockService.getCurrentTime())));
        overviewMessagePerBoss.put("", stringBuilder.toString());
        return overviewMessagePerBoss;
    }
}
