package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

/**
 * !raid whatsnew
 */
public class WhatsNewCommand extends ConfigAwareCommand {
    public WhatsNewCommand(ServerConfigRepository serverConfigRepository, CommandListener commandListener,
                           LocaleService localeService) {
        super(serverConfigRepository, commandListener, localeService);
        this.name = "whatsnew";
        this.aliases = new String[]{"latest", "version"};
        this.help = localeService.getMessageFor(LocaleService.WHATS_NEW_HELP, localeService.getLocaleForUser((User) null));
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String message;
        if (config.getLocale().equals(LocaleService.SWEDISH)) {
            message = "**Nytt i " + BotServerMain.version + ":**\n\n" +
                    "* Möjligt att markera gymnamn om de är potentiella EX gyms (kräver viss extradata)\n" +
                    "* Celebi och Mew preppade som nya bossar\n";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "* Possible to mark gymnames if they are potential EX raid gyms (requires extra data)\n" +
                    "* Celebi and Mew prepared as new raid bosses\n";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
