package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class GettingStartedCommand extends Command {
    public GettingStartedCommand(LocaleService localeService) {
        this.name = "getting-started";
        this.guildOnly = false;
        this.help = localeService.getMessageFor(LocaleService.GETTING_STARTED_HELP,
                localeService.getLocaleForUser((User) null));
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final String message = "**Kom-i-gång guide (Svenska):**\n" +
                "https://github.com/magnusmickelsson/pokeraidbot/blob/master/GETTING_STARTED_USER_sv.md\n\n" +
                "**Getting started guide (English):**\n" +
                "https://github.com/magnusmickelsson/pokeraidbot/blob/master/GETTING_STARTED_USER_en.md";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(null);
        embedBuilder.setAuthor(null, null, null);
        embedBuilder.setDescription(message);
        commandEvent.reply(embedBuilder.build());
    }
}
