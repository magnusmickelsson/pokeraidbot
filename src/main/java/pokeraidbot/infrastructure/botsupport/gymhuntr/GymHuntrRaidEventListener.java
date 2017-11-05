package pokeraidbot.infrastructure.botsupport.gymhuntr;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class GymHuntrRaidEventListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymHuntrRaidEventListener.class);

    private ServerConfigRepository serverConfigRepository;
    private RaidRepository raidRepository;
    private GymRepository gymRepository;
    private PokemonRepository pokemonRepository;
    private LocaleService localeService;
    private ExecutorService executorService;
    private final ClockService clockService;

    public GymHuntrRaidEventListener(ServerConfigRepository serverConfigRepository, RaidRepository raidRepository,
                                     GymRepository gymRepository, PokemonRepository pokemonRepository,
                                     LocaleService localeService, ExecutorService executorService,
                                     ClockService clockService) {
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.gymRepository = gymRepository;
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.executorService = executorService;
        this.clockService = clockService;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent guildEvent = (GuildMessageReceivedEvent) event;
            if (guildEvent.getAuthor().isBot() &&
                    StringUtils.containsIgnoreCase(guildEvent.getAuthor().getName(), "gymhuntrbot")) {
                final Config config = serverConfigRepository.getConfigForServer(guildEvent.getGuild().getName());
                final List<MessageEmbed> embeds = guildEvent.getMessage().getEmbeds();
                if (embeds != null && embeds.size() > 0) {
                    for (MessageEmbed embed : embeds) {
                        LocalDateTime now = clockService.getCurrentDateTime();
                        final String description = embed.getDescription();
                        final String title = embed.getTitle();
                        final List<MessageEmbed.Field> fields = embed.getFields();
                        final MessageEmbed.ImageInfo image = embed.getImage();
                        final String logMessage = "Content in bot message:\n" + title + " " + description;
                        LOGGER.warn(logMessage);
                        guildEvent.getMessage().getChannel().sendMessage(logMessage).queue();
                        final Raid raidToCreate = new Raid(pokemonRepository.getByName("Tyranitar"),
                                now.plusMinutes(30), gymRepository.findByName("Hästen", config.getRegion()),
                                localeService, config.getRegion());
                        raidRepository.newRaid(guildEvent.getAuthor(), raidToCreate);
                    }
                }
            }
        }
    }
}
