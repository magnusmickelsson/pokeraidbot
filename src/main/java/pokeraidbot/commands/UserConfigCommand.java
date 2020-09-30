package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
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
    protected void executeWithConfig(CommandEvent commandEvent, Config serverConfig) {
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
                userConfig = userConfigRepository.save(new UserConfig(user.getId(), null,
                        null, null, serverConfig.getLocale()));
            }
            replyBasedOnConfig(serverConfig, commandEvent, String.valueOf(userConfig));
        }
        else {
            final String[] paramAndValue = arguments[0].split("=");
            if (paramAndValue.length > 2 || paramAndValue.length < 2) {
                throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.USER_CONFIG_BAD_SYNTAX,
                        localeService.getLocaleForUser(user))
                );
            }
            String param = paramAndValue[0];
            String value = paramAndValue[1];
            switch (param.toLowerCase()) {
                case "locale":
                    final Locale newLocale = setLocaleForUser(user, userConfig, value);
                    replyBasedOnConfigAndRemoveAfter(serverConfig, commandEvent,
                            localeService.getMessageFor(LocaleService.LOCALE_SET,
                                    localeService.getLocaleForUser(user), newLocale.getLanguage()),
                            BotServerMain.timeToRemoveFeedbackInSeconds);
                    break;
                case "nick":
                    userConfig = setNickForUser(serverConfig, user, userConfig, value);
                    replyBasedOnConfig(serverConfig, commandEvent, String.valueOf(userConfig));
                    break;
                default:
                    throw new UserMessedUpException(user,
                            localeService.getMessageFor(LocaleService.USER_CONFIG_BAD_PARAM,
                                    localeService.getLocaleForUser(user), param)
                    );

            }
        }
    }

    private UserConfig setNickForUser(Config config, User user, UserConfig userConfig, String value) {
        if (value.length() > 10 || value.length() < 3) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.USER_NICK_INVALID,
                    localeService.getLocaleForUser(user))
            );
        }
        if (userConfig == null) {
            final UserConfig entity = new UserConfig(user.getId(), null, null, null,
                    config.getLocale());
            entity.setNick(value);
            userConfig = userConfigRepository.save(entity);
        } else {
            userConfig.setNick(value);
            userConfig = userConfigRepository.save(userConfig);
        }
        return userConfig;
    }

    private Locale setLocaleForUser(User user, UserConfig userConfig, String value) {
        final Locale newLocale = new Locale(value);
        if (!LocaleService.isSupportedLocale(newLocale)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.UNSUPPORTED_LOCALE,
                            localeService.getLocaleForUser(user),
                            value));
        }
        userConfig.setLocale(newLocale);
        userConfigRepository.save(userConfig);
        return newLocale;
    }
}
