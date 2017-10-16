package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InstallCommand extends Command {
    private final ConfigRepository configRepository;
    private final GymRepository gymRepository;

    public InstallCommand(ConfigRepository configRepository, GymRepository gymRepository) {
        this.configRepository = configRepository;
        this.gymRepository = gymRepository;
        this.name = "install";
        this.help = "Installation command, only meant for server administrator.";
        this.guildOnly = false;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        if (StringUtils.isEmpty(args)) {
            // Answer with how-to
            event.replyInDM("Re-run the command !raid install, but with the following syntax:");
            event.replyInDM("!raid install server=[server name];region=[region dataset reference];" +
                    "replyInDm=[true or false];locale=[2 char language code]");
            event.replyInDM("Example: !raid install server=My test server;region=stockholm;replyInDm=false;locale=sv");
            event.reactSuccess();
            return;
        } else {
            Map<String, String> settingsToSet = new HashMap<>();
            final String[] arguments = args.split(";");
            if (arguments.length != 4) {
                event.replyInDM("Wrong syntax of install command. Do this again, and this time " +
                        "follow instructions, please: !raid install");
                event.reactError();
                return;
            }
            for (String argument : arguments) {
                final String[] keyValue = argument.split("=");
                if (keyValue.length != 2 || StringUtils.isEmpty(keyValue[0]) || StringUtils.isEmpty(keyValue[1])) {
                    event.replyInDM("Wrong syntax of install command. Do this again, and this " +
                            "time follow instructions, please: !raid install");
                    event.reactError();
                    return;
                }
                settingsToSet.put(keyValue[0].trim().toLowerCase(), keyValue[1].trim().toLowerCase());
            }

            final String server = settingsToSet.get("server");
            try {
                Config config = configRepository.getConfigForServer(server);
                final Locale locale = new Locale(settingsToSet.get("locale"));
                final String region = settingsToSet.get("region");
                final Boolean replyInDmWhenPossible = Boolean.valueOf(settingsToSet.get("replyindm"));
                if (config == null) {
                    config = new Config(region,
                            replyInDmWhenPossible,
                            locale, server);
                } else {
                    config.setLocale(locale);
                    config.setRegion(region);
                    config.setReplyInDmWhenPossible(replyInDmWhenPossible);
                }
                event.replyInDM("Configuration complete. Saved configuration: " + configRepository.save(config));
                event.replyInDM("Now, run \"!raid install-emotes\" in your server's text chat to install the custom " +
                        "emotes the bot needs.");
                event.reactSuccess();
                gymRepository.reloadGymData();
            } catch (Throwable t) {
                event.replyInDM("There was an error: " + t.getMessage());
                event.replyInDM("Make sure you have followed the instructions correctly. " +
                        "If you have, contact magnus.mickelsson@gmail.com for support " +
                        "and ensure you include the error message you got above ...");
                event.reactError();
            }
        }
    }
}
