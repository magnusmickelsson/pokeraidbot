package pokeraidbot;

import com.jagrosh.jdautilities.commandclient.CommandClient;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.jagrosh.jdautilities.commandclient.examples.PingCommand;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.commands.*;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRaidStrategyService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.EmoticonSignUpMessageListener;
import pokeraidbot.domain.tracking.TrackingCommandListener;
import pokeraidbot.infrastructure.botsupport.gymhuntr.GymHuntrRaidEventListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;
import pokeraidbot.jda.AggregateCommandListener;
import pokeraidbot.jda.EventLoggingListener;
import pokeraidbot.jda.SignupWithPlusCommandListener;
import pokeraidbot.jda.StartUpEventListener;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public class BotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);
    private final Set<EventListener> extraListeners = new CopyOnWriteArraySet<>();
    private String ownerId;
    private String token;
    private JDA botInstance;
    private CommandClient commandClient;
    private CommandListener aggregateCommandListener;
    private TrackingCommandListener trackingCommandListener;
    private GymRepository gymRepository;
    private ServerConfigRepository serverConfigRepository;
    private UserConfigRepository userConfigRepository;

    public BotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                      PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                      ServerConfigRepository serverConfigRepository, UserConfigRepository userConfigRepository,
                      ExecutorService executorService, ClockService clockService, String ownerId, String token,
                      TrackingCommandListener trackingCommandListener) {
        this.gymRepository = gymRepository;
        this.serverConfigRepository = serverConfigRepository;
        this.userConfigRepository = userConfigRepository;
        this.ownerId = ownerId;
        this.token = token;
        this.trackingCommandListener = trackingCommandListener;
        if (!System.getProperty("file.encoding").equals("UTF-8")) {
            System.err.println("ERROR: Not using UTF-8 encoding");
            System.exit(-1);
        }

        initializeConfig();

        EventWaiter waiter = new EventWaiter();
        EventLoggingListener eventLoggingListener = new EventLoggingListener();
        GymHuntrRaidEventListener gymHuntrRaidEventListener = new GymHuntrRaidEventListener(
                serverConfigRepository, raidRepository, gymRepository, pokemonRepository, localeService,
                executorService,
                clockService, this);
        StartUpEventListener startUpEventListener = new StartUpEventListener(serverConfigRepository,
                raidRepository, localeService, clockService, executorService, this, gymRepository, pokemonRepository);
        SignupWithPlusCommandListener plusCommandEventListener = new SignupWithPlusCommandListener(raidRepository,
                pokemonRepository, serverConfigRepository, this, localeService);
        aggregateCommandListener = new AggregateCommandListener(Arrays.asList(this.trackingCommandListener));

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(this.ownerId);
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        client.setPrefix("!raid ");
        client.setGame(Game.of("Type !raid usage"));
        client.addCommands(
                new WhatsNewCommand(serverConfigRepository, aggregateCommandListener, localeService),
                new HelpManualCommand(localeService, serverConfigRepository, aggregateCommandListener),
                new AboutCommand(
                        Color.BLUE, localeService.getMessageFor(LocaleService.AT_YOUR_SERVICE, LocaleService.DEFAULT),
                        new String[]{LocaleService.featuresString_SV}, Permission.ADMINISTRATOR
                ),
                new PingCommand(),
                new UsageCommand(localeService, serverConfigRepository, aggregateCommandListener),
                new GettingStartedCommand(localeService),
//                new ShutdownCommand(),
                new NewRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new NewRaidStartsAtCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new NewRaidExCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new UserConfigCommand(serverConfigRepository, trackingCommandListener, localeService,
                        userConfigRepository),
                new RaidStatusCommand(gymRepository, raidRepository, localeService,
                        serverConfigRepository, this, aggregateCommandListener, pokemonRepository),
                new RaidListCommand(raidRepository, localeService, serverConfigRepository, pokemonRepository,
                        aggregateCommandListener),
                new SignUpCommand(gymRepository, raidRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new WhereIsGymCommand(gymRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new WhereIsGymInChatCommand(gymRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new RemoveSignUpCommand(gymRepository, raidRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new PokemonVsCommand(pokemonRepository, raidInfoService, localeService, serverConfigRepository,
                        aggregateCommandListener),
                new ServerInfoCommand(serverConfigRepository, localeService, aggregateCommandListener, clockService),
                new DonateCommand(localeService, serverConfigRepository, aggregateCommandListener),
                new TrackPokemonCommand(this, serverConfigRepository, localeService, pokemonRepository,
                        aggregateCommandListener),
                new UnTrackPokemonCommand(this, serverConfigRepository, localeService, pokemonRepository,
                        aggregateCommandListener),
                new InstallCommand(serverConfigRepository, gymRepository),
                new InstallEmotesCommand(localeService),
                new AlterRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService, serverConfigRepository,
                        aggregateCommandListener, this),
                new NewRaidGroupCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener, this, clockService, executorService),
                new RaidOverviewCommand(raidRepository, localeService, serverConfigRepository, pokemonRepository,
                        aggregateCommandListener, clockService, executorService)
        );

        try {
            commandClient = client.build();
            botInstance = new JDABuilder(AccountType.BOT)
                    // set the token
                    .setToken(this.token)

                    // set the game for when the bot is loading
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of("loading..."))

                    // add the listeners
                    .addEventListener(waiter)
                    .addEventListener(commandClient)
//                    .addEventListener(eventLoggingListener)
                    .addEventListener(startUpEventListener)
                    .addEventListener(plusCommandEventListener)
                    .addEventListener(gymHuntrRaidEventListener)

                    // start it up!
                    .buildBlocking();
            for (EventListener extraListener : extraListeners) {
                botInstance.addEventListener(extraListener);
                LOGGER.info("Added extra event listener after initialization: " + extraListener);
            }
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void initializeConfig() {
        if (serverConfigRepository.findAll().size() == 0) {
            LOGGER.warn("Could not find any configuration in database, assuming fresh install. Creating basic server configurations..");
            // My test servers
            serverConfigRepository.save(new Config("manhattan_new_york", false, Locale.ENGLISH, "pokeraidbot_us_test"));
            serverConfigRepository.save(new Config("uppsala", "zhorhn tests stuff"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_lab2"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_stage"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_test"));

            // External user's servers
            serverConfigRepository.save(new Config("uppsala", "pokemon go uppsala"));
            serverConfigRepository.save(new Config("umeå", "pokémon go sverige admin"));
            serverConfigRepository.save(new Config("luleå", "pokémon luleå"));
            serverConfigRepository.save(new Config("ängelholm", "test pokemongo ängelholm"));
            serverConfigRepository.save(new Config("norrköping", true, "raid-test-nkpg"));
            serverConfigRepository.save(new Config("norrköping", true, "raid - pokemon go norrköping"));
            LOGGER.info("Server configurations created. Add more via the command for an administrator in a server where pokeraidbot has been added: !raid install");
        }
        gymRepository.reloadGymData();
    }

    public JDA getBot() {
        if (botInstance == null) {
            throw new IllegalStateException("Bot instance has not yet been initialized!");
        }
        return botInstance;
    }

    public CommandClient getCommandClient() {
        return commandClient;
    }

    public TrackingCommandListener getTrackingCommandListener() {
        return trackingCommandListener;
    }

    public void addExtraListenerToBeAddedAfterStartUp(EventListener listener) {
        extraListeners.add(listener);
    }

    public void addEmoticonEventListener(EmoticonSignUpMessageListener emoticonSignUpMessageListener) {
        if (botInstance == null) {
            addExtraListenerToBeAddedAfterStartUp(emoticonSignUpMessageListener);
        } else {
            botInstance.addEventListener(emoticonSignUpMessageListener);
        }
    }
}
