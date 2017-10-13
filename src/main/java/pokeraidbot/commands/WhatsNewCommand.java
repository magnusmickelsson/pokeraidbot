package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class WhatsNewCommand extends ConfigAwareCommand {
    public WhatsNewCommand(ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.name = "whatsnew";
        this.aliases = new String[]{"latest", "version"};
        // todo: i18n
        this.help = "Ange vilken version av botten som körs, och vad som är nytt i denna version.";
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String message = "**Nytt i 0.9.0 (2017-10-13):**\n\n" +
                "- Man kan nu göra signup via *+1 09:45 Hästen* (+{antal} {tid man kommer (HH:mm)} {gym})\n" +
                "- Vissa feedbackmeddelanden rensas nu automatiskt, på samma sätt som felmeddelanden.";
        replyBasedOnConfig(config, commandEvent, message);
    }
}
