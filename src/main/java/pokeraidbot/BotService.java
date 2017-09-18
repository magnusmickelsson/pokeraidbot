package pokeraidbot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.jagrosh.jdautilities.commandclient.examples.PingCommand;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.impl.GameImpl;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pokeraidbot.commands.*;
import pokeraidbot.domain.*;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class BotService {
    @Autowired
    public BotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                      PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService) {
        if (!System.getProperty("file.encoding").equals("UTF-8")) {
            System.err.println("ERROR: Not using UTF-8 encoding");
            System.exit(-1);
        }

        // todo: turn into spring resource bundle
        final InputStream propsAsStream = BotService.class.getResourceAsStream("/pokeraidbot.properties");
        Properties properties = new Properties();
        try {
            properties.load(propsAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(properties.getProperty("ownerId"));
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        client.setPrefix("!raid ");
        client.setGame(new GameImpl("Type !raid usage", "", Game.GameType.DEFAULT));
        client.addCommands(
                new AboutCommand(
                        Color.BLUE, localeService.getMessageFor(LocaleService.AT_YOUR_SERVICE, LocaleService.DEFAULT),
                        new String[]{LocaleService.featuresString_SV}, Permission.ADMINISTRATOR
                ),
                new PingCommand(),
                new HelpCommand(localeService),
//                new ShutdownCommand(),
//                new NewRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService),
//                new RaidStatusCommand(gymRepository, raidRepository, localeService),
//                new RaidListCommand(raidRepository, localeService),
//                new SignUpCommand(gymRepository, raidRepository, localeService),
                new WhereIsGymCommand(gymRepository, localeService),
//                new RemoveSignUpCommand(gymRepository, raidRepository, localeService),
                new PokemonVsCommand(pokemonRepository, raidInfoService, localeService)
        );

        try {
            new JDABuilder(AccountType.BOT)
                    // set the token
                    .setToken(properties.getProperty("token"))

                    // set the game for when the bot is loading
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of("loading..."))

                    // add the listeners
                    .addEventListener(waiter)
                    .addEventListener(client.build())

                    // start it up!
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            throw new RuntimeException(e);
        }
    }
}
