package pokeraidbot.jda;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class SignupWithPlusCommandListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignupWithPlusCommandListener.class);

    private RaidRepository raidRepository;
    private PokemonRepository pokemonRepository;
    private ConfigRepository configRepository;

    public SignupWithPlusCommandListener(RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                         ConfigRepository configRepository) {
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.configRepository = configRepository;
    }

    public static final String plusXRegExp = "^[+]\\d{1,2}\\s.*";
    @Override
    public void onEvent(Event event) {
        if (event instanceof GuildMessageReceivedEvent) {
            final String rawContent = ((GuildMessageReceivedEvent) event).getMessage().getRawContent();
            if (rawContent.matches(plusXRegExp)) {
                LOGGER.warn("It would seem this is a + command to add signups: " + rawContent);
                String strippedContent = rawContent.replaceAll("\\s{2,4}", " ")
                        .replaceAll("[+]", "");
                String[] splitArguments = strippedContent.split("\\s{1}");
                final String numberOfPeopleArgument = splitArguments[0];
                final String etaArgument = splitArguments[1];
                final String[] gymArgument = ArrayUtils.removeAll(splitArguments, 0, 1);
                LOGGER.warn("Adding " + numberOfPeopleArgument + " to raid, ETA " + etaArgument + " to gym " + StringUtils.join(gymArgument, ","));
            }
        }
    }
}
