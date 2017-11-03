package pokeraidbot.jda;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.commands.RaidOverviewCommand;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.List;
import java.util.concurrent.*;

public class StartUpEventListener implements EventListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpEventListener.class);
    private ServerConfigRepository serverConfigRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final ClockService clockService;
    protected static final ExecutorService executorService = new ThreadPoolExecutor(100, Integer.MAX_VALUE,
            65L, TimeUnit.SECONDS,
            new LinkedTransferQueue<>());

    // todo: pass thread pool as an argument, so we have the same pool for all worker threads
    public StartUpEventListener(ServerConfigRepository serverConfigRepository,
                                RaidRepository raidRepository, LocaleService localeService,
                                ClockService clockService) {
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.clockService = clockService;
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
                }
            }
        }
    }

    private boolean getAndAttachToOverviewMessageIfExists(Guild guild, Config config, String messageId, MessageChannel channel) {
        try {
            if (channel.getMessageById(messageId).complete() != null) {
                final Callable<Boolean> overviewTask =
                        RaidOverviewCommand.getMessageRefreshingTaskToSchedule(
                                null, config, messageId, localeService, serverConfigRepository,
                                raidRepository, clockService, channel,
                                executorService);
                executorService.submit(overviewTask);
                LOGGER.info("Found overview message for channel " + channel.getName() +
                        " (server " + guild.getName() + "). Attaching to it.");
                if (guild.getDefaultChannel() != null) {
                    guild.getDefaultChannel().sendMessage(
                            localeService.getMessageFor(LocaleService.OVERVIEW_ATTACH,
                                    config.getLocale(),
                                    channel.getName())).queue();
                }
                return true;
            }
        } catch (UserMessedUpException e) {
            channel.sendMessage(e.getMessage()).queue();
        } catch (ErrorResponseException e) {
            // We couldn't find the message in this channel or had permission issues, ignore
        } catch (Throwable e) {
            // Ignore any other error and try the other server channels
        }
        return false;
    }
}
