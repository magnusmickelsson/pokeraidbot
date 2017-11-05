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
                    "- Kommando för att rapportera raid utifrån starttid och inte sluttid - *!raid start*\n" +
                    "- Nya bossarna har nu full counterdata så *!raid vs (pokemon)* " +
                    "ger ok resultat för alla tier 3+ bossar\n" +
                    "- Kan konfigurera mod roll per server som har rätt att göra det server admin har\n" +
                    "- EX-raidhantering finns nu i hjälpmanualen via *!raid man raid*";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- Command to report raid based on start time and not end - *!raid start*\n" +
                    "- New bosses now have full counter data for tier 3+\n" +
                    "- Possible to configure a mod role per server, so mods can do what the server admin can\n" +
                    "- EX raid handling added to help manual *!raid man raid*";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
