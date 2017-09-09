package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.LocaleService;

import java.util.Locale;

public class HelpCommand extends Command {
    private final LocaleService localeService;

    public HelpCommand(LocaleService localeService) {
        this.localeService = localeService;
        this.name = "usage";
        this.help = localeService.getMessageFor(LocaleService.USAGE_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final String args = commandEvent.getArgs();
        Locale locale;
        if (args != null && args.length() > 0) {
            locale = new Locale(args);
        } else {
            locale = localeService.getLocaleForUser(commandEvent.getAuthor().getName());
        }
        commandEvent.reply(
                localeService.getMessageFor(LocaleService.USAGE, locale));
    }
}
