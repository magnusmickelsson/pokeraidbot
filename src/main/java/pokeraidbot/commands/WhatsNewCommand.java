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
                    "- Fix: !raid track när en server har botintegration aktiverad ska nu funka som det ska\n" +
                    "- !raid group titel har nu raidstart och -slut\n" +
                    "- Fix: Eventlyssnare för grupper rensas undan bättre\n" +
                    "- !raid overview och !raid list är omgjorda, listar nu bland annat grupptider i stället för ETA\n" +
                    "- Gen3 pokemons finns nu inlagda (rapportera gärna om något är fel!)\n" +
                    "- Alias för vissa kommandon, så det blir mindre att skriva - t.ex. " +
                    "*!r s Tyra 10:00 hästen* är samma som *!raid start Tyranitar 10:00 hästen*\n" +
                    "- Bot bekräftar nu ok !raid group med check\n" +
                    "- Resistance-tabell, mer detaljerad/korrekt !raid vs\n" +
                    "- !raid track notifiering omgjord\n" +
                    "- Nya adminverktyg\n" +
                    "- Fix: Bättre felmeddelande vid !raid start zipit ger nu ett \"dålig syntax\"-meddelande\n" +
                    "- Fix: EX raids kommer inte längre skapas via botintegration";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- Fix: !raid track when a server has bot integration setting active should now work properly\n" +
                    "- Add raid duration to group message header\n" +
                    "- Fix: Better cleanup of event listeners\n" +
                    "- !raid overview and !raid list remade, for example they now list times for groups instead of ETA\n" +
                    "- Gen3 pokemons added to repository\n" +
                    "- Aliases for commands, to reduce typing during winter - for example " +
                    "*!r s Tyranitar 10:00 hasten* is the same as *!raid start Tyranitar 10:00 hasten*\n" +
                    "- Green checkmark on !raid group as bot confirmation\n" +
                    "- Resistances table, detailed\n" +
                    "- !raid track notification handling refactored\n" +
                    "- New admin tools\n" +
                    "- Fix: Error message when doing !raid start zipit is bad, now gives a bad syntax message\n" +
                    "- Fix: EX raids will no longer be created via bot integration";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
