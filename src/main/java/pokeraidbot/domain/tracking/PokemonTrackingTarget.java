package pokeraidbot.domain.tracking;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.Locale;

public class PokemonTrackingTarget implements TrackingTarget, Comparable<PokemonTrackingTarget> {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(PokemonTrackingTarget.class);
    private String userId;
    private Pokemon pokemon;

    public PokemonTrackingTarget(String userId, Pokemon pokemon) {
        Validate.notEmpty(userId, "User ID is empty!");
        Validate.notNull(pokemon, "Pokemon is null!");
        this.userId = userId;
        this.pokemon = pokemon;
    }

    public String getUserId() {
        return userId;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    private void sendPrivateMessage(User user, String content)
    {
        // openPrivateChannel provides a RestAction<PrivateChannel>
        // which means it supplies you with the resulting channel
        user.openPrivateChannel().queue((channel) ->
        {
            // value is a parameter for the `accept(T channel)` method of our callback.
            // here we implement the body of that method, which will be called later by JDA automatically.
            try {
                channel.sendMessage(content).queue(m -> {}, m ->{
                    LOGGER.warn("Could not send private message for tracking " + this + ": " + m.getMessage());
                });
                // here we access the enclosing scope variable -content-
                // which was provided to sendPrivateMessage(User, String) as a parameter
            } catch (Throwable t) {
                LOGGER.warn("Could not send private message for tracking " + this + ": " + t.getMessage());
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonTrackingTarget)) return false;

        PokemonTrackingTarget that = (PokemonTrackingTarget) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return pokemon != null ? pokemon.equals(that.pokemon) : that.pokemon == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (pokemon != null ? pokemon.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PokemonTrackingTarget{" +
                "userId='" + userId + '\'' +
                ", pokemon='" + pokemon.getName() + '\'' +
                '}';
    }

    @Override
    public int compareTo(PokemonTrackingTarget o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean canHandle(Config config, User user, Raid raid, Guild guild) {
        if (config == null || user == null || raid == null || guild == null) {
            LOGGER.debug("Returning tracker can't handle this, because some input is null. Config: " +
                    config + ", User: " + (user == null ? "null" : user.getName()) + ", Raid: " + raid +
                    ", Guild: " + guild);
            return false;
        }
        if (!config.useBotIntegration() && user.isBot()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Skipping, since server has no bot integration and message is from a bot.");
            }
            return false; // Skip bot messages
        }
        if (user.getId().equals(userId)) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Skipping, since this is user's own raid.");
            }
            return false; // Skip raids user created
        }
        if (guild.getMemberById(this.userId) == null) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Skipping, since user with this tracking is not a member of server " + guild.getName());
            }
            return false; // Skip raids for guilds where user is not a member
        }

        boolean raidIsForPokemon =
                StringUtils.containsIgnoreCase(raid.getPokemon().getName(),
                        pokemon.getName());
        return raidIsForPokemon;
    }

    @Override
    public void handle(Guild guild, LocaleService localeService, Config config, User user, Raid raid,
                       String inputMessage) {
        Validate.notNull(guild, "Guild is null");
        Validate.notNull(config, "Config is null");
        Validate.notNull(raid, "Raid is null");
        Validate.notNull(user, "User is null");
        Validate.notNull(localeService, "LocaleService is null");

        final Member memberById = guild.getMemberById(Long.parseLong(userId));
        if (memberById == null) {
            LOGGER.warn("Member with ID " + userId + " doesn't exist for server " + guild.getName() + "!");
            return;
        }
        final User userToMessage = memberById.getUser();
        if (userToMessage == null) {
            LOGGER.warn("User instance for member with ID " + userId +
                    " doesn't exist for server " + guild.getName() + "!");
            return;
        }
        final String commandInitiator = user.getName();
        final Locale locale = localeService.getLocaleForUser(user);

        final String message = localeService.getMessageFor(LocaleService.TRACKED_RAID, locale, guild.getName(),
                commandInitiator, raid.toString(locale));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending DM to user with ID " + userId + " for tracked pokemon " + pokemon.getName());
        }
        try {
            sendPrivateMessage(userToMessage, message);
        } catch (Throwable t) {
            LOGGER.warn("Could not send private message for tracking " + this + ": " + t.getMessage());
        }
    }
}
