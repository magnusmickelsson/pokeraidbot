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
                    "* Bilder funkar nu även för gen 3 pokemons\n" +
                    "* Nya gym inlagda\n" +
                    "* Fix: fel kommer inte längre leda till att raid untrack/track-förändringar rullas tillbaka i databasen\n" +
                    "* !r sg (mon) (time) (gym) - kombinerat kommando för att skapa raid och grupp på samma gång\n" +
                    "* !raid change group remove {eventuellt: time} {gym} för att ta bort raidgrupp, både meddelande " +
                    "och i databasen - användare ska kunna göra detta själva om inga anmälningar finns till gruppen\n" +
                    "* !raid change remove tar bort alla relaterade grupper och deras meddelanden\n" +
                    "* Dokumentation kring hur man sätter upp integration mot Gymhuntr och PokeAlarm\n" +
                    "* Dokumentation - hantering av raidägg inlagd i !raid getting-started\n";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "* Correct pictures for pokemon, including gen 3\n" +
                    "* New gym data for many regions\n" +
                    "* Fix so that exceptions don't lead to raid untrack/track database update rollback for the user's change\n" +
                    "* !r sg (mon) (time) (gym) - Combined command to create raid starting at, and creating a group at the same time\n" +
                    "* !raid change group remove {optional: time} {gym} to delete group, both message and database entry - user should " +
                    "be able to do this if no people signed up for the group\n" +
                    "* !raid change remove should lead to any related group messages being removed as well\n" +
                    "* howto-documentation for PokeAlarm and Gymhuntr for helping with botintegration setup\n" +
                    "* add egg handling to getting started documentation\n";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
