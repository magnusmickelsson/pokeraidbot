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
                    "- Medlemmar i mods gruppen kan nu ändra grupptider för andra användare (så länge det inte blir konflikter)\n" +
                    "- Flyttade raidtid för grupper från titeln till beskrivningen i embed message för att undvika konstig layout på Android\n" +
                    "- !raid overview och !raid list - bara första datumet visas, för att spara plats";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- Members of server mods group can now change group time\n" +
                    "- Moved raid time from group message title to description to avoid no linebreaks on Android\n" +
                    "- !raid overview and !raid list - only the first date is shown, to save space";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
