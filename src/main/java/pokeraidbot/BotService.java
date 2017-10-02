package pokeraidbot;

import com.jagrosh.jdautilities.commandclient.CommandClient;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.jagrosh.jdautilities.commandclient.examples.PingCommand;
import com.jagrosh.jdautilities.commandclient.examples.ShutdownCommand;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.commands.*;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRaidStrategyService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.EmoticonMessageListener;
import pokeraidbot.domain.tracking.TrackingCommandListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;
import pokeraidbot.jda.AggregateCommandListener;
import pokeraidbot.jda.EventLoggingListener;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Arrays;

public class BotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);

    private String ownerId;
    private String token;
    private JDA botInstance;
    private CommandClient commandClient;
    private CommandListener aggregateCommandListener;
    private TrackingCommandListener trackingCommandListener;
    private GymRepository gymRepository;
    private ConfigRepository configRepository;

    public BotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                      PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                      ConfigRepository configRepository, String ownerId, String token) {
        this.gymRepository = gymRepository;
        this.configRepository = configRepository;
        this.ownerId = ownerId;
        this.token = token;
        if (!System.getProperty("file.encoding").equals("UTF-8")) {
            System.err.println("ERROR: Not using UTF-8 encoding");
            System.exit(-1);
        }

        EventWaiter waiter = new EventWaiter();
        EventLoggingListener eventLoggingListener = new EventLoggingListener();
        EmoticonMessageListener emoticonMessageListener = new EmoticonMessageListener(this, localeService,
                configRepository, raidRepository, pokemonRepository, gymRepository);
        trackingCommandListener = new TrackingCommandListener(configRepository, localeService);
        aggregateCommandListener = new AggregateCommandListener(Arrays.asList(trackingCommandListener));
//                new EmoticonMessageListener(this, localeService, configRepository, raidRepository,
//                        pokemonRepository, gymRepository)));

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(this.ownerId);
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        client.setPrefix("!raid ");
        client.setGame(Game.of("Type !raid usage"));
        client.addCommands(
                new AboutCommand(
                        Color.BLUE, localeService.getMessageFor(LocaleService.AT_YOUR_SERVICE, LocaleService.DEFAULT),
                        new String[]{LocaleService.featuresString_SV}, Permission.ADMINISTRATOR
                ),
                new PingCommand(),
                new UsageCommand(localeService, configRepository, aggregateCommandListener),
                new ShutdownCommand(),
                new NewRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        configRepository, aggregateCommandListener),
                new NewRaidExCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        configRepository, aggregateCommandListener),
                new RaidStatusCommand(gymRepository, raidRepository, localeService,
                        configRepository, this, aggregateCommandListener),
                new RaidListCommand(raidRepository, localeService, configRepository, pokemonRepository,
                        aggregateCommandListener),
                new SignUpCommand(gymRepository, raidRepository, localeService,
                        configRepository, aggregateCommandListener),
                new WhereIsGymCommand(gymRepository, localeService,
                        configRepository, aggregateCommandListener),
                new RemoveSignUpCommand(gymRepository, raidRepository, localeService,
                        configRepository, aggregateCommandListener),
                new PokemonVsCommand(pokemonRepository, raidInfoService, localeService, configRepository,
                        aggregateCommandListener),
                new ServerInfoCommand(configRepository, localeService, aggregateCommandListener),
                new DonateCommand(localeService, configRepository, aggregateCommandListener),
                new TrackPokemonCommand(this, configRepository, localeService, pokemonRepository,
                        aggregateCommandListener),
                new UnTrackPokemonCommand(this, configRepository, localeService, pokemonRepository,
                        aggregateCommandListener),
                new InstallCommand(configRepository, gymRepository),
                new InstallEmotesCommand(),
                new AlterRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService, configRepository,
                        aggregateCommandListener),
                new NewRaidGroupCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        configRepository, aggregateCommandListener, this),
                new HelpTopicCommand(localeService, configRepository, aggregateCommandListener)
        );

        try {
            commandClient = client.build();
//            commandClient.setListener(aggregateCommandListener);
            botInstance = new JDABuilder(AccountType.BOT)
                    // set the token
                    .setToken(this.token)

                    // set the game for when the bot is loading
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of("loading..."))

                    // add the listeners
                    .addEventListener(waiter)
                    .addEventListener(commandClient)
                    .addEventListener(eventLoggingListener)
                    .addEventListener(emoticonMessageListener)

                    // start it up!
                    .buildBlocking();
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    @Transactional
    public void initializeConfig() {
        if (configRepository.findAll().size() == 0) {
            LOGGER.warn("Could not find any configuration in database, assuming fresh install. Creating basic server configurations..");
            // My test servers
            configRepository.save(new Config("uppsala", "zhorhn tests stuff"));
            configRepository.save(new Config("uppsala", "pokeraidbot_lab"));
            configRepository.save(new Config("uppsala", "pokeraidbot_lab2"));
            configRepository.save(new Config("uppsala", "pokeraidbot_stage"));
            configRepository.save(new Config("uppsala", "pokeraidbot_test"));

            // External user's servers
            configRepository.save(new Config("luleå", "pokémon luleå"));
            configRepository.save(new Config("ängelholm", "test pokemongo ängelholm"));
            configRepository.save(new Config("norrköping", true, "raid-test-nkpg"));
            configRepository.save(new Config("norrköping", true, "raid - pokemon go norrköping"));
            LOGGER.info("Server configurations created. Add more via the command for an administrator in a server where pokeraidbot has been added: !raid install");
        }
        gymRepository.reloadGymData();
    }

    public JDA getBot() {
        return botInstance;
    }

    public CommandClient getCommandClient() {
        return commandClient;
    }

    public TrackingCommandListener getTrackingCommandListener() {
        return trackingCommandListener;
    }
//    public CommandListener getAggregateCommandListener() {
//        return aggregateCommandListener;
//    }
}
