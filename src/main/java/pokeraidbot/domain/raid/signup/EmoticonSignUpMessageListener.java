package pokeraidbot.domain.raid.signup;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EmoticonSignUpMessageListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmoticonSignUpMessageListener.class);
    private final BotService botService;
    private final LocaleService localeService;
    private final ConfigRepository configRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final GymRepository gymRepository;
    private String emoteMessageId;
    private final String raidId;
    private String infoMessageId;
    private LocalDateTime startAt;
    private String userHadError = null;

    public EmoticonSignUpMessageListener(BotService botService, LocaleService localeService, ConfigRepository configRepository,
                                         RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                         GymRepository gymRepository,
                                         String raidId, LocalDateTime startAt) {
        this.botService = botService;
        this.localeService = localeService;
        this.configRepository = configRepository;
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.gymRepository = gymRepository;
        this.raidId = raidId;
        this.startAt = startAt;
    }

    public void setEmoteMessageId(String emoteMessageId) {
        this.emoteMessageId = emoteMessageId;
    }

    public void setInfoMessageId(String infoMessageId) {
        this.infoMessageId = infoMessageId;
    }

    @Override
    public void onEvent(Event event) {
        String reactionMessageId = null;
        User user = null;
        try {
            if (event instanceof GuildMessageReactionAddEvent) {
                final GuildMessageReactionAddEvent reactionEvent = (GuildMessageReactionAddEvent) event;
                reactionMessageId = reactionEvent.getReaction().getMessageId();
                if (!emoteMessageId.equals(reactionMessageId)) {
                    return;
                }
                user = reactionEvent.getUser();
                // If this is a reaction for a user that just triggered an error with his/her reaction, skip it
                if (user.getName().equals(userHadError)) {
                    userHadError = null;
                    return;
                }
                // If the bot added any reactions, don't respond to them
                if (user.equals(botService.getBot().getSelfUser())) return;

                final MessageReaction.ReactionEmote emote = reactionEvent.getReaction().getEmote();
                if (emote != null) {
                    switch (emote.getName()) {
                        case "mystic":
                            if (assertUserDoesntHaveIncorrectRole(reactionEvent, emote.getName())) {
                                addToSignUp(user, 1, 0, 0, 0);
                            }
                            break;
                        case "instinct":
                            if (assertUserDoesntHaveIncorrectRole(reactionEvent, emote.getName())) {
                                addToSignUp(user, 0, 1, 0, 0);
                            }
                            break;
                        case "valor":
                            if (assertUserDoesntHaveIncorrectRole(reactionEvent, emote.getName())) {
                                addToSignUp(user, 0, 0, 1, 0);
                            }
                            break;
                        case Emotes.ONE:
                            addToSignUp(user, 0, 0, 0, 1);
                            break;
                        case Emotes.TWO:
                            addToSignUp(user, 0, 0, 0, 2);
                            break;
                        case Emotes.THREE:
                            addToSignUp(user, 0, 0, 0, 3);
                            break;
                        case Emotes.FOUR:
                            addToSignUp(user, 0, 0, 0, 4);
                            break;
                        case Emotes.FIVE:
                            addToSignUp(user, 0, 0, 0, 5);
                            break;
//                        case Emotes.SIX:
//                            addToSignUp(user, 0, 0, 0, 6);
//                            break;
                        default:
                    }
                }
            } else if (event instanceof GuildMessageReactionRemoveEvent) {
                final GuildMessageReactionRemoveEvent reactionEvent = (GuildMessageReactionRemoveEvent) event;
                // If the bot added any reactions, don't respond to them
                user = reactionEvent.getUser();
                // If this is a reaction for a user that just triggered an error with his/her reaction, skip it
                if (user.getName().equals(userHadError)) {
                    userHadError = null;
                    return;
                }
                reactionMessageId = reactionEvent.getReaction().getMessageId();
                if (!emoteMessageId.equals(reactionMessageId)) {
                    return;
                }
                if (user.equals(botService.getBot().getSelfUser())) return;

                final MessageReaction.ReactionEmote emote = reactionEvent.getReaction().getEmote();
                if (emote != null) {
                    switch (emote.getName()) {
                        case "mystic":
                            removeFromSignUp(user, 1, 0, 0, 0);
                            break;
                        case "instinct":
                            removeFromSignUp(user, 0, 1, 0, 0);
                            break;
                        case "valor":
                            removeFromSignUp(user, 0, 0, 1, 0);
                            break;
                        case Emotes.ONE:
                            removeFromSignUp(user, 0, 0, 0, 1);
                            break;
                        case Emotes.TWO:
                            removeFromSignUp(user, 0, 0, 0, 2);
                            break;
                        case Emotes.THREE:
                            removeFromSignUp(user, 0, 0, 0, 3);
                            break;
                        case Emotes.FOUR:
                            removeFromSignUp(user, 0, 0, 0, 4);
                            break;
                        case Emotes.FIVE:
                            removeFromSignUp(user, 0, 0, 0, 5);
                            break;
//                        case Emotes.SIX:
//                            removeFromSignUp(user, 0, 0, 0, 6);
//                            break;
                        default:
                    }
                }
            }
        } catch (Throwable t) {
            if (event instanceof GenericGuildMessageReactionEvent) {
                final GenericGuildMessageReactionEvent guildMessageReactionEvent =
                        (GenericGuildMessageReactionEvent) event;
                // Since we got an error, remove last reaction
                if (reactionMessageId != null && reactionMessageId.equals(emoteMessageId)) {
                    // Do this with a slight delay to prevent graphical glitches client side.
                    guildMessageReactionEvent.getReaction().removeReaction(user)
                            .queueAfter(30, TimeUnit.MILLISECONDS);
                    userHadError = user.getName();
                }
                if (user != null) {
                    MessageBuilder messageBuilder = new MessageBuilder();
                    // todo: turn into message that only the target user can see
                    messageBuilder.append(user.getAsMention())
                            .append(": ").append(t.getMessage());
                    guildMessageReactionEvent.getChannel().sendMessage(messageBuilder.build()).queue();
                } else {
                    LOGGER.warn("We have a situation where user is null! Event: " + event);
                }
            }
        }
    }

    private Raid addToSignUp(User user, int mystic, int instinct, int valor, int plebs) {
        Raid changedRaid = raidRepository.addToOrCreateSignup(raidId, user.getName(),
                mystic, instinct, valor, plebs, startAt);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Added signup for user " + user.getName() +
                    " to raid: " + changedRaid + " - mystic: " + mystic + ", instinct: " + instinct + ", valor: " +
            valor + ", plebs: " + plebs);
        }
        return changedRaid;
    }

    private Raid removeFromSignUp(User user, int mystic, int instinct, int valor, int plebs) {
        Raid changedRaid = raidRepository.removeFromSignUp(raidId, user,
                mystic, instinct, valor, plebs, startAt);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removed from signup for user " + user.getName() +
                    ", raid: " + changedRaid + " - mystic: " + mystic + ", instinct: " + instinct + ", valor: " +
                    valor + ", plebs: " + plebs);
        }
        return changedRaid;
    }

    private boolean assertUserDoesntHaveIncorrectRole(GuildMessageReactionAddEvent event, String teamName) {
        final Guild guild = event.getGuild();
        final User user = event.getUser();
        final Set<String> teams = new HashSet<>(Arrays.asList("instinct", "valor", "mystic"));
        final Collection<String> teamsYouShouldntBeIn = CollectionUtils.subtract(teams, new HashSet<>(Arrays.asList(teamName)));
        final List<Role> roles = guild.getMember(user).getRoles();
        for (Role role : roles) {
            for (String teamYouShouldntBeIn : teamsYouShouldntBeIn) {
                if (StringUtils.containsIgnoreCase(teamYouShouldntBeIn, role.getName())) {
                    // todo: i18n
                    event.getChannel().sendMessage(user.getAsMention() +
                            ": Du har roll som lag " + teamYouShouldntBeIn +
                            " men försöker signa upp som " + teamName +
                            ". Jag struntar i just det klicket ;p").queue();
//                    event.getChannel().sendMessage(user.getAsMention() +
//                            ": You're in team " + teamYouShouldntBeIn + " trying to signup as " + teamName +
//                            ". Removing signup.").queue();
                    event.getReaction().removeReaction(user).queueAfter(30, TimeUnit.MILLISECONDS);
                    userHadError = user.getName();
                    return false;
                }
            }
        }
        return true;
    }

    public String getEmoteMessageId() {
        return emoteMessageId;
    }

    public String getInfoMessageId() {
        return infoMessageId;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }
}
