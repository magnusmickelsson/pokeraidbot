package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * !raid install
 * todo: make this command easier and with better error handling
 */
public class InstallCommand extends Command {
    private final ServerConfigRepository serverConfigRepository;
    private final GymRepository gymRepository;

    public InstallCommand(ServerConfigRepository serverConfigRepository, GymRepository gymRepository) {
        this.serverConfigRepository = serverConfigRepository;
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
            event.replyInDm("Re-run the command !raid install, but with the following syntax:");
            event.replyInDm("!raid install server=[server name];region=[region dataset reference];" +
                    "replyInDm=[true or false];locale=[2 char language code];mods=[group for mods (optional)]" +
                    ";feedback=[feedback strategy (optional)];groupCreation=[group creation strategy (optional)];" +
                    "groupChannel=[if using named channel strategy, channel name (optional)];botIntegration=[true or false " +
                    "(optional)];pinGroups=[true or false (optional)]");
            event.replyInDm("Example: !raid install server=My test server;region=stockholm;" +
                    "replyInDm=false;locale=sv;mods=mods;feedback=REMOVE_ALL_EXCEPT_MAP;groupCreation=NAMED_CHANNEL;" +
                    "groupChannel=raidgroups;botIntegration=true;pinGroups=true");
            event.reactSuccess();
            return;
        } else {
            Map<String, String> settingsToSet = new HashMap<>();
            final String[] arguments = args.split(";");
            if (arguments.length < 4 || arguments.length > 10) {
                event.replyInDm("Wrong syntax of install command. Do this again, and this time " +
                        "follow instructions, please: !raid install");
                event.reactError();
                return;
            }
            for (String argument : arguments) {
                final String[] keyValue = argument.split("=");
                if (keyValue.length != 2 || StringUtils.isEmpty(keyValue[0]) || StringUtils.isEmpty(keyValue[1])) {
                    event.replyInDm("Wrong syntax of install command. Do this again, and this " +
                            "time follow instructions, please: !raid install");
                    event.reactError();
                    return;
                }
                settingsToSet.put(keyValue[0].trim().toLowerCase(), keyValue[1].trim().toLowerCase());
            }

            final String server = settingsToSet.get("server");
            try {
                Config config = serverConfigRepository.getConfigForServer(server);
                final Locale locale = new Locale(settingsToSet.get("locale"));
                final String region = settingsToSet.get("region");
                final String modGroup = settingsToSet.get("mods");
                final String replyindmValue = settingsToSet.get("replyindm");
                final Boolean replyInDmWhenPossible = replyindmValue == null ? false : Boolean.valueOf(replyindmValue);
                final String feedbackStrategyValue = settingsToSet.get("feedback");
                final String groupCreationStrategyValue = settingsToSet.get("groupcreation");
                final String groupChannel = settingsToSet.get("groupchannel");
                final String botIntegrationValue = settingsToSet.get("botintegration");
                final Boolean botIntegration =
                        botIntegrationValue == null ? false : Boolean.valueOf(botIntegrationValue);
                final String pinGroupsValue = settingsToSet.get("pinGroups");
                final Boolean pinGroups = pinGroupsValue == null ? false : Boolean.valueOf(pinGroupsValue);
                if (config == null) {
                    config = new Config(region,
                            replyInDmWhenPossible,
                            locale, server);
                } else {
                    config.setLocale(locale);
                    config.setRegion(region);
                    config.setReplyInDmWhenPossible(replyInDmWhenPossible);
                }
                config.setModPermissionGroup(modGroup);
                Config.RaidGroupCreationStrategy groupCreationStrategy =
                        getGroupCreationStrategyIfPossible(event, groupCreationStrategyValue);
                if (groupCreationStrategy != null) {
                    config.setGroupCreationStrategy(groupCreationStrategy);
                }
                Config.FeedbackStrategy feedbackStrategy = getFeedbackStrategyIfPossible(event, feedbackStrategyValue);
                if (feedbackStrategy != null) {
                    config.setFeedbackStrategy(feedbackStrategy);
                }
                config.setGroupCreationChannel(groupChannel);
                config.setUseBotIntegration(botIntegration);
                config.setPinGroups(pinGroups);
                event.replyInDm("Configuration complete. Saved configuration: " + serverConfigRepository.save(config));
                event.replyInDm("Now, run \"!raid install-emotes\" in your server's text chat to install the custom " +
                        "emotes the bot needs.");
                event.reactSuccess();
                gymRepository.reloadGymData();
            } catch (Throwable t) {
                event.replyInDm("There was an error: " + t.getMessage());
                event.replyInDm("Make sure you have followed the instructions correctly. " +
                        "If you have, contact magnus.mickelsson@gmail.com for support " +
                        "and ensure you include the error message you got above ...");
                event.reactError();
            }
        }
    }

    private Config.FeedbackStrategy getFeedbackStrategyIfPossible(CommandEvent event, String enumValue) {
        Config.FeedbackStrategy valueToSet = null;
        if (enumValue != null) {
            try {
                valueToSet = Config.FeedbackStrategy.valueOf(enumValue.toUpperCase());
            } catch (Throwable t) {
                event.replyInDm("Bad value of " + Config.FeedbackStrategy.class.getSimpleName() + ". Available values: " +
                        StringUtils.join(Config.FeedbackStrategy.values(), ", "));
                event.reactError();
                return null;
            }
        }
        return valueToSet;
    }

    private Config.RaidGroupCreationStrategy getGroupCreationStrategyIfPossible(CommandEvent event, String enumValue) {
        Config.RaidGroupCreationStrategy valueToSet = null;
        if (enumValue != null) {
            try {
                valueToSet = Config.RaidGroupCreationStrategy.valueOf(enumValue.toUpperCase());
            } catch (Throwable t) {
                event.replyInDm("Bad value of " + Config.RaidGroupCreationStrategy.class.getSimpleName() +
                        ". Available values: " +
                        StringUtils.join(Config.RaidGroupCreationStrategy.values(), ", "));
                event.reactError();
                return null;
            }
        }
        return valueToSet;
    }
}
