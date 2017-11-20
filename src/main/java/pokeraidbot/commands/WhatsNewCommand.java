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
                    "* Bugfix: !raid overview borde funka bättre nu, inte kräva manuell uppstädning och få fel i " +
                    "loggen\n" +
                    "* !raid overview är inte längre ett svar utan ett eget meddelande, så originalkommandot " +
                    "kan tas bort utan att ta bort översikten\n\n" +
                    "**Nytt i 1.4.0:**\n\n" +
                    "- Feedbackhanteringsstrategier möjligt (se *!raid install*)\n" +
                    "- Standardtid innan feedbackmeddelanden tas bort ökad från 20 till 30 sekunder\n" +
                    "- Integration med Gymhuntr och PokeAlarm - raider kan nu automatiskt skapas. " +
                    "Se <https://gymhuntr.com> och <https://github.com/PokeAlarm/PokeAlarm>\n" +
                    "- Bugfix: Raidgrupper kan inte starta innan aktuell tid inom raidtiden\n" +
                    "- Man kan bara skapa en raidgrupp per användare och raid\n" +
                    "- Man kan inte skapa flera raidgrupper för samma tid och raid\n" +
                    "- Man kan nu avanmäla sig från en raid via *-2 {gym}*, t.ex. *-2 Solna Platform*";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "* Bugfix: !raid overview fixes that hopefully sort out the problem where it stops " +
                    "working and ends up in a state that " +
                    "needs manual cleanup and lots of exceptions in logs\n" +
                    "* !raid overview is no longer a reply but its own message, so the original command can be " +
                    "removed without removing overview\n\n" +
                    "**New in 1.4.0:**\n\n" +
                    "- Feedback handling strategies possible (see *!raid install*)\n" +
                    "- Default time for feedback messages to be removed increased from 20 to 30 seconds\n" +
                    "- Integration with Gymhuntr and PokeAlarm - raids can now be automatically created. See " +
                    "<https://gymhuntr.com> and <https://github.com/PokeAlarm/PokeAlarm>\n" +
                    "- Bugfix: Raid groups can no longer be set before current time (within raid duration)\n" +
                    "- You can no longer create more than one raidgroup per user and raid\n" +
                    "- You can no longer create more than one raidgroup for the same time and raid\n" +
                    "- You can now unsign from a raid via *-2 {gym}*, for example *-2 Solna Platform*";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
