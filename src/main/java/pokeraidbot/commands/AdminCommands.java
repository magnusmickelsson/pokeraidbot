package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import main.BotServerMain;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.tracking.TrackingService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfig;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Admin commands, only meant for bot owner/creator
 * Remove a user's config (to reset it): !raid admin userconfig {userid}
 * Check bot permissions: !raid admin permissions
 * Clear all !raid track entities - !raid admin clear tracking
 * Send message to default channel in all guilds this bot is in: !raid admin announce {message}
 * Check if a user is a member of a certain guild: !raid admin ismember {userid} {guild name}
 * List guilds the bot is in: !raid admin guilds
 * List what guilds a certain user is member of: !raid admin member {userid}
 * Create a test raid: !raid admin test {pokemon} {start time} {gym}
 * Set current tier 5 raid boss list: !raid admin tier5 {list of bosses ;-separated}
 */
public class AdminCommands extends Command {
    private final UserConfigRepository userConfigRepository;
    private final ServerConfigRepository serverConfigRepository;
    private final GymRepository gymRepository;
    private final BotService botService;
    private final TrackingService trackingCommandListener;
    private final LocaleService localeService;
    private final PokemonRepository pokemonRepository;
    private final RaidRepository raidRepository;

    public AdminCommands(UserConfigRepository userConfigRepository, ServerConfigRepository serverConfigRepository,
                         GymRepository gymRepository, BotService botService,
                         TrackingService trackingCommandListener, LocaleService localeService,
                         PokemonRepository pokemonRepository, RaidRepository raidRepository) {
        this.userConfigRepository = userConfigRepository;
        this.serverConfigRepository = serverConfigRepository;
        this.gymRepository = gymRepository;
        this.botService = botService;
        this.trackingCommandListener = trackingCommandListener;
        this.localeService = localeService;
        this.pokemonRepository = pokemonRepository;
        this.raidRepository = raidRepository;
        this.guildOnly = false;
        this.name = "admin";
        this.help = "Admin commands, only for Bot creator.";
    }

    @Override
    protected void execute(CommandEvent event) {
        final User user = event.getAuthor();
        if (user == null || user.getId() == null || (!user.getId().equals(BotServerMain.BOT_CREATOR_USERID))) {
            event.replyInDm("This command is reserved only for bot creator. Hands off! ;p Your user ID was: " +
                    String.valueOf(user.getId()));
            return;
        } else {
            final String eventArgs = event.getArgs();
            if (eventArgs.startsWith("userconfig")) {
                String userId = eventArgs.replaceAll("userconfig\\s{1,3}", "");
                final UserConfig userConfig = userConfigRepository.findOne(userId);
                if (userConfig == null) {
                    event.replyInDm("No user with ID " + userId);
                    return;
                } else {
                    userConfigRepository.delete(userConfig);
                    event.replyInDm("Removed user configuration for user with ID " + userId);
                    return;
                }
            } else if (eventArgs.startsWith("permissions")) {
                final JDA bot = botService.getBot();
                final List<Guild> guilds = bot.getGuilds();
                StringBuilder sb = new StringBuilder();
                sb.append("**Permissions for bot across servers:**\n\n");
                for (Guild guild : guilds) {
                    final Member member = guild.getMember(bot.getSelfUser());
                    if (member == null) {
                        event.replyInDm("Could not get bot as servermember!");
                        return;
                    }
                    sb.append("*").append(guild.getName()).append("*\n");
                    for (Permission p : member.getPermissions()) {
                        sb.append(p.getName()).append("(Guild: ").append(p.isGuild())
                                .append(" Channel: ").append(p.isChannel()).append(")\n");
                    }
                    sb.append("\n\n");
                }
                event.replyInDm(sb.toString());
                return;
            } else if (eventArgs.startsWith("clear tracking")) {
                trackingCommandListener.clearCache();
                event.replyInDm("Cleared tracking cache.");
                return;
            } else if (eventArgs.startsWith("announce")) {
                final JDA bot = botService.getBot();
                final List<Guild> guilds = bot.getGuilds();
                StringBuilder sb = new StringBuilder();
                for (Guild guild : guilds) {
                    try {
                        guild.getDefaultChannel().sendMessage(eventArgs
                                .replaceAll("announce\\s{1,3}", "")).queue();
                        sb.append("Sent message for guild ").append(guild.getName()).append("\n");
                    } catch (Throwable t) {
                        sb.append("Failed to send message for guild ").append(guild.getName())
                                .append(": ").append(t.getMessage()).append("\n");
                    }
                }
                event.replyInDm(sb.toString());
                return;
            } else if (eventArgs.startsWith("ismember")) {
                String userIdAndGuildName = eventArgs.replaceAll("ismember\\s{1,3}", "");
                String[] args = userIdAndGuildName.split(" ");
                if (args.length < 2) {
                    event.reply("Bad syntax, should be something like: !raid admin ismember {userid} {guildname}");
                    return;
                } else {
                    final JDA bot = botService.getBot();
                    Guild guild = null;
                    final List<Guild> guilds = bot.getGuilds();
                    String guildName = StringUtils.join(ArrayUtils.remove(args, 0), " ");
                    for (Guild guildToCheck : guilds) {
                        if (guildToCheck.getName().equalsIgnoreCase(guildName)) {
                            guild = guildToCheck;
                        }
                    }
                    if (guild != null) {
                        final Member memberById = guild.getMemberById(args[0]);
                        if (memberById != null) {
                            event.reply("User is a member of server " + guild.getName());
                        } else {
                            event.reply("User is not a member of server " + guild.getName());
                        }
                    } else {
                        event.reply("There was no server the user is a member of.");
                    }
                    return;
                }
            } else if (eventArgs.startsWith("member")) {
                String userIdAndGuildName = eventArgs.replaceAll("member\\s{1,3}", "");
                String[] args = userIdAndGuildName.split(" ");
                if (args.length < 1 || args.length > 2) {
                    event.reply("Bad syntax, should be something like: !raid admin member {userid}");
                    return;
                } else {
                    StringBuilder sb = new StringBuilder();
                    final JDA bot = botService.getBot();
                    final List<Guild> guilds = bot.getGuilds();
                    sb.append("User with ID ").append(args[0]).append(" is a member of the following servers:\n\n");
                    if (guilds.size() == 0) {
                        sb.append("-");
                    }
                    for (Guild guild : guilds) {
                        final Member memberById = guild.getMemberById(args[0]);
                        if (memberById != null) {
                            sb.append(guild.getName()).append(" (Username ").append(memberById.getUser().getName())
                            .append(")\n");
                        }
                    }
                    event.reply(sb.toString());
                    return;
                }
            } else if (eventArgs.startsWith("guilds")) {
                final JDA bot = botService.getBot();
                final List<Guild> guilds = bot.getGuilds();
                StringBuilder sb = new StringBuilder();
                for (Guild guildToCheck : guilds) {
                    sb.append(guildToCheck.getName().toLowerCase()).append("\n");
                }
                event.reply(sb.toString());
                return;
            }  else if (eventArgs.startsWith("test")) {
                final Config configForServer =
                        serverConfigRepository.getConfigForServer(event.getGuild().getName().toLowerCase());
                String[] args = eventArgs.replaceAll("test\\s{1,3}", "").trim().split(" ");
                String pokemon = args[0];
                LocalDateTime startsAt = LocalDateTime.of(LocalDate.now(),
                        Utils.parseTime(user, args[1], localeService));
                String gymName = StringUtils.join(ArrayUtils.removeElements(args, 0, 1), " ").trim();
                final String region = configForServer.getRegion();
                Raid raid = new Raid(pokemonRepository.search(pokemon, user),
                        startsAt.plusMinutes(Utils.RAID_DURATION_IN_MINUTES),
                        gymRepository.search(user, gymName, region), localeService, region, false);
                final Raid createdRaid = raidRepository.newRaid(botService.getBot().getSelfUser(), raid,
                        event.getGuild(), configForServer,
                        event.getMessage().getContentRaw());
                event.reply("Bot created your test raid: " + createdRaid);
                return;
            } else if (eventArgs.startsWith("tier5")) {
                String[] bosses = eventArgs.replaceAll("tier5\\s{1,3}", "").trim().split(";");
                if (bosses == null || bosses.length < 1) {
                    event.reply("Bad syntax, should be: !raid admin tier5 Boss1;Boss2;Boss3");
                    return;
                } else {
                    final CopyOnWriteArrayList<String> currentTier5Bosses = new CopyOnWriteArrayList<>();
                    currentTier5Bosses.addAll(Arrays.asList(bosses));
                    BotService.currentTier5Bosses = currentTier5Bosses;
                    event.reply("Set current tier5 boss list: " + StringUtils.join(bosses, ", "));
                    return;
                }
            }
        }
        event.reply("No such command. Existing ones are:\n- userconfig {userid}\n- permissions\n" +
                "- clear tracking\n- announce {message}\n- ismember {userid} {guild name}\n- guilds\n" +
                " - member {userid}\n - test {pokemon} {start time} {gym}\n- tier5 {list of bosses ;-separated}");
    }
}
