package pokeraidbot.jda;

import main.BotServerMain;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.commands.NewRaidGroupCommand;
import pokeraidbot.commands.RaidOverviewCommand;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.EmoticonSignUpMessageListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class StartUpEventListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpEventListener.class);
    private ServerConfigRepository serverConfigRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final ClockService clockService;
    private final ExecutorService executorService;
    private final BotService botService;
    private final GymRepository gymRepository;
    private final PokemonRepository pokemonRepository;
    private final PokemonRaidStrategyService pokemonRaidStrategyService;

    public StartUpEventListener(ServerConfigRepository serverConfigRepository,
                                RaidRepository raidRepository, LocaleService localeService,
                                ClockService clockService, ExecutorService executorService, BotService botService,
                                GymRepository gymRepository, PokemonRepository pokemonRepository,
                                PokemonRaidStrategyService pokemonRaidStrategyService) {
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.clockService = clockService;
        this.executorService = executorService;
        this.botService = botService;
        this.gymRepository = gymRepository;
        this.pokemonRepository = pokemonRepository;
        this.pokemonRaidStrategyService = pokemonRaidStrategyService;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            final List<Guild> guilds = event.getJDA().getGuilds();
            for (Guild guild : guilds) {
                Config config = serverConfigRepository.getConfigForServer(guild.getName().trim().toLowerCase());
                if (config != null) {
                    final String messageId = config.getOverviewMessageId();
                    if (!StringUtils.isEmpty(messageId)) {
                        for (MessageChannel channel : guild.getTextChannels()) {
                            if (attachToOverviewMessageIfExists(guild, config, messageId, channel,
                                    pokemonRaidStrategyService)) {
                                break;
                            } else {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("Didn't find overview in channel " + channel.getName());
                                }
                            }
                        }
                    }

                    final List<RaidGroup> groupsForServer = raidRepository.getGroupsForServer(config.getServer());
                    for (RaidGroup group : groupsForServer) {
                        attachToGroupMessage(guild, config, group);
                    }
                }
            }
        }
    }

    private boolean attachToGroupMessage(Guild guild, Config config,
                                         RaidGroup raidGroup) {
        MessageChannel channel = null;
        try {
            final List<TextChannel> textChannels = guild.getTextChannels();
            for (TextChannel textChannel : textChannels) {
                if (textChannel.getName().equalsIgnoreCase(raidGroup.getChannel())) {
                    channel = textChannel;
                    break;
                }
            }
            final Locale locale = config.getLocale();
            Raid raid = raidRepository.getById(raidGroup.getRaidId());
            final EmoticonSignUpMessageListener emoticonSignUpMessageListener =
                    new EmoticonSignUpMessageListener(botService, localeService, serverConfigRepository,
                            raidRepository, pokemonRepository, gymRepository, raid.getId(), raidGroup.getStartsAt(),
                            raidGroup.getCreatorId());
            emoticonSignUpMessageListener.setEmoteMessageId(raidGroup.getEmoteMessageId());
            emoticonSignUpMessageListener.setInfoMessageId(raidGroup.getInfoMessageId());
            final int delayTime = raid.isExRaid() ? 1 : 15;
            final TimeUnit delayTimeUnit = raid.isExRaid() ? TimeUnit.MINUTES : TimeUnit.SECONDS;
            final Callable<Boolean> groupEditTask =
                    NewRaidGroupCommand.getMessageRefreshingTaskToSchedule(channel, raid,
                            emoticonSignUpMessageListener,
                            raidGroup.getInfoMessageId(), locale, raidRepository, pokemonRaidStrategyService,
                            localeService,
                            clockService, executorService, botService, delayTimeUnit, delayTime, raidGroup.getId());
            executorService.submit(groupEditTask);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found group message for raid " + raid + " in channel " +
                        (channel == null ? "N/A" : channel.getName()) +
                        " (server " + guild.getName() + "). Attaching to it.");
            }
            return true;
        } catch (UserMessedUpException e) {
            if (channel != null)
                channel.sendMessage(e.getMessage()).queue(m -> {
                    m.delete().queueAfter(BotServerMain.timeToRemoveFeedbackInSeconds, TimeUnit.SECONDS);
                });
        } catch (ErrorResponseException e) {
            // We couldn't find the message in this channel or had permission issues, ignore
            LOGGER.info("Caught exception during startup: " + e.getMessage());
            LOGGER.warn("Cleaning up raidgroup...");
            cleanUpRaidGroup(raidGroup);
            LOGGER.debug("Stacktrace:", e);
        } catch (Throwable e) {
            LOGGER.warn("Cleaning up raidgroup due to exception: " + e.getMessage());
            cleanUpRaidGroup(raidGroup);
        }
        return false;
    }

    private void cleanUpRaidGroup(RaidGroup raidGroup) {
        try {
            RaidGroup deletedRaidGroup = raidRepository.deleteGroupInNewTransaction(raidGroup.getRaidId(), raidGroup.getId());
            if (deletedRaidGroup != null) {
                LOGGER.debug("Cleaned up raid group: " + deletedRaidGroup);
            } else {
                LOGGER.debug("Didn't delete raid group, it was apparantly deleted already.");
            }
        } catch (Throwable t) {
            // Ignore any other error and try the other server channels
            LOGGER.warn("Exception when cleaning up group " + raidGroup + ": " + t.getMessage());
        }
    }

    private boolean attachToOverviewMessageIfExists(Guild guild, Config config, String messageId,
                                                    MessageChannel channel, PokemonRaidStrategyService raidStrategyService) {
        try {
            if (channel.getMessageById(messageId).complete() != null) {
                final Locale locale = config.getLocale();
                final Callable<Boolean> overviewTask =
                        RaidOverviewCommand.getMessageRefreshingTaskToSchedule(
                                null, config.getServer(), messageId, localeService, locale, serverConfigRepository,
                                raidRepository, clockService, channel,
                                executorService, raidStrategyService);
                executorService.submit(overviewTask);
                LOGGER.info("Found overview message for channel " + channel.getName() +
                        " (server " + guild.getName() + "). Attaching to it.");
                return true;
            }
        } catch (UserMessedUpException e) {
            LOGGER.warn("Could not attach to message due to an error: " + e.getMessage());
        } catch (ErrorResponseException e) {
            // We couldn't find the message in this channel or had permission issues, ignore
        } catch (Throwable e) {
            // Ignore any other error and try the other server channels
        }
        return false;
    }
}
