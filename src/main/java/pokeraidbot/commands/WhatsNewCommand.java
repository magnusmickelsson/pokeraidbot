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
                    "- Integration med Gymhuntr-bot - raider kan nu automatiskt skapas med hjälp av Gymhuntr " +
                    "(gymhuntr.com)\n" +
                    "- Bugfix: Raidgrupper kan inte starta innan aktuell tid inom raidtiden\n" +
                    "- Nytt utseende för !raid overview";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- Integration with Gymhuntr-bot - raids can now be automatically created via Gymhuntr " +
                    "(gymhuntr.com)\n" +
                    "- Bugfix: Raid groups can no longer be set before current time (within raid duration)\n" +
                    "- New appearance for !raid overview";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
