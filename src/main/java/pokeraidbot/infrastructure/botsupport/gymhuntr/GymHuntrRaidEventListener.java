package pokeraidbot.infrastructure.botsupport.gymhuntr;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static pokeraidbot.Utils.printTime;

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
            final User messageAuthor = guildEvent.getAuthor();
            if (isUserGymhuntrBot(messageAuthor) || isUserPokeAlarmBot(messageAuthor)) {
                final String serverName = guildEvent.getGuild().getName().toLowerCase();
                final List<MessageEmbed> embeds = guildEvent.getMessage().getEmbeds();
                if (embeds != null && embeds.size() > 0) {
                    for (MessageEmbed embed : embeds) {
                        final LocalDateTime currentDateTime = clockService.getCurrentDateTime();
                        final String description = embed.getDescription();
                        final String title = embed.getTitle();
                        List<String> newRaidArguments;
                        if (isUserGymhuntrBot(messageAuthor)) {
                            newRaidArguments = gymhuntrArgumentsToCreateRaid(title, description, clockService);
                        } else if (isUserPokeAlarmBot(messageAuthor)) {
                            newRaidArguments = pokeAlarmArgumentsToCreateRaid(title, description, clockService);
                        } else {
                            newRaidArguments = new ArrayList<>();
                        }
                        if (newRaidArguments != null && newRaidArguments.size() > 0) {
                            // todo: arguments checking
                            final Iterator<String> iterator = newRaidArguments.iterator();
                            final String gym = iterator.next();
                            final String pokemon = iterator.next();
                            final String time = iterator.next();
                            final Pokemon raidBoss = pokemonRepository.getByName(pokemon);
                            final Config config = serverConfigRepository.getConfigForServer(serverName);
                            final Gym raidGym = gymRepository.findByName(gym, config.getRegion());
                            final LocalDate currentDate = currentDateTime.toLocalDate();
                            final LocalDateTime endOfRaid = LocalDateTime.of(currentDate,
                                    LocalTime.parse(time, Utils.timeParseFormatter));
                            handleRaidFromIntegration(guildEvent, raidBoss, raidGym, endOfRaid, config, clockService);
                        }
                    }
                }
            }
        }
    }

    public void handleRaidFromIntegration(GuildMessageReceivedEvent guildEvent, Pokemon raidBoss, Gym raidGym,
                                          LocalDateTime endOfRaid, Config config, ClockService clockService) {
        User messageAuthor = guildEvent.getAuthor();
        LocalDateTime currentDateTime = clockService.getCurrentDateTime();
        final boolean moreThan10MinutesLeftOnRaid = endOfRaid.isAfter(currentDateTime.plusMinutes(10));
        if (moreThan10MinutesLeftOnRaid) {
            final Raid raidToCreate = new Raid(raidBoss,
                    endOfRaid,
                    raidGym,
                    localeService, config.getRegion());
            final Raid createdRaid;
            try {
                createdRaid = raidRepository.newRaid(messageAuthor, raidToCreate);
                final Locale locale = config.getLocale();
                final MessageEmbed messageEmbed = new EmbedBuilder().setTitle(null, null)
                        .setDescription(localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
                                locale, createdRaid.toString(locale))).build();
                // todo: fetch from config what channel to post this message in
                guildEvent.getMessage().getChannel().sendMessage(messageEmbed).queue(m -> {
                    LOGGER.info("Raid created via Bot integration: " + createdRaid);
                });
            } catch (Throwable t) {
                LOGGER.warn("Exception when trying to create raid via botintegration: " +
                        t.getMessage());
            }
        } else {
            LOGGER.debug("Skipped creating raid at " + raidGym +
                    ", less than 10 minutes remaining on it.");
        }
    }

    public static boolean isUserPokeAlarmBot(User user) {
        return user.isBot() && (user.getName().equalsIgnoreCase("raid") ||
                user.getName().equalsIgnoreCase("egg"));
    }

    public static boolean isUserGymhuntrBot(User user) {
        return user.isBot() && StringUtils.containsIgnoreCase(
                user.getName(), "gymhuntrbot");
    }

    public static List<String> pokeAlarmArgumentsToCreateRaid(String title, String description,
                                                              ClockService clockService) {
        String gym, pokemon, timeString;
        if (title.contains("Raid is available")) {
            final String[] titleSplit = title.replaceAll("!", "").split(" ");
            pokemon = titleSplit[titleSplit.length - 1];
            final String[] descriptionSplit = description.split(" ");
            timeString = printTime(LocalTime.parse(descriptionSplit[descriptionSplit.length - 3]));
            final String[] descSplit = description.split("has a raid and is available until");
            gym = descSplit[0].trim();
            // todo: reactivate if gym name comes back to the egg announce message
//        }
//        else if (title.contains("Raid is incoming!") && description.contains("level 5 raid will hatch")){
//            pokemon = "Raikou";
//            final String[] descriptionSplit = description.split(" ");
//            timeString = printTime(LocalTime.parse(descriptionSplit[descriptionSplit.length - 3])
//                    .plusMinutes(Utils.RAID_DURATION_IN_MINUTES));
//            gym = description.split("has a level 5 raid")[0].trim();
        } else {
            return new ArrayList<>(); // We shouldn't create a raid for this case, non-tier 5 egg
        }
        return Arrays.asList(new String[]{gym, pokemon, timeString});
    }

    public static List<String> gymhuntrArgumentsToCreateRaid(String title, String description,
                                                             ClockService clockService) {
        String gym, pokemon, timeString;
        if (title.contains("Raid has started!")) {
            final String[] firstPass = description.replaceAll("[*]", "").replaceAll("[.]", "")
                    .replaceAll("Raid Ending: ", "").split("\n");
            final String[] timeArguments = firstPass[3].replaceAll("hours ", "")
                    .replaceAll("min ", "").replaceAll("sec", "").split(" ");
            timeString = printTime(clockService.getCurrentTime()
                    .plusHours(Long.parseLong(timeArguments[0]))
                    .plusMinutes(Long.parseLong(timeArguments[1]))
                    .plusSeconds(Long.parseLong(timeArguments[2])));
            gym = firstPass[0].trim();
            pokemon = firstPass[1].trim();
        } else if (title.contains("Level 5 Raid is starting soon!")){
            final String[] firstPass = description.replaceAll("[*]", "").replaceAll("[.]", "")
                    .replaceAll("Raid Starting: ", "").split("\n");
            pokemon = "Raikou"; // todo: fetch from some repo keeping track of what tier 5 boss is active for the region?
            gym = firstPass[0].trim();
            final String[] timeArguments = firstPass[1].replaceAll("hours ", "")
                    .replaceAll("min ", "").replaceAll("sec", "").split(" ");
            timeString = printTime(clockService.getCurrentTime()
                    .plusHours(Long.parseLong(timeArguments[0]))
                    .plusMinutes(Long.parseLong(timeArguments[1]))
                    .plusSeconds(Long.parseLong(timeArguments[2]))
                    .plusMinutes(Utils.RAID_DURATION_IN_MINUTES));
        } else {
            return new ArrayList<>(); // = We shouldn't create this raid, since it is a non-tier 5 egg
        }
        final String[] argumentsInOrder = new String[]{gym, pokemon, timeString};
        return Arrays.asList(argumentsInOrder);
    }
}
