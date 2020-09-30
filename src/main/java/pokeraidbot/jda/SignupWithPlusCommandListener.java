package pokeraidbot.jda;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.commands.ConfigAwareCommand;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.concurrent.TimeUnit;

public class SignupWithPlusCommandListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignupWithPlusCommandListener.class);

    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final ServerConfigRepository serverConfigRepository;
    private final BotService botService;
    private final LocaleService localeService;

    public SignupWithPlusCommandListener(RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                         ServerConfigRepository serverConfigRepository, BotService botService, LocaleService localeService) {
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.serverConfigRepository = serverConfigRepository;
        this.botService = botService;
        this.localeService = localeService;
    }

    public static final String plusXRegExp = "^[+]\\d{1,2}\\s{1,2}\\d{1,2}[:.]?\\d{2}\\s{1,2}.*";
    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof GuildMessageReceivedEvent) {
            final GuildMessageReceivedEvent guildMessageReceivedEvent = (GuildMessageReceivedEvent) event;
            if (guildMessageReceivedEvent.getAuthor().isBot()) {
                return;
            }

            final String rawContent = guildMessageReceivedEvent.getMessage().getContentRaw();
            if (rawContent.matches(plusXRegExp)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("It would seem this is a + command to add signups: " + rawContent);
                }
                String strippedContent = rawContent.replaceAll("\\s{2,4}", " ")
                        .replaceAll("[+]", "");
                String[] splitArguments = strippedContent.split("\\s{1}");
                attemptSignUpFromPlusCommand(guildMessageReceivedEvent, splitArguments);
            }
        }
    }

    private void attemptSignUpFromPlusCommand(GuildMessageReceivedEvent guildMessageReceivedEvent, String[] splitArguments) {
        final String numberOfPeopleArgument = splitArguments[0];
        final String etaArgument = splitArguments[1];
        final String[] gymArgument = ArrayUtils.removeAll(splitArguments, 0, 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to add " + numberOfPeopleArgument + " to raid, ETA " + etaArgument + " to gym " +
                    StringUtils.join(gymArgument, " "));
        }
        final String guild = guildMessageReceivedEvent.getGuild().getName().trim().toLowerCase();
        final Config configForServer = serverConfigRepository.getConfigForServer(guild);
        final User user = guildMessageReceivedEvent.getAuthor();
        String message;
        try {
            message = raidRepository.executeSignUpCommand(configForServer, user,
                    localeService.getLocaleForUser(user),
                    splitArguments, "signup");
            guildMessageReceivedEvent.getMessage().addReaction(Emotes.OK).queue();
            ConfigAwareCommand.removeOriginMessageIfConfigSaysSo(configForServer, guildMessageReceivedEvent);
        } catch (Throwable t) {
            LOGGER.debug("Signup plus command failed: " + t.getMessage());
            message = t.getMessage() + "\n\n" +
            "Syntax: *+1 09:45 Solna Platform*";
            guildMessageReceivedEvent.getMessage().addReaction(Emotes.ERROR).queue();
            ConfigAwareCommand.removeOriginMessageIfConfigSaysSo(configForServer, guildMessageReceivedEvent);
        }
        if (!StringUtils.isEmpty(message)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(null, null, null);
            embedBuilder.setTitle(null);
            embedBuilder.setDescription(message);
            final String msgRemoveText =
                    localeService.getMessageFor(LocaleService.KEEP_CHAT_CLEAN,
                            localeService.getLocaleForUser(user), "15");
            embedBuilder.setFooter(msgRemoveText, null);
            guildMessageReceivedEvent.getMessage().getChannel().sendMessage(embedBuilder.build())
                    .queue(msg -> {
                        msg.delete().queueAfter(15, TimeUnit.SECONDS); // Clean up feedback after x seconds
                    }
            );
            LOGGER.debug("Added signup.");
        }
    }
}
