package pokeraidbot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.jagrosh.jdautilities.commandclient.examples.PingCommand;
import com.jagrosh.jdautilities.commandclient.examples.ShutdownCommand;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.impl.GameImpl;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Value;
import pokeraidbot.commands.*;
import pokeraidbot.domain.*;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class BotService {
    private String ownerId;
    private String token;

    public BotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                      PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                      ConfigRepository configRepository, String ownerId, String token) {
        this.ownerId = ownerId;
        this.token = token;
        if (!System.getProperty("file.encoding").equals("UTF-8")) {
            System.err.println("ERROR: Not using UTF-8 encoding");
            System.exit(-1);
        }

        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(this.ownerId);
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
                new ShutdownCommand(),
                new NewRaidCommand(gymRepository, raidRepository, pokemonRepository, localeService,
                        configRepository),
                new RaidStatusCommand(gymRepository, raidRepository, localeService,
                        configRepository),
                new RaidListCommand(raidRepository, localeService, configRepository),
                new SignUpCommand(gymRepository, raidRepository, localeService,
                        configRepository),
                new WhereIsGymCommand(gymRepository, localeService,
                        configRepository),
                new RemoveSignUpCommand(gymRepository, raidRepository, localeService,
                        configRepository),
                new PokemonVsCommand(pokemonRepository, raidInfoService, localeService)
        );

        try {
            new JDABuilder(AccountType.BOT)
                    // set the token
                    .setToken(this.token)

                    // set the game for when the bot is loading
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of("loading..."))

                    // add the listeners
                    .addEventListener(waiter)
                    .addEventListener(client.build())

                    // start it up!
                    .buildBlocking();
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
