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
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.EmoticonSignUpMessageListener;
import pokeraidbot.domain.tracking.TrackingService;
import pokeraidbot.infrastructure.botsupport.gymhuntr.GymHuntrRaidEventListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;
import pokeraidbot.jda.AggregateCommandListener;
import pokeraidbot.jda.SignupWithPlusCommandListener;
import pokeraidbot.jda.StartUpEventListener;
import pokeraidbot.jda.UnsignWithMinusCommandListener;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public class BotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);
    private final Set<EventListener> extraListeners = new CopyOnWriteArraySet<>();
    private TrackingService trackingService;
    private String ownerId;
    private String token;
    private JDA botInstance;
    private CommandClient commandClient;
    private CommandListener aggregateCommandListener;
    private GymRepository gymRepository;
    private ServerConfigRepository serverConfigRepository;
    private UserConfigRepository userConfigRepository;
    public static List<String> currentTier5Bosses = new CopyOnWriteArrayList<>();
    static {
        if (LocalDate.now().isBefore(LocalDate.of(2018, Month.MARCH, 17))) {
            currentTier5Bosses.add("Rayquaza");
        }

        if (LocalDate.now().isBefore(LocalDate.of(2018, Month.APRIL, 3))) {
            currentTier5Bosses.add("Lugia");
        }

        if (LocalDate.now().isAfter(LocalDate.of(2018, Month.APRIL, 2))) {
            currentTier5Bosses.add("Ho-Oh");
        }
    }

    public BotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                      PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                      ServerConfigRepository serverConfigRepository, UserConfigRepository userConfigRepository,
                      ExecutorService executorService, ClockService clockService, TrackingService trackingService,
                      String ownerId, String token) {
        this.gymRepository = gymRepository;
        this.serverConfigRepository = serverConfigRepository;
        this.userConfigRepository = userConfigRepository;
        this.trackingService = trackingService;
        this.ownerId = ownerId;
        this.token = token;
        if (!System.getProperty("file.encoding").equals("UTF-8")) {
            System.err.println("ERROR: Not using UTF-8 encoding");
            System.exit(-1);
        }

        initializeConfig();

        EventWaiter waiter = new EventWaiter();
        // For detailed logging - used during debugging/development
//        EventLoggingListener eventLoggingListener = new EventLoggingListener();
        GymHuntrRaidEventListener gymHuntrRaidEventListener = new GymHuntrRaidEventListener(
                serverConfigRepository, raidRepository, gymRepository, pokemonRepository, localeService,
                executorService,
                clockService, this, raidInfoService);
        StartUpEventListener startUpEventListener = new StartUpEventListener(serverConfigRepository,
                raidRepository, localeService, clockService, executorService, this, gymRepository,
                pokemonRepository, raidInfoService);
        SignupWithPlusCommandListener plusCommandEventListener = new SignupWithPlusCommandListener(raidRepository,
                pokemonRepository, serverConfigRepository, this, localeService);
        UnsignWithMinusCommandListener minusCommandEventListener = new UnsignWithMinusCommandListener(raidRepository,
                pokemonRepository, serverConfigRepository, this, localeService);
        aggregateCommandListener = new AggregateCommandListener(Arrays.asList());

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(this.ownerId);
        client.setEmojis(Emotes.OK, "\uD83D\uDE2E", Emotes.ERROR);
        client.setPrefix("!raid ");
        client.setAlternativePrefix("!r ");
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
                new GettingStartedCommand(localeService, serverConfigRepository, aggregateCommandListener),
                new AdminCommands(userConfigRepository, serverConfigRepository, gymRepository,
                        this, trackingService, localeService, pokemonRepository, raidRepository),
                new NewRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new NewRaidStartsAtCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new NewRaidExCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new UserConfigCommand(serverConfigRepository, aggregateCommandListener, localeService,
                        userConfigRepository),
                new RaidStatusCommand(gymRepository, raidRepository, localeService,
                        serverConfigRepository, aggregateCommandListener),
                new RaidListCommand(raidRepository, localeService, serverConfigRepository, pokemonRepository,
                        aggregateCommandListener),
                new PotentialExRaidListCommand(raidRepository, localeService, serverConfigRepository,
                        gymRepository, aggregateCommandListener),
                new ExRaidListCommand(localeService, serverConfigRepository,
                        gymRepository, aggregateCommandListener),
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
                new TrackPokemonCommand(serverConfigRepository, localeService, pokemonRepository,
                        trackingService, aggregateCommandListener),
                new UnTrackPokemonCommand(serverConfigRepository, localeService, pokemonRepository,
                        aggregateCommandListener, trackingService),
                new InstallCommand(serverConfigRepository, gymRepository),
                new InstallEmotesCommand(localeService),
                new AlterRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService, serverConfigRepository,
                        aggregateCommandListener, this),
                new NewRaidGroupCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener, this, clockService,
                        executorService, raidInfoService),
                new StartRaidAndCreateGroupCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository, aggregateCommandListener, this, clockService,
                        executorService, raidInfoService),
                new EggHatchedCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        serverConfigRepository,
                        aggregateCommandListener, raidInfoService),
                new RaidOverviewCommand(raidRepository, localeService, serverConfigRepository,
                        aggregateCommandListener, clockService, executorService, raidInfoService)
        );

        try {
            commandClient = client.build();
            botInstance = new JDABuilder(AccountType.BOT)
                    // set the token
                    .setToken(this.token)

                    // set the game for when the bot is loading
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of("loading..."))

                    // Network-related settings
                    .setRequestTimeoutRetry(true)
                    .setAutoReconnect(true)

                    // add the listeners
                    .addEventListener(waiter)
                    .addEventListener(commandClient)
//                    .addEventListener(eventLoggingListener)
                    .addEventListener(startUpEventListener)
                    .addEventListener(plusCommandEventListener)
                    .addEventListener(minusCommandEventListener)
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
            LOGGER.warn("Could not find any configuration in database, assuming fresh install. " +
                    "Creating basic server configurations..");
            // My test servers
            serverConfigRepository.save(new Config("manhattan_new_york", false,
                    Locale.ENGLISH, "pokeraidbot_us_test"));
            serverConfigRepository.save(new Config("uppsala", "zhorhn tests stuff"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_lab2"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_stage"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_test"));

            LOGGER.info("Server configurations created. Add more via the command for an administrator " +
                    "in a server where pokeraidbot has been added: !raid install");
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
