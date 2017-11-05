package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

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
                    "- Nya bossarna inlagda med max CP (dock ej full counterdata)\n" +
                    "- Raidtimer numera 45 minuter i stället för 60\n" +
                    "- Fullt stöd för EX raider - använd *!raid ex* istället för !raid new";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- New bosses added with max CP (but not full counter data)\n" +
                    "- Raid timer now 45 minutes instead of 60\n" +
                    "- Support for EX raids - use *!raid ex* instead of !raid new";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
