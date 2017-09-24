package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.Config;
import pokeraidbot.domain.LocaleService;

import java.util.Locale;

public interface TrackingTarget {
    boolean canHandle(CommandEvent commandEvent, Command command);
    void handle(CommandEvent commandEvent, Command command, LocaleService localeService, Locale locale, Config config);
}
