package pokeraidbot.jda;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class SignupWithPlusCommandListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignupWithPlusCommandListener.class);

    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final ConfigRepository configRepository;
    private final BotService botService;
    private final LocaleService localeService;

    public SignupWithPlusCommandListener(RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                         ConfigRepository configRepository, BotService botService, LocaleService localeService) {
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.configRepository = configRepository;
        this.botService = botService;
        this.localeService = localeService;
    }

    public static final String plusXRegExp = "^[+]\\d{1,2}\\s.*";
    @Override
    public void onEvent(Event event) {
        if (event instanceof GuildMessageReceivedEvent) {
            final GuildMessageReceivedEvent guildMessageReceivedEvent = (GuildMessageReceivedEvent) event;
            if (guildMessageReceivedEvent.getAuthor().isBot()) {
                return;
            }

            final String rawContent = guildMessageReceivedEvent.getMessage().getRawContent();
            if (rawContent.matches(plusXRegExp)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("It would seem this is a + command to add signups: " + rawContent);
                }
                String strippedContent = rawContent.replaceAll("\\s{2,4}", " ")
                        .replaceAll("[+]", "");
                String[] splitArguments = strippedContent.split("\\s{1}");
                final String numberOfPeopleArgument = splitArguments[0];
                final String etaArgument = splitArguments[1];
                final String[] gymArgument = ArrayUtils.removeAll(splitArguments, 0, 1);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Adding " + numberOfPeopleArgument + " to raid, ETA " + etaArgument + " to gym " +
                            StringUtils.join(gymArgument, ","));
                }
                final String guild = guildMessageReceivedEvent.getGuild().getName().trim().toLowerCase();
                final Config configForServer = configRepository.getConfigForServer(guild);
                final User user = guildMessageReceivedEvent.getAuthor();
                String message;
                try {
                    message = raidRepository.executeSignUpCommand(configForServer, user,
                            localeService.getLocaleForUser(user),
                            splitArguments, "signup");
                } catch (Throwable t) {
                    message = t.getMessage();
                }
                guildMessageReceivedEvent.getMessage().getChannel().sendMessage(message).queue();
                LOGGER.debug("Added signup.");
            }
        }
    }
}