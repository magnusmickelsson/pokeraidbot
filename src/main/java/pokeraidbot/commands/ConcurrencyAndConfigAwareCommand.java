package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandListener;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.concurrent.ExecutorService;

public abstract class ConcurrencyAndConfigAwareCommand extends ConfigAwareCommand {
    protected final ExecutorService executorService;

    public ConcurrencyAndConfigAwareCommand(ServerConfigRepository serverConfigRepository,
                                            CommandListener commandListener, LocaleService localeService,
                                            ExecutorService executorService, UserConfigRepository userConfigRepository) {
        super(serverConfigRepository, commandListener, localeService, userConfigRepository);
        Validate.notNull(executorService);
        this.executorService = executorService;
    }
}
