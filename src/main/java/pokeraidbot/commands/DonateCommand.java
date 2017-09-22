package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.LocaleService;

public class DonateCommand extends Command {
    private static final String link = "https://pledgie.com/campaigns/34823";

    public DonateCommand(LocaleService localeService) {
        this.name = "donate";
        this.help = localeService.getMessageFor(LocaleService.DONATE, LocaleService.DEFAULT);
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.reply(link);
    }
}
