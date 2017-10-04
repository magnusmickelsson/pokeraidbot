package pokeraidbot.domain.raid.signup;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EmoticonMessageListener implements EventListener {
    private final BotService botService;
    private final LocaleService localeService;
    private final ConfigRepository configRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final GymRepository gymRepository;
    private final String messageId;

    public EmoticonMessageListener(BotService botService, LocaleService localeService, ConfigRepository configRepository,
                                   RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                   GymRepository gymRepository, String messageId) {
        this.botService = botService;

        this.localeService = localeService;
        this.configRepository = configRepository;
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.gymRepository = gymRepository;
        this.messageId = messageId;
    }

    @Override
    public void onEvent(Event event) {
        // todo: We need to keep track of which messages were raidstatus messages and only check their emotes as command initiators
        if (event instanceof GuildMessageReactionAddEvent) {
            final GuildMessageReactionAddEvent reactionEvent = (GuildMessageReactionAddEvent) event;
            // If the bot added any reactions, don't respond to them
            final User user = reactionEvent.getUser();
            final Guild guild = reactionEvent.getGuild();
            if (user.equals(botService.getBot().getSelfUser())) return;

            boolean wasPokeraidbotEmote = false;
            final MessageReaction.ReactionEmote emote = reactionEvent.getReaction().getEmote();
            if (emote != null) {
                switch (emote.getName()) {
                    case "mystic":
                        if (assertUserDoesntHaveIncorrectRole(reactionEvent, emote.getName())) {
                            ((GuildMessageReactionAddEvent) event).getChannel()
                                    .sendMessage(user.getAsMention() + " från team mystic anmälde sig!").queue();
                            wasPokeraidbotEmote = true;
                        }
                        break;
                    case "instinct":
                        if (assertUserDoesntHaveIncorrectRole(reactionEvent, emote.getName())) {
                            // todo: skicka pm med kommandoinstruktioner
                            reactionEvent.getChannel()
                                    .sendMessage(user.getAsMention() + " från team instinct anmälde sig!").queue();
                            wasPokeraidbotEmote = true;
                        }
                        break;
                    case "valor":
                        if (assertUserDoesntHaveIncorrectRole(reactionEvent, emote.getName())) {
                            reactionEvent.getChannel()
                                    .sendMessage(user.getAsMention() + " från team valor anmälde sig!").queue();
                            wasPokeraidbotEmote = true;
                        }
                        break;
                    case Emotes.ONE:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " anmälde en laglös person!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.TWO:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " anmälde två laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.THREE:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " anmälde tre laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.FOUR:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " anmälde fyra laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.FIVE:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " anmälde fem laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.SIX:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " anmälde sex laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    default:
                }

                // Remove the reaction which was added in this event.
                // Do this with a slight delay to prevent graphical glitches client side.
//                if (wasPokeraidbotEmote) {
//                    reactionEvent.getReaction().removeReaction(user).queueAfter(30, TimeUnit.MILLISECONDS);
//                }
            }
        } else if (event instanceof GuildMessageReactionRemoveEvent) {
            final GuildMessageReactionRemoveEvent reactionEvent = (GuildMessageReactionRemoveEvent) event;
            // If the bot added any reactions, don't respond to them
            final User user = reactionEvent.getUser();
            final Guild guild = reactionEvent.getGuild();
            if (user.equals(botService.getBot().getSelfUser())) return;

            boolean wasPokeraidbotEmote = false;
            final MessageReaction.ReactionEmote emote = reactionEvent.getReaction().getEmote();
            if (emote != null) {
                switch (emote.getName()) {
                    case "mystic":
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " från team mystic avanmälde sig!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case "instinct":
                        // todo: skicka pm med kommandoinstruktioner
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " från team instinct avanmälde sig!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case "valor":
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " från team valor avanmälde sig!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.ONE:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " avanmälde en laglös person!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.TWO:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " avanmälde två laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.THREE:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " avanmälde tre laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.FOUR:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " avanmälde fyra laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.FIVE:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " avanmälde fem laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.SIX:
                        reactionEvent.getChannel()
                                .sendMessage(user.getAsMention() + " avanmälde sex laglösa personer!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    default:
                }

                // Remove the reaction which was added in this event.
                // Do this with a slight delay to prevent graphical glitches client side.
//                if (wasPokeraidbotEmote) {
//                    reactionEvent.getReaction().removeReaction(user).queueAfter(30, TimeUnit.MILLISECONDS);
//                }
            }
        }

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
                            ": You're in team " + teamYouShouldntBeIn + " trying to signup as " + teamName +
                            ". Removing signup.").queue();
                    event.getReaction().removeReaction(user).queueAfter(30, TimeUnit.MILLISECONDS);
                    return false;
                }
            }
        }
        return true;
    }
}
