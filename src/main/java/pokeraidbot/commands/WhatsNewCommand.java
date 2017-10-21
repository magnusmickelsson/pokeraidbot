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
            message = "**Nytt i 1.0.0:**\n\n" +
                    "- Ny readme för EN och SV, inklusive nya bilder\n" +
                    "- \"Komma igång\"-guide på engelska\n" +
                    "- Raidgrupper tas nu bort efter raidtid + 5 minuter, och anmälningar till gruppen tas då bort\n" +
                    "- Raidöversikt, automatiskt uppdaterad var 60:e sekund - !raid overview\n" +
                    "- Raid map i serverchatt oavsett konfiguration: !raid mapinchat {gym-namn}\n" +
                    "- Parameter för att välja server default locale (en eller sv)";
        } else {
            message = "**New in 1.0.0:**\n\n" +
                    "- Fixed readme for both english and swedish including images\n" +
                    "- Fixed getting started guide for english locale\n" +
                    "- Raid groups now expire after raid time + 5 minutes, removing all signups for the group\n" +
                    "- Raid overview, automatically updated every 60 seconds - !raid overview\n" +
                    "- Raid map in serverchat regardless of configuration: !raid mapinchat {gym name}\n" +
                    "- Input parameter to application with default locale (en or sv)";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
