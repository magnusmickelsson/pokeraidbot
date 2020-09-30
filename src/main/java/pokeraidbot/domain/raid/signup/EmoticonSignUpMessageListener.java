package pokeraidbot.domain.raid.signup;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class EmoticonSignUpMessageListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmoticonSignUpMessageListener.class);
    private final BotService botService;
    private final LocaleService localeService;
    private final ServerConfigRepository serverConfigRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final GymRepository gymRepository;
    private String emoteMessageId;
    private final String raidId;
    private String infoMessageId;
    private LocalDateTime startAt;
    private String userHadError = null;
    private String userId;

    public EmoticonSignUpMessageListener(BotService botService, LocaleService localeService,
                                         ServerConfigRepository serverConfigRepository,
                                         RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                         GymRepository gymRepository,
                                         String raidId, LocalDateTime startAt, User user) {
        this.botService = botService;
        this.localeService = localeService;
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.gymRepository = gymRepository;
        this.raidId = raidId;
        this.startAt = startAt;
        this.userId = user.getId();
        botService.addEmoticonEventListener(this);
    }

    public EmoticonSignUpMessageListener(BotService botService, LocaleService localeService,
                                         ServerConfigRepository serverConfigRepository,
                                         RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                         GymRepository gymRepository,
                                         String raidId, LocalDateTime startAt, String userId) {
        this.botService = botService;
        this.localeService = localeService;
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.gymRepository = gymRepository;
        this.raidId = raidId;
        this.startAt = startAt;
        this.userId = userId;
        botService.addEmoticonEventListener(this);
    }

    public void setEmoteMessageId(String emoteMessageId) {
        this.emoteMessageId = emoteMessageId;
    }

    public void setInfoMessageId(String infoMessageId) {
        this.infoMessageId = infoMessageId;
    }

    @Override
    public void onEvent(GenericEvent event) {
        String reactionMessageId = null;
        User user = null;
        if (emoteMessageId == null && infoMessageId == null) {
            LOGGER.trace("This listener haven't received a emote message id or info message id yet.");
            return;
        }
        try {
            if (event instanceof GuildMessageReactionAddEvent) {
                final GuildMessageReactionAddEvent reactionEvent = (GuildMessageReactionAddEvent) event;
                user = reactionEvent.getUser();
                // If the bot added any reactions, don't respond to them
                if (user.isBot()) return;
                reactionMessageId = reactionEvent.getReaction().getMessageId();
                if (emoteMessageId == null || !emoteMessageId.equals(reactionMessageId)) {
                    return;
                }
                // If this is a reaction for a user that just triggered an error with his/her reaction, skip it
                if (user.getName().equals(userHadError)) {
                    userHadError = null;
                    return;
                }

                final MessageReaction.ReactionEmote emote = reactionEvent.getReaction().getReactionEmote();
                if (emote != null) {
                    switch (emote.getName()) {
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
                        default:
                    }
                }
            } else if (event instanceof GuildMessageReactionRemoveEvent) {
                final GuildMessageReactionRemoveEvent reactionEvent = (GuildMessageReactionRemoveEvent) event;
                // If the bot added any reactions, don't respond to them
                user = reactionEvent.getUser();
                if (user.isBot()) return;
                // If this is a reaction for a user that just triggered an error with his/her reaction, skip it
                if (user.getName().equals(userHadError)) {
                    userHadError = null;
                    return;
                }
                reactionMessageId = reactionEvent.getReaction().getMessageId();
                if (!emoteMessageId.equals(reactionMessageId)) {
                    return;
                }

                final MessageReaction.ReactionEmote emote = reactionEvent.getReaction().getReactionEmote();
                if (emote != null) {
                    switch (emote.getName()) {
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
                        default:
                    }
                }
            }
        } catch (Throwable t) {
            if (event instanceof GenericGuildMessageReactionEvent) {
                final GenericGuildMessageReactionEvent guildMessageReactionEvent =
                        (GenericGuildMessageReactionEvent) event;
                reactionMessageId = guildMessageReactionEvent.getReaction().getMessageId();
                if (emoteMessageId == null) {
                    LOGGER.warn("Emote message ID = null, this should get cleaned up!" +
                            " Event: " + printInfoAbout(event));
                    return;
                }
                if (!emoteMessageId.equals(reactionMessageId)) {
                    LOGGER.warn("We got a guild reaction event throwing exception, but not one we were listening for!" +
                            " Event: " + printInfoAbout(event));
                    return;
                }
                // Since we got an error, remove last reaction
                if (reactionMessageId != null && reactionMessageId.equals(emoteMessageId)) {
                    // Do this with a slight delay to prevent graphical glitches client side.
                    guildMessageReactionEvent.getReaction().removeReaction(user)
                            .queueAfter(30, TimeUnit.MILLISECONDS);
                    userHadError = user.getName();
                }
                if (user != null && t.getMessage() != null) {
                    MessageBuilder messageBuilder = new MessageBuilder();
                    if (!t.getMessage().contains(user.getAsMention())) {
                        messageBuilder.append(user.getAsMention())
                                .append(": ");
                    }
                    messageBuilder.append(t.getMessage());
                    guildMessageReactionEvent.getChannel().sendMessage(messageBuilder.build()).queue();
                } else {
                    LOGGER.warn("We have a situation where user " + user + " or exception (of type " +
                            t.getClass().getSimpleName() + ") message is null! Event: " +
                            printInfoAbout(event));
                }
            } else {
                LOGGER.warn("Exception in event listener! Event: " +
                        printInfoAbout(event));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Stacktrace:", t);
                }
            }
        }
    }

    private String printInfoAbout(GenericEvent event) {
        try {
            StringBuilder sb = new StringBuilder();
            if (event == null) {
                return "null";
            }
            String className = event.getClass().getSimpleName();
            sb.append(className);
            String reflectionToString;
            try {
                reflectionToString = ReflectionToStringBuilder.toString(event);
            } catch (Throwable t) {
                reflectionToString = "N/A";
            }
            sb.append(":").append(reflectionToString);
            return sb.toString();
        } catch (Throwable t) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exception when printing info about event", t);
            }
            return "N/A";
        }
    }

    private Raid addToSignUp(User user, int mystic, int instinct, int valor, int plebs) {
        Raid changedRaid = raidRepository.modifySignUp(raidId, user,
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

    public String getRaidId() {
        return raidId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "EmoticonSignUpMessageListener{" +
                "emoteMessageId='" + emoteMessageId + '\'' +
                ", raidId='" + raidId + '\'' +
                ", infoMessageId='" + infoMessageId + '\'' +
                ", startAt=" + startAt +
                ", userId='" + userId + '\'' +
                '}';
    }
}
