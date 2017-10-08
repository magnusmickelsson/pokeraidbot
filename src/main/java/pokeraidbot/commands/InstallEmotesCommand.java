package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.EmoteImpl;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.Route;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstallEmotesCommand extends Command {
    public InstallEmotesCommand() {
        this.name = "install-emotes";
        this.help = "Installation av emotes, bara till för administratörer.";
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
                // todo: i18n
//                event.reply("You already have an icon with the name \"" + emoteToInstall + "\".");
                event.reply("Du har redan installerat emote för: \"" + emoteToInstall + "\".");
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
            body.put("roles", Stream.of(roles).filter(Objects::nonNull).map(ISnowflake::getId).collect(Collectors.toSet()));

        // todo: check bot permissions, must be able to handle emojis
        GuildImpl guild = (GuildImpl) commandEvent.getGuild();
        JDA jda = commandEvent.getJDA();
        Route.CompiledRoute route = Route.Emotes.CREATE_EMOTE.compile(guild.getId());
        AuditableRestAction<Emote> action = new AuditableRestAction<Emote>(jda, route, body)
        {
            @Override
            protected void handleResponse(Response response, Request<Emote> request)
            {
                if (response.isOk()) {
                    JSONObject obj = response.getObject();
                    final long id = obj.getLong("id");
                    String name = obj.getString("name");
                    EmoteImpl emote = new EmoteImpl(id, guild).setName(name);
                    // managed is false by default, should always be false for emotes created by client accounts.

                    JSONArray rolesArr = obj.getJSONArray("roles");
                    Set<Role> roleSet = emote.getRoleSet();
                    for (int i = 0; i < rolesArr.length(); i++)
                    {
                        roleSet.add(guild.getRoleById(rolesArr.getString(i)));
                    }

                    // put emote into cache
                    guild.getEmoteMap().put(id, emote);

                    request.onSuccess(emote);
                }
                else {
                    request.onFailure(response);
                    throw new RuntimeException("Couldn't install emojis. Make sure that pokeraidbot has access to manage emojis.");
                }
            }
        };
        action.queue();
    }
}
