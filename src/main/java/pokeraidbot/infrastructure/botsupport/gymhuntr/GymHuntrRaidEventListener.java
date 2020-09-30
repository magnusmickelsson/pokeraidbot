package pokeraidbot.infrastructure.botsupport.gymhuntr;

import main.BotServerMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.commands.NewRaidGroupCommand;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRaidInfo;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static pokeraidbot.Utils.*;

public class GymHuntrRaidEventListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymHuntrRaidEventListener.class);

    private ServerConfigRepository serverConfigRepository;
    private RaidRepository raidRepository;
    private GymRepository gymRepository;
    private PokemonRepository pokemonRepository;
    private LocaleService localeService;
    private ExecutorService executorService;
    private final ClockService clockService;
    private final BotService botService;
    private final PokemonRaidStrategyService strategyService;

    public GymHuntrRaidEventListener(ServerConfigRepository serverConfigRepository, RaidRepository raidRepository,
                                     GymRepository gymRepository, PokemonRepository pokemonRepository,
                                     LocaleService localeService, ExecutorService executorService,
                                     ClockService clockService, BotService botService,
                                     PokemonRaidStrategyService strategyService) {
        this.serverConfigRepository = serverConfigRepository;
        this.raidRepository = raidRepository;
        this.gymRepository = gymRepository;
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.executorService = executorService;
        this.clockService = clockService;
        this.botService = botService;
        this.strategyService = strategyService;
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent guildEvent = (GuildMessageReceivedEvent) event;
            final User messageAuthor = guildEvent.getAuthor();
            try {
                if (isUserGymhuntrBot(messageAuthor) || isUserPokeAlarmBot(messageAuthor)) {
                    final String serverName = guildEvent.getGuild().getName().toLowerCase();
                    final Config config = serverConfigRepository.getConfigForServer(serverName);
                    if (config == null) {
                        LOGGER.warn("Server configuration is null for this guild: " + guildEvent.getGuild().getName());
                        return;
                    }

                    if (!config.useBotIntegration()) {
                        if (LOGGER.isTraceEnabled()) {
                            LOGGER.trace("Skipping trigger, since bot integration setting is false for server " +
                                    guildEvent.getGuild().getName());
                        }
                        return;
                    }
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
                            try {
                                if (newRaidArguments != null && newRaidArguments.size() > 0) {
                                    final Iterator<String> iterator = newRaidArguments.iterator();
                                    final String gym = iterator.next();
                                    final String pokemon = iterator.next();
                                    final String time = iterator.next();
                                    final Pokemon raidBoss = pokemonRepository.getByName(pokemon);
                                    final String region = config.getRegion();
                                    final Gym raidGym = gymRepository.findByName(gym, region);
                                    final LocalDate currentDate = currentDateTime.toLocalDate();
                                    final LocalDateTime endOfRaid = LocalDateTime.of(currentDate,
                                            LocalTime.parse(time, Utils.timeParseFormatter));
                                    final SelfUser botUser = botService.getBot().getSelfUser();
                                    final PokemonRaidInfo raidInfo;
                                    raidInfo = strategyService.getRaidInfo(raidBoss);
                                    handleRaidFromIntegration(botUser,
                                            guildEvent, raidBoss, raidGym, endOfRaid, config, clockService,
                                            raidInfo, strategyService);
                                } else {
                                    if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("No arguments to create raid with for server " + config +
                                                ", skipping. Raw command: " + guildEvent.getMessage().getContentRaw());
                                    }
                                }
                            } catch (Throwable t) {
                                LOGGER.warn("Exception when trying to get arguments for raid creation: " + t.getMessage());
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Exception thrown for event listener: " + t.getMessage());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Stacktrace: ", t);
                }
            }
        }
    }

    public void handleRaidFromIntegration(User user, GuildMessageReceivedEvent guildEvent, Pokemon raidBoss, Gym raidGym,
                                          LocalDateTime endOfRaid, Config config, ClockService clockService,
                                          PokemonRaidInfo pokemonRaidInfo,
                                          PokemonRaidStrategyService pokemonRaidStrategyService) {
        Validate.notNull(user, "User");
        Validate.notNull(guildEvent, "Guild event");
        Validate.notNull(config, "Config");
        Validate.notNull(raidBoss, "Raid boss");
        Validate.notNull(raidGym, "Gym");
        Validate.notNull(user, "User");

        final LocalDateTime now = clockService.getCurrentDateTime();
        LocalDateTime currentDateTime = now;
        final boolean moreThan10MinutesLeftOnRaid = endOfRaid.isAfter(currentDateTime.plusMinutes(10));
        if (moreThan10MinutesLeftOnRaid) {
            final Raid raidToCreate = new Raid(raidBoss,
                    endOfRaid,
                    raidGym,
                    localeService, config.getRegion(), false);
            final MessageChannel channel = guildEvent.getChannel();
            try {
                if (raidRepository.isActiveOrExRaidAt(raidGym, config.getRegion())) {
                    Raid existingRaid =
                            raidRepository.getActiveRaidOrFallbackToExRaid(raidGym, config.getRegion(), user);
                    if (existingRaid.getPokemon().isEgg()) {
                        existingRaid = raidRepository.changePokemon(existingRaid, raidBoss,
                                guildEvent.getGuild(), config, user,
                                "(bot) " +
                                        "!raid hatch " + raidBoss.getName() + " " + existingRaid.getGym().getName());
                        LOGGER.info("Hatched raid: " + existingRaid);
                    } else {
                        LOGGER.info("Raid already present, which is not an egg to hatch. " +
                                "Skipping raid at: " + raidGym.getName() + " for server " + config.getServer());
                    }
                } else {
                    createRaid(user, guildEvent, config, clockService, pokemonRaidInfo, now, raidToCreate, channel);
                }
            } catch (Throwable t) {
                LOGGER.warn("Exception when trying to create raid via botintegration for server " +
                        config.getServer() + ", channel " + (channel != null ? channel.getName() : "NULL") + ": " +
                        t.getMessage());
            }
        } else {
            LOGGER.debug("Skipped creating raid at " + raidGym +
                    ", less than 10 minutes remaining on it.");
        }
    }

    protected void createRaid(User user, GuildMessageReceivedEvent guildEvent, Config config,
                              ClockService clockService, PokemonRaidInfo pokemonRaidInfo,
                              LocalDateTime now, Raid raidToCreate, MessageChannel channel) {
        Raid createdRaid;
        if (raidToCreate.isExRaid()) {
            LOGGER.debug("Got an EX raid to create from gym integration, skipping: " + raidToCreate);
            return;
        }
        createdRaid = raidRepository.newRaid(user, raidToCreate, guildEvent.getGuild(), config,
                "(bot) !raid new " + raidToCreate.getPokemon().getName() + " " +
                        printTimeIfSameDay(raidToCreate.getEndOfRaid()) + " " + raidToCreate.getGym().getName());
        final Locale locale = config.getLocale();
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(null, null);
        StringBuilder sb = new StringBuilder();
        sb.append(localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
                locale, createdRaid.toString(locale)));
//        if (user != null && channel != null) {
//            createGroupIfConfigSaysSo(user, guildEvent, config, clockService,
//                    pokemonRaidInfo, now, createdRaid, channel);
//        } else {
//            LOGGER.warn("Could not create group, as some input values were null!");
//        }

        embedBuilder.setDescription(sb.toString());
        final MessageEmbed messageEmbed = embedBuilder.build();
        sendFeedbackThenCleanUp(createdRaid, channel, messageEmbed);
    }

    private void sendFeedbackThenCleanUp(Raid createdRaid, MessageChannel channel, MessageEmbed messageEmbed) {
        LOGGER.info("Raid created via Bot integration for region " + createdRaid.getRegion() + ": " + createdRaid);
        try {
            channel.sendMessage(messageEmbed).queue(m -> {
                // Clean up message
                try {
                    channel.deleteMessageById(m.getId())
                            .queueAfter(BotServerMain.timeToRemoveFeedbackInSeconds, TimeUnit.SECONDS);
                } catch (Throwable t) {
                    LOGGER.warn("Could not clean up feedback from raid creation: " + t.getMessage());
                }
            });
        } catch (Throwable t) {
            LOGGER.debug("Could not send feedback for raid creation: " + t.getMessage());
        }
    }

    private void createGroupIfConfigSaysSo(User user, GuildMessageReceivedEvent guildEvent, Config config,
                                           ClockService clockService, PokemonRaidInfo pokemonRaidInfo,
                                           LocalDateTime now, Raid createdRaid, MessageChannel channel) {
        // Auto create group for tier 5 bosses, if server config says to do so
        if (pokemonRaidInfo != null && pokemonRaidInfo.getBossTier() == 5) {
            LocalTime groupStart = getAutoCreatedRaidGroupStart(now, createdRaid);

            if (groupStart != null) {
                MessageChannel chn = config.getGroupCreationChannel(guildEvent.getGuild());
                MessageChannel channelToCreateGroupIn = channel;
                if (chn != null &&
                        config.getGroupCreationStrategy() == Config.RaidGroupCreationStrategy.NAMED_CHANNEL) {
                    channelToCreateGroupIn = chn;
                }
                if (LOGGER.isDebugEnabled()) {
                    if (channel != null) {
                        LOGGER.debug("Channel to use to create group: " + channel.getName());
                    }
                }
                try {
                    NewRaidGroupCommand.createRaidGroup(channelToCreateGroupIn, guildEvent.getGuild(), config, user,
                            config.getLocale(), groupStart, createdRaid.getId(), localeService, raidRepository,
                            botService, serverConfigRepository, pokemonRepository, gymRepository,
                            clockService, executorService, strategyService);
                } catch (Throwable t) {
                    LOGGER.warn("Could not create raid group for server " + config.getServer() + " and raid " +
                    createdRaid + ": " + t.getMessage());
                }
            }
        } else {
            if (pokemonRaidInfo == null) {
                LOGGER.debug("PokeRaidInfo was null for pokemon " + createdRaid.getPokemon().getName());
            }
        }
    }

    protected static LocalTime getAutoCreatedRaidGroupStart(LocalDateTime now, Raid createdRaid) {
        LocalTime groupStart = null;
        final LocalDateTime endOfRaid = createdRaid.getEndOfRaid();
        final LocalDateTime startOfRaid = getStartOfRaid(endOfRaid, createdRaid.isExRaid());
        final int defaultNumberOfMinutesAfterHatchForGroupCreation = getDefaultNumberOfMinutesAfterHatchForGroupCreation();
        if (now.isBefore(startOfRaid)) {
            groupStart = startOfRaid.toLocalTime().plusMinutes(defaultNumberOfMinutesAfterHatchForGroupCreation);
        } else if (now.isAfter(startOfRaid) && now.plusMinutes(defaultNumberOfMinutesAfterHatchForGroupCreation)
                .plusMinutes(5)
                .isBefore(endOfRaid)) {
            groupStart = now.toLocalTime().plusMinutes(defaultNumberOfMinutesAfterHatchForGroupCreation);
        } else if (now.isBefore(endOfRaid.minusMinutes(10))) {
            groupStart = endOfRaid.toLocalTime().minusMinutes(5);
        }

        return groupStart;
    }

    protected static int getDefaultNumberOfMinutesAfterHatchForGroupCreation() {
        return BotService.currentTier5Bosses.size() > 1 ? 30 : 10;
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
        if (title.contains("raid is available against")) {
            final String[] titleSplit = title.replaceAll("!", "").split(" ");
            pokemon = titleSplit[titleSplit.length - 1];
            final String[] descriptionSplitNewLines = description.split("\n");
            final String[] descriptionSplit = descriptionSplitNewLines[0].split(" ");
            timeString = printTime(LocalTime.parse(descriptionSplit[descriptionSplit.length - 3]));
            final String[] gymSplit = title.split("raid is available against");
            gym = gymSplit[0].trim();
        } else if (title.contains("has a level 5") && description.contains("will hatch")) {
            final String[] descriptionSplit = description.split(" ");
            timeString = printTime(LocalTime.parse(descriptionSplit[descriptionSplit.length - 3])
                    .plusMinutes(Utils.RAID_DURATION_IN_MINUTES));
            gym = title.split("has a level 5")[0].trim();
            pokemon = getTier5RaidBossBasedOnSeason(clockService);
        } else if (title.contains("has a level 6") && description.contains("will hatch")) {
            final String[] descriptionSplit = description.split(" ");
            timeString = printTime(LocalTime.parse(descriptionSplit[descriptionSplit.length - 3])
                    .plusMinutes(Utils.RAID_DURATION_IN_MINUTES));
            gym = title.split("has a level 6")[0].trim();
            pokemon = PokemonRepository.EGG_6;
        } else {
            return new ArrayList<>(); // We shouldn't create a raid for this case, non-tier 5 egg
        }
        return Arrays.asList(new String[]{gym, pokemon, timeString});
    }

    protected static String getTier5RaidBossBasedOnSeason(ClockService clockService) {
        String pokemon;
        final List<String> currentTier5Bosses = BotService.currentTier5Bosses;
        if (currentTier5Bosses == null || currentTier5Bosses.size() != 1){
            pokemon = "Egg5";
        } else {
            pokemon = currentTier5Bosses.iterator().next();
        }
        return pokemon;
    }

    public static List<String> gymhuntrArgumentsToCreateRaid(String title, String description,
                                                             ClockService clockService) {
        String gym, pokemon, timeString;
        if (title.contains("Raid has started!")) {
            final String[] firstPass = description.replaceAll("[*]", "")
                    .replaceAll("[.]", "")
                    .replaceAll("Raid Ending: ", "").split("\n");
            final String[] timeArguments = firstPass[3].replaceAll("hours ", "")
                    .replaceAll("min ", "").replaceAll("sec", "").split(" ");
            timeString = printTime(clockService.getCurrentTime()
                    .plusHours(Long.parseLong(timeArguments[0]))
                    .plusMinutes(Long.parseLong(timeArguments[1]))
                    .plusSeconds(Long.parseLong(timeArguments[2])));
            gym = firstPass[0].trim();
            pokemon = firstPass[1].trim();
        } else if (title.contains("Level 5 Raid is starting soon!")) {
            final String[] firstPass = description.replaceAll("[*]", "")
                    .replaceAll("[.]", "")
                    .replaceAll("Raid Starting: ", "").split("\n");
            gym = firstPass[0].trim();
            final String[] timeArguments = firstPass[1].replaceAll("hours ", "")
                    .replaceAll("min ", "").replaceAll("sec", "").split(" ");
            timeString = printTime(clockService.getCurrentTime()
                    .plusHours(Long.parseLong(timeArguments[0]))
                    .plusMinutes(Long.parseLong(timeArguments[1]))
                    .plusSeconds(Long.parseLong(timeArguments[2]))
                    .plusMinutes(Utils.RAID_DURATION_IN_MINUTES));
            pokemon = getTier5RaidBossBasedOnSeason(clockService);
        } else if (title.contains("Level 6 Raid is starting soon!")) {
            final String[] firstPass = description.replaceAll("[*]", "")
                    .replaceAll("[.]", "")
                    .replaceAll("Raid Starting: ", "").split("\n");
            gym = firstPass[0].trim();
            final String[] timeArguments = firstPass[1].replaceAll("hours ", "")
                    .replaceAll("min ", "").replaceAll("sec", "").split(" ");
            timeString = printTime(clockService.getCurrentTime()
                    .plusHours(Long.parseLong(timeArguments[0]))
                    .plusMinutes(Long.parseLong(timeArguments[1]))
                    .plusSeconds(Long.parseLong(timeArguments[2]))
                    .plusMinutes(Utils.RAID_DURATION_IN_MINUTES));
            pokemon = PokemonRepository.EGG_6;
        } else {
            return new ArrayList<>(); // = We shouldn't create this raid, since it is a non-tier 5 egg
        }
        final String[] argumentsInOrder = new String[]{gym, pokemon, timeString};
        return Arrays.asList(argumentsInOrder);
    }
}
