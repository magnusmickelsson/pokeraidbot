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
                    "- Bättre felhantering kring skickandet av meddelanden i samband med !raid track\n" +
                    "- För att nollställa serverns raidöversikt och kunna skapa om den - *!raid overview reset*";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- Better error handling in relation to sending messages triggered via !raid track\n" +
                    "- To reset the server's raid overview to be able to recreate it - run *!raid overview reset*";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
