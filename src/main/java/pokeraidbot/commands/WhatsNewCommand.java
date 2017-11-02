package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
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
            message = "**Nytt i 1.0.0:**\n\n" +
                    "- Ny readme för EN och SV, inklusive nya bilder\n" +
                    "- \"Komma igång\"-guide på engelska\n" +
                    "- Raidgrupper tas nu bort efter raidtid + 5 minuter, och anmälningar till gruppen tas då bort\n" +
                    "- Raidöversikt, automatiskt uppdaterad var 60:e sekund (endast admin) - *!raid overview*\n" +
                    "- Raid map i serverchatt oavsett konfiguration: *!raid mapinchat {gym-namn}*\n" +
                    "- Feedbackmeddelande efter *!raid new* tas bort efter 15 sek\n" +
                    "- Persistent tracking (*!raid track* sparas i databas, " +
                    "varje användare kan ha 3 pokemons man spårar)\n" +
                    "- \"Fuzzy search\" för pokemonnamn (dvs den klarar vissa felstavningar)\n" +
                    "- Flytta tid för en raidgrupp: *!raid change group {ny tid} {gym-namn}*\n" +
                    "- Användare kan själva välja locale via: *!raid config locale={språkkod, t.ex. en eller sv}*\n" +
                    "- Kom-igång-guide - *!raid getting-started*\n" +
                    "- För serveradmins: parameter för att välja server default locale (en eller sv)";
        } else {
            message = "**New in 1.0.0:**\n\n" +
                    "- Fixed readme for both english and swedish including images\n" +
                    "- Fixed getting started guide for english locale\n" +
                    "- Raid groups now expire after raid time + 5 minutes, removing all signups for the group\n" +
                    "- Raid overview, automatically updated every 60 seconds (admin command) - *!raid overview*\n" +
                    "- Raid map in serverchat regardless of configuration: *!raid mapinchat {gym name}*\n" +
                    "- Feedback message after *!raid new* is removed after 15 sec\n" +
                    "- Persistent tracking (*!raid track* is saved in database, " +
                    "every user can have 3 pokemons to track)\n" +
                    "- \"Fuzzy search\" for pokemon name (the bot can handle some minor mistakes)\n" +
                    "- Change time for a raid group: *!raid change group {new time} {gym name}*\n" +
                    "- Users can choose their own locale via: *!raid config locale=" +
                    "{language code, for example en or sv}*\n" +
                    "- Getting started guide - *!raid getting-started*\n" +
                    "- For serveradmins: input parameter to application with default locale (en or sv)";
        }
        replyBasedOnConfig(config, commandEvent, message);
    }
}
