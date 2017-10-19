package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class WhatsNewCommand extends ConfigAwareCommand {
    public WhatsNewCommand(ConfigRepository configRepository, CommandListener commandListener,
                           LocaleService localeService) {
        super(configRepository, commandListener, localeService);
        this.name = "whatsnew";
        this.aliases = new String[]{"latest", "version"};
        this.help = localeService.getMessageFor(LocaleService.WHATS_NEW_HELP, localeService.getLocaleForUser((User) null));
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String message;
        if (config.getLocale().equals(LocaleService.SWEDISH)) {
            message = "**Nytt i 0.9.0 (2017-10-13):**\n\n" +
                    "- Man kan nu göra signup via *+1 09:45 Hästen* (+{antal} {tid man kommer (HH:mm)} {gym})\n" +
                    "- Vissa feedbackmeddelanden rensas nu automatiskt, på samma sätt som felmeddelanden.";
        } else {
            message = "**New in 0.9.0 (2017-10-13):**\n\n" +
                    "- You can now signup via commanda *+1 09:45 Cafe Lalo* (+{number of people} {ETA (HH:mm)} {gym})\n" +
                    "- Some feedback messages are now deleted automatically, just like error messages.";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
