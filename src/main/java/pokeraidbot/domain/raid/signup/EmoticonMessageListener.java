package pokeraidbot.domain.raid.signup;

import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.ConfigRepository;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;

import java.util.concurrent.TimeUnit;

public class EmoticonMessageListener implements EventListener {
    private final BotService botService;
    private final LocaleService localeService;
    private final ConfigRepository configRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final GymRepository gymRepository;

    public EmoticonMessageListener(BotService botService, LocaleService localeService, ConfigRepository configRepository,
                                   RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                   GymRepository gymRepository) {
        this.botService = botService;

        this.localeService = localeService;
        this.configRepository = configRepository;
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.gymRepository = gymRepository;
    }

    @Override
    public void onEvent(Event event) {
        // todo: We need to keep track of which messages were raidstatus messages and only check their emotes as command initiators
        if (event instanceof GuildMessageReactionAddEvent) {
            final GuildMessageReactionAddEvent reactionEvent = (GuildMessageReactionAddEvent) event;
            // If the bot added any reactions, don't respond to them
            if (reactionEvent.getUser().equals(botService.getBot().getSelfUser())) return;

            boolean wasPokeraidbotEmote = false;
            final MessageReaction.ReactionEmote emote = reactionEvent.getReaction().getEmote();
            if (emote != null) {
                switch (emote.getName()) {
                    case Emotes.SIGN_UP_NOW_EMOTE:
                        ((GuildMessageReactionAddEvent) event).getChannel()
                                .sendMessage(reactionEvent.getUser().getAsMention() + " anmälde sig!").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    case Emotes.UNSIGN_EMOTE:
                        ((GuildMessageReactionAddEvent) event).getChannel()
                                .sendMessage(reactionEvent.getUser().getAsMention() + " tog bort sin anmälan. :(").queue();
                        wasPokeraidbotEmote = true;
                        break;
                    default:
                }

                // Remove the reaction which was added in this event.
                // Do this with a slight delay to prevent graphical glitches client side.
                if (wasPokeraidbotEmote) {
                    reactionEvent.getReaction().removeReaction(reactionEvent.getUser()).queueAfter(30, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
