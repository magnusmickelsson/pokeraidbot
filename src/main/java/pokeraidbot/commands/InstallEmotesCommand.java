package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import org.json.JSONObject;
import pokeraidbot.domain.config.LocaleService;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * !raid install-emotes
 */
public class InstallEmotesCommand extends Command {
    private final LocaleService localeService;

    public InstallEmotesCommand(LocaleService localeService) {
        this.localeService = localeService;
        this.name = "install-emotes";
        this.help = "Installation of emotes (admin only).";
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(CommandEvent event) {
        final List<Emote> currentEmotes = event.getGuild().getEmotes();
        final Set<String> emoteNamesToInstall = new HashSet<>(Arrays.asList("mystic", "valor", "instinct"));
        boolean emotesAlreadyInstalled = false;
        for (Emote emote : currentEmotes) {
            final String emoteToInstall = emote.getName().toLowerCase();
            if (emoteNamesToInstall.contains(emoteToInstall)) {
                event.reply(localeService.getMessageFor(LocaleService.EMOTE_INSTALLED_ALREADY,
                        localeService.getLocaleForUser(event.getAuthor()), emoteToInstall));
                emotesAlreadyInstalled = true;
            }
        }

        if (emotesAlreadyInstalled) {
            return;
        }

        final InputStream mysticPngResource =
                InstallEmotesCommand.class.getResourceAsStream("/static/img/mystic.png");
        final InputStream valorPngResource =
                InstallEmotesCommand.class.getResourceAsStream("/static/img/valor.png");
        final InputStream instinctPngResource =
                InstallEmotesCommand.class.getResourceAsStream("/static/img/instinct.png");
        try {
            event.reply("Installing icons for Pokemon go teams...");
            createEmote("mystic", event, Icon.from(mysticPngResource));
            createEmote("valor", event, Icon.from(valorPngResource));
            createEmote("instinct", event, Icon.from(instinctPngResource));
            event.reply("Emotes installed. Try them out: :mystic: :valor: :instinct:");
        } catch (Throwable t) {
            event.reply(t.getMessage());
        }
    }

    // Code taken from JDA's GuildController since they have a limitation that bot accounts can't upload emotes.
    private void createEmote(String iconName, CommandEvent commandEvent, Icon icon, Role... roles) {
        JSONObject body = new JSONObject();
        body.put("name", iconName);
        body.put("image", icon.getEncoding());
        if (roles.length > 0) // making sure none of the provided roles are null before mapping them to the snowflake id
        {
            body.put("roles",
                    Stream.of(roles).filter(Objects::nonNull).map(ISnowflake::getId).collect(Collectors.toSet()));
        }

        StringWriter writer = new StringWriter();
        body.write(writer);
        DataObject dataObject = DataObject.fromJson(writer.toString());

        GuildImpl guild = (GuildImpl) commandEvent.getGuild();
        JDA jda = commandEvent.getJDA();
        Route.CompiledRoute route = Route.Emotes.CREATE_EMOTE.compile(guild.getId());
        AuditableRestAction<Emote> action = new AuditableRestActionImpl<Emote>(jda, route, dataObject)
        {
            @Override
            public void handleResponse(Response response, Request<Emote> request)
            {
                if (response.isOk()) {
                    DataObject obj = response.getObject();
                    final long id = obj.getLong("id");
                    String name = obj.getString("name");
                    EmoteImpl emote = new EmoteImpl(id, guild).setName(name);
                    // managed is false by default, should always be false for emotes created by client accounts.

                    DataArray rolesArr = obj.getArray("roles");
                    Set<Role> roleSet = emote.getRoleSet();
                    for (int i = 0; i < rolesArr.length(); i++)
                    {
                        roleSet.add(guild.getRoleById(rolesArr.getString(i)));
                    }

                    // put emote into cache
                    guild.createEmote(name, icon, roles);

                    request.onSuccess(emote);
                }
                else {
                    request.onFailure(response);
                    throw new RuntimeException("Couldn't install emojis. " +
                            "Make sure that pokeraidbot has access to manage emojis.");
                }
            }
        };
        action.queue();
    }
}
