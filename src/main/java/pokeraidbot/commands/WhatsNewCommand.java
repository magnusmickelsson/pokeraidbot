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
            message = "**Nytt i 1.7.0-" + BotServerMain.version + ":**\n\n" +
                    "* Uppdaterad gymdata för Uppsala\n" +
                    "* Gym data för Dalarna\n" +
                    "* Fix: raidgrupper som rensades vid Discord API-problem (timeouts) ska hanteras bättre\n" +
                    "* Raid boss CP data och counter data uppdaterade för kommande bossar\n" +
                    "* CSV-datafiler fixade från specialtecken\n" +
                    "* Lade till en diff-rapport för gym data import\n" +
                    "* Slog av meddelandet som säger att botten är här\n" +
                    "* !raid status visar vem som skapade raiden\n" +
                    "* Lycksele gym data inlagt\n" +
                    "* Wailmer ny raidboss\n" +
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
            message = "**New in 1.7.0-" + BotServerMain.version + ":**\n\n" +
                    "* Updated gym for Uppsala\n" +
                    "* Gym data for Dalarna\n" +
                    "* Fix: raid group cleanup during Discord API timeouts should not lead to messages dying anymore\n" +
                    "* Raid boss CP data and counter data updated for coming raid bosses\n" +
                    "* Changing so CSV files now are separated by ; and not ,\n" +
                    "* Added a diff mechanism to gym data import\n" +
                    "* Turned off message that says Pokeraidbot is up after downtime\n" +
                    "* !raid status shows who created the raid\n" +
                    "* Lycksele gymdata added\n" +
                    "* Wailmer new raidboss\n" +
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
