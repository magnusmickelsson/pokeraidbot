package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class GettingStartedCommand extends ConfigAwareCommand {
    public GettingStartedCommand(LocaleService localeService, ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.name = "getting-started";
        this.aliases = new String[]{"get-started"};
        this.guildOnly = false;
        this.help = localeService.getMessageFor(LocaleService.GETTING_STARTED_HELP,
                localeService.getLocaleForUser((User) null));
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String message = "**Kom-i-gång guide (Svenska):**\n" +
                "<https://github.com/magnusmickelsson/pokeraidbot/blob/master/GETTING_STARTED_USER_sv.md>\n\n" +
                "**Getting started guide (English):**\n" +
                "<https://github.com/magnusmickelsson/pokeraidbot/blob/master/GETTING_STARTED_USER_en.md>";
        replyBasedOnConfigButKeep(config, commandEvent, message);
    }
}
