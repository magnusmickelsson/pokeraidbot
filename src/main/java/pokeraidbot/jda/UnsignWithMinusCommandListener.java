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

public class UnsignWithMinusCommandListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnsignWithMinusCommandListener.class);

    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final ServerConfigRepository serverConfigRepository;
    private final BotService botService;
    private final LocaleService localeService;

    public UnsignWithMinusCommandListener(RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                          ServerConfigRepository serverConfigRepository, BotService botService,
                                          LocaleService localeService) {
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.serverConfigRepository = serverConfigRepository;
        this.botService = botService;
        this.localeService = localeService;
    }

    public static final String minusXRegExp = "^[-]\\d{1,2}\\s{1,2}.*";
    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof GuildMessageReceivedEvent) {
            final GuildMessageReceivedEvent guildMessageReceivedEvent = (GuildMessageReceivedEvent) event;
            if (guildMessageReceivedEvent.getAuthor().isBot()) {
                return;
            }

            final String rawContent = guildMessageReceivedEvent.getMessage().getContentRaw();
            if (rawContent.matches(minusXRegExp)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("It would seem this is a - command to remove signups: " + rawContent);
                }
                String strippedContent = rawContent.replaceAll("\\s{2,4}", " ")
                        .replaceFirst("[-]", "");
                String[] splitArguments = strippedContent.split("\\s{1}");
                attemptUnsignFromMinusCommand(guildMessageReceivedEvent, splitArguments);
            }
        }
    }

    private void attemptUnsignFromMinusCommand(GuildMessageReceivedEvent guildMessageReceivedEvent, String[] splitArguments) {
        final String numberOfPeopleArgument = splitArguments[0];
        final String[] gymArgument = ArrayUtils.removeAll(splitArguments, 0);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to remove " + numberOfPeopleArgument + " from raid, gym " +
                    StringUtils.join(gymArgument, " "));
        }
        final String guild = guildMessageReceivedEvent.getGuild().getName().trim().toLowerCase();
        final Config configForServer = serverConfigRepository.getConfigForServer(guild);
        final User user = guildMessageReceivedEvent.getAuthor();
        String message;
        try {
            message = raidRepository.executeUnsignCommand(configForServer, user,
                    localeService.getLocaleForUser(user),
                    splitArguments, "signup");
            guildMessageReceivedEvent.getMessage().addReaction(Emotes.OK).queue();
            ConfigAwareCommand.removeOriginMessageIfConfigSaysSo(configForServer, guildMessageReceivedEvent);
        } catch (Throwable t) {
            LOGGER.debug("Unsign command failed: " + t.getMessage());
            message = t.getMessage() + "\n\n" +
            "Syntax: *-1 Solna Platform*";
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
                            localeService.getLocaleForUser(user),
                            "15");
            embedBuilder.setFooter(msgRemoveText, null);
            guildMessageReceivedEvent.getMessage().getChannel().sendMessage(embedBuilder.build())
                    .queue(msg -> {
                        msg.delete().queueAfter(15, TimeUnit.SECONDS); // Clean up feedback after x seconds
                    }
            );
            LOGGER.debug("Removed signup.");
        }
    }
}
