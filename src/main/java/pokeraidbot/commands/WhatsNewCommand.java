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
            message = "**Nytt i 1.9.0" //-" + BotServerMain.version
                    + ":**\n\n" +
                    "* Nya raidbossar\n" +
                    "* Gym data uppdaterad\n" +
                    "* Snooze-funktion för raidgrupper (admin eller gruppskapare)\n" +
                    "* Sortering av EX gym fixad\n";
        } else {
            message = "**New in 1.9.0-" //+ BotServerMain.version
                    + ":**\n\n" +
                    "* New raidbosses\n" +
                    "* Gym data updated\n" +
                    "* Snooze function for raidgroups (admin or group creator)\n" +
                    "* Sorting of EX gyms fixed\n";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
