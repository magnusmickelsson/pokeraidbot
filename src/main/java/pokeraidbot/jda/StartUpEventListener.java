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
import pokeraidbot.domain.pokemon.PokemonRaidStrategyService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.EmoticonSignUpMessageListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

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
    private final UserConfigRepository userConfigRepository;

    public StartUpEventListener(ServerConfigRepository serverConfigRepository,
                                RaidRepository raidRepository, LocaleService localeService,
                                ClockService clockService, ExecutorService executorService, BotService botService,
                                GymRepository gymRepository, PokemonRepository pokemonRepository,
                                PokemonRaidStrategyService pokemonRaidStrategyService,
                                UserConfigRepository userConfigRepository) {
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.clockService = clockService;
        this.executorService = executorService;
        this.botService = botService;
        this.gymRepository = gymRepository;
        this.pokemonRepository = pokemonRepository;
        this.pokemonRaidStrategyService = pokemonRaidStrategyService;
        this.userConfigRepository = userConfigRepository;
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
                            getAndAttachToOverviewMessageIfExists(guild, config, messageId, channel);
                        }
                    }

                    final List<RaidGroup> groupsForServer = raidRepository.getGroupsForServer(config.getServer());
                    for (RaidGroup group : groupsForServer) {
                        getAndAttachToGroupMessageIfItExists(guild, config, group);
                    }
                }

                // Try to resend last message and catch it - as part of tests
//                if (guild.getName().toLowerCase().contains("pokeraidbot")) {
//                    final TextChannel textChannel = guild.getTextChannels().stream().filter(
//                            c -> c.getName().toLowerCase().contains("general")).findFirst().get();
//                    try {
//                        textChannel.getHistory().retrievePast(1).queue(l -> {
//                            l.stream().forEach(m -> textChannel.sendMessage(m).queue(msg -> {
//                                System.out.println("Message sent: " + msg);
//                            }, msg -> {
//                                System.out.println("Failed to send message: " + msg);
//                            }));
//                        });
//                    } catch (Throwable t) {
//                        System.out.println("Error: " + t.getMessage());
//                    }
//                }
            }
        }
    }

    private boolean getAndAttachToGroupMessageIfItExists(Guild guild, Config config,
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
            // todo: change to only use one message listener
            if (channel.getMessageById(raidGroup.getEmoteMessageId()).complete() != null &&
                    channel.getMessageById(raidGroup.getInfoMessageId()).complete() != null) {
                final Locale locale = config.getLocale();
                Raid raid = raidRepository.getById(raidGroup.getRaidId());
                final EmoticonSignUpMessageListener emoticonSignUpMessageListener =
                        new EmoticonSignUpMessageListener(botService, localeService, serverConfigRepository,
                                raidRepository, pokemonRepository, gymRepository, userConfigRepository,
                                raid.getId(), raidGroup.getStartsAt(),
                                raidGroup.getCreatorId());
                emoticonSignUpMessageListener.setEmoteMessageId(raidGroup.getEmoteMessageId());
                emoticonSignUpMessageListener.setInfoMessageId(raidGroup.getInfoMessageId());
                final int delayTime = raid.isExRaid() ? 1 : 15;
                final TimeUnit delayTimeUnit = raid.isExRaid() ? TimeUnit.MINUTES : TimeUnit.SECONDS;
                final Callable<Boolean> overviewTask =
                        NewRaidGroupCommand.getMessageRefreshingTaskToSchedule(channel, raid,
                                emoticonSignUpMessageListener,
                                raidGroup.getInfoMessageId(), locale, raidRepository, pokemonRaidStrategyService,
                                localeService,
                                clockService, executorService, botService, delayTimeUnit, delayTime, raidGroup.getId());
                executorService.submit(overviewTask);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found group message for raid " + raid + " in channel " + channel.getName() +
                            " (server " + guild.getName() + "). Attaching to it.");
                }
                return true;
            } else {
                cleanUpRaidGroup(raidGroup);
            }
        } catch (UserMessedUpException e) {
            if (channel != null)
                channel.sendMessage(e.getMessage()).queue(m -> {
                    m.delete().queueAfter(BotServerMain.timeToRemoveFeedbackInSeconds, TimeUnit.SECONDS);
                });
        } catch (ErrorResponseException e) {
            // We couldn't find the message in this channel or had permission issues, ignore
            LOGGER.debug("Caught exception: " + e.getMessage());
        } catch (Throwable e) {
            cleanUpRaidGroup(raidGroup);
            // Ignore any other error and try the other server channels
            LOGGER.debug("Caught exception: " + e.getMessage());
        }
        return false;
    }

    private void cleanUpRaidGroup(RaidGroup raidGroup) {
        try {
            raidRepository.deleteGroupInNewTransaction(raidGroup.getRaidId(), raidGroup.getId());
            LOGGER.debug("Cleaned up raid group: " + raidGroup);
        } catch (Throwable t) {
            // Ignore any other error and try the other server channels
            LOGGER.warn("Exception when cleaning up group " + raidGroup + ": " + t.getMessage());
        }
    }

    private boolean getAndAttachToOverviewMessageIfExists(Guild guild, Config config, String messageId,
                                                          MessageChannel channel) {
        try {
            if (channel.getMessageById(messageId).complete() != null) {
                final Locale locale = config.getLocale();
                final Callable<Boolean> overviewTask =
                        RaidOverviewCommand.getMessageRefreshingTaskToSchedule(
                                null, config.getServer(), messageId, localeService, locale, serverConfigRepository,
                                raidRepository, clockService, channel,
                                executorService);
                executorService.submit(overviewTask);
                LOGGER.info("Found overview message for channel " + channel.getName() +
                        " (server " + guild.getName() + "). Attaching to it.");
                if (guild.getDefaultChannel() != null) {
                    guild.getDefaultChannel().sendMessage(
                            localeService.getMessageFor(LocaleService.OVERVIEW_ATTACH,
                                    locale,
                                    channel.getName())).queue(m -> {
                                        m.delete().queueAfter(BotServerMain.timeToRemoveFeedbackInSeconds,
                                                TimeUnit.SECONDS);
                    });
                }
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
