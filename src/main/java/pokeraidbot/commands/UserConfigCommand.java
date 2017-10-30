package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfig;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.Locale;

// todo: Not done yet
public class UserConfigCommand extends ConfigAwareCommand {
    private final UserConfigRepository userConfigRepository;

    public UserConfigCommand(ServerConfigRepository serverConfigRepository, CommandListener commandListener,
                             LocaleService localeService, UserConfigRepository userConfigRepository) {
        super(serverConfigRepository, commandListener, localeService);
        this.userConfigRepository = userConfigRepository;
        this.name = "config";
        // todo: i18n
        this.help = "Get or change user configuration - !raid config show to display, !raid config {param}={value} to change.";
    }

    // todo: refactor
    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        UserConfig userConfig = userConfigRepository.findOne(user.getId());
        // todo: allow user to set their locale and other config
        final String[] arguments = commandEvent.getArgs().split(" ");
        if (arguments.length > 1 || arguments.length < 1) {
            // todo: i18n
            // todo: add to man command as new topic
            throw new UserMessedUpException(user,
                    "Bad syntax. To see user's configuration: *!raid config show*\n" +
                            "To change: *!raid config {param}={value}*");
        }

        if ("show".equalsIgnoreCase(arguments[0])) {
            if (userConfig == null) {
                userConfig = userConfigRepository.save(new UserConfig(user.getId(), null, null, null, config.getLocale()));
            }
            replyBasedOnConfig(config, commandEvent, String.valueOf(userConfig));
        } else {
            final String[] paramAndValue = arguments[0].split("=");
            if (paramAndValue.length > 2 || paramAndValue.length < 2) {
                //  todo: i18n (use same exception as above)
                throw new UserMessedUpException(user,
                        "Bad syntax. To see user's configuration: *!raid config show*\n" +
                                "To change: *!raid config {param}={value}*");
            }
            String param = paramAndValue[0];
            String value = paramAndValue[1];
            if (!"locale".equalsIgnoreCase(param)) {
                //  todo: i18n
                throw new UserMessedUpException(user,
                        "The only parameter that can be changed right now is locale. You tried to set " + param + ".");
            }

            final Locale newLocale = new Locale(value);
            if (!LocaleService.isSupportedLocale(newLocale)) {
                //  todo: i18n
                throw new UserMessedUpException(user,
                        "You tried to set an unsupported locale: " + value + ". Supported locales are: " +
                                StringUtils.join(LocaleService.SUPPORTED_LOCALES, ", "));
            }
            userConfig.setLocale(newLocale);
            userConfigRepository.save(userConfig);
            // todo: i18n
            replyBasedOnConfigAndRemoveAfter(config, commandEvent, "Locale set to: " + newLocale, 15);
        }
    }
}
