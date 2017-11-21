package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.commands.NewRaidCommand;
import pokeraidbot.commands.NewRaidExCommand;
import pokeraidbot.commands.NewRaidStartsAtCommand;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.Locale;

public class PokemonTrackingTarget implements TrackingTarget, Comparable<PokemonTrackingTarget> {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(PokemonTrackingTarget.class);
    private String region;
    private String userId;
    private Pokemon pokemon;

    public PokemonTrackingTarget(String region, String userId, Pokemon pokemon) {
        this.region = region;
        this.userId = userId;
        this.pokemon = pokemon;
    }

    public String getRegion() {
        return region;
    }

    public String getUserId() {
        return userId;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    @Override
    public boolean canHandle(CommandEvent commandEvent, Command command) {
        if (commandEvent.getAuthor().isBot()) {
            return false; // Skip bot messages
        }
        if (commandEvent.getAuthor().getId().equals(userId)) {
            return false; // Skip raids user created
        }
        if (command instanceof NewRaidCommand || command instanceof NewRaidExCommand ||
                command instanceof NewRaidStartsAtCommand) {
            boolean rawContentContainsPokemonName =
                    StringUtils.containsIgnoreCase(commandEvent.getEvent().getMessage().getRawContent(),
                            pokemon.getName());
            return rawContentContainsPokemonName;
        }
        return false;
    }

    @Override
    public void handle(CommandEvent commandEvent, Command command, LocaleService localeService, Locale locale, Config config) {
        final Member memberById = commandEvent.getGuild().getMemberById(Long.parseLong(userId));
        final User userToMessage = memberById.getUser();
        final String raidCreator = commandEvent.getEvent().getAuthor().getName();
        final String rawContent = commandEvent.getEvent().getMessage().getRawContent();

        final String message = localeService.getMessageFor(LocaleService.TRACKED_RAID, locale, pokemon.getName(),
                raidCreator, rawContent);
        sendPrivateMessage(userToMessage, message);
    }

    @Override
    public boolean canHandle(GuildMessageReceivedEvent event, Raid raid) {
        if (event == null || event.getAuthor() == null || raid == null || event.getAuthor().getId().equals(userId)) {
            return false; // Skip events user created
        }
        return raid.getPokemon().equals(pokemon);
    }

    @Override
    public void handle(GuildMessageReceivedEvent event, Raid raid, LocaleService localeService, Locale locale,
                       Config config) {
        Validate.notNull(event, "Guild event is null");
        Validate.notNull(config, "Config is null");
        Validate.notNull(raid, "Raid is null");
        Validate.notNull(locale, "Locale is null");

        final Member memberById = event.getGuild().getMemberById(Long.parseLong(userId));
        if (memberById == null) {
            LOGGER.warn("Member with user ID " + userId + " could not be found!");
            return;
        }
        final User userToMessage = memberById.getUser();
        final String raidCreator = event.getAuthor().getName();

        final String message = localeService.getMessageFor(LocaleService.TRACKED_RAID, locale, pokemon.getName(),
                raidCreator, raid.toString(locale));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending DM to user with ID " + userId + " for tracked pokemon " + pokemon.getName());
        }
        sendPrivateMessage(userToMessage, message);
    }

    private void sendPrivateMessage(User user, String content)
    {
        // openPrivateChannel provides a RestAction<PrivateChannel>
        // which means it supplies you with the resulting channel
        user.openPrivateChannel().queue((channel) ->
        {
            // value is a parameter for the `accept(T channel)` method of our callback.
            // here we implement the body of that method, which will be called later by JDA automatically.
            channel.sendMessage(content).queue();
            // here we access the enclosing scope variable -content-
            // which was provided to sendPrivateMessage(User, String) as a parameter
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonTrackingTarget)) return false;

        PokemonTrackingTarget that = (PokemonTrackingTarget) o;

        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return pokemon != null ? pokemon.equals(that.pokemon) : that.pokemon == null;
    }

    @Override
    public int hashCode() {
        int result = region != null ? region.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (pokemon != null ? pokemon.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PokemonTrackingTarget{" +
                "region='" + region + '\'' +
                ", userId='" + userId + '\'' +
                ", pokemon='" + pokemon + '\'' +
                '}';
    }

    @Override
    public int compareTo(PokemonTrackingTarget o) {
        return toString().compareTo(o.toString());
    }
}
