package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandListener;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.concurrent.ExecutorService;

/**
 * Command base class adding the concurrency executor service for spawned threads
 */
public abstract class ConcurrencyAndConfigAwareCommand extends ConfigAwareCommand {
    protected final ExecutorService executorService;

    public ConcurrencyAndConfigAwareCommand(ServerConfigRepository serverConfigRepository,
                                            CommandListener commandListener, LocaleService localeService,
                                            ExecutorService executorService) {
        super(serverConfigRepository, commandListener, localeService);
        Validate.notNull(executorService);
        this.executorService = executorService;
    }
}
