package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfig;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.Locale;

public class UserConfigCommand extends ConfigAwareCommand {
    private final UserConfigRepository userConfigRepository;

    public UserConfigCommand(ServerConfigRepository serverConfigRepository, CommandListener commandListener,
                             LocaleService localeService, UserConfigRepository userConfigRepository) {
        super(serverConfigRepository, commandListener, localeService);
        this.userConfigRepository = userConfigRepository;
        this.name = "config";
        this.help = localeService.getMessageFor(LocaleService.HELP_USER_CONFIG, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        UserConfig userConfig = userConfigRepository.findOne(user.getId());
        final String[] arguments = commandEvent.getArgs().split(" ");
        if (arguments.length > 1 || arguments.length < 1) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.USER_CONFIG_BAD_SYNTAX,
                    localeService.getLocaleForUser(user))
                    );
        }

        if ("show".equalsIgnoreCase(arguments[0])) {
            if (userConfig == null) {
                userConfig = userConfigRepository.save(new UserConfig(user.getId(), null, null, null, config.getLocale()));
            }
            replyBasedOnConfig(config, commandEvent, String.valueOf(userConfig));
        } else {
            final String[] paramAndValue = arguments[0].split("=");
            if (paramAndValue.length > 2 || paramAndValue.length < 2) {
                throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.USER_CONFIG_BAD_SYNTAX,
                        localeService.getLocaleForUser(user))
                );
            }
            String param = paramAndValue[0];
            String value = paramAndValue[1];
            if (!"locale".equalsIgnoreCase(param)) {
                throw new UserMessedUpException(user,
                        localeService.getMessageFor(LocaleService.USER_CONFIG_BAD_PARAM,
                                localeService.getLocaleForUser(user), param)
                );
            }

            final Locale newLocale = new Locale(value);
            if (!LocaleService.isSupportedLocale(newLocale)) {
                throw new UserMessedUpException(user,
                        localeService.getMessageFor(LocaleService.UNSUPPORTED_LOCALE,
                                localeService.getLocaleForUser(user),
                        value));
            }
            userConfig.setLocale(newLocale);
            userConfigRepository.save(userConfig);
            replyBasedOnConfigAndRemoveAfter(config, commandEvent,
                    localeService.getMessageFor(LocaleService.LOCALE_SET,
                            localeService.getLocaleForUser(user), newLocale.getLanguage()),
                    BotServerMain.timeToRemoveFeedbackInSeconds);
        }
    }
}
