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
                    "- Bugfix rörande rättighetskontroller för rapportering av kläckta ägg\n" +
                    "- Kunna rapportera raidägg (Egg1-5 funkar som pokemons för alla raidkommandon)," +
                    " och rapportera kläckning när man vet vad det blir, " +
                    "via *!raid hatch {pokemon} {gym}*\n" +
                    "- Botintegrationen kan nu rapportera ägg enligt ovan, och automatiskt göra !raid hatch " +
                    "när PokeAlarm eller Gymhuntr rapporterar vad det blev\n" +
                    "- Admins och mods ska kunna ta bort raider även om de har anmälningar (om något blir riktigt fel)";
        } else {
            message = "**New in " + BotServerMain.version + ":**\n\n" +
                    "- Bugfix related to access rights for egg hatching\n" +
                    "- Report and create raids and groups for eggs (Egg1-5 works as pokemons for all raid commands)" +
                    ", and be able to hatch them when we know what " +
                    "they are, via *!raid hatch {pokemon} {gym}*\n" +
                    "- Bot integration can now report eggs, and automatically report what " +
                    "hatched as soon as the bot reports it\n" +
                    "- Admins and mods should be able to remove raids even if they have signups";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
