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
                    "- Feedbackhanteringsstrategier möjligt (se *!raid install*)\n" +
                    "- Integration med Gymhuntr och PokeAlarm - raider kan nu automatiskt skapas. " +
                    "Se <https://gymhuntr.com> och <https://github.com/PokeAlarm/PokeAlarm>\n" +
                    "- Bugfix: Raidgrupper kan inte starta innan aktuell tid inom raidtiden\n" +
                    "- Nytt utseende för !raid overview";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- Feedback handling strategies possible (see *!raid install*)\n" +
                    "- Integration with Gymhuntr and PokeAlarm - raids can now be automatically created. See " +
                    "<https://gymhuntr.com> and <https://github.com/PokeAlarm/PokeAlarm>\n" +
                    "- Bugfix: Raid groups can no longer be set before current time (within raid duration)\n" +
                    "- New appearance for !raid overview";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
