package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.EmoticonSignUpMessageListener;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ConcurrentModificationException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static pokeraidbot.Utils.*;

/**
 * !raid group [start raid at (HH:MM)] [Pokestop name]
 */
public class NewRaidGroupCommand extends ConcurrencyAndConfigAwareCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewRaidGroupCommand.class);

    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final BotService botService;
    private final ClockService clockService;
    private final PokemonRaidStrategyService pokemonRaidStrategyService;

    public NewRaidGroupCommand(GymRepository gymRepository, RaidRepository raidRepository,
                               PokemonRepository pokemonRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository,
                               CommandListener commandListener, BotService botService,
                               ClockService clockService, ExecutorService executorService,
                               PokemonRaidStrategyService pokemonRaidStrategyService) {
        super(serverConfigRepository, commandListener, localeService, executorService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.botService = botService;
        this.clockService = clockService;
        this.pokemonRaidStrategyService = pokemonRaidStrategyService;
        this.name = "group";
        this.aliases = new String[]{"g"};
        this.help = localeService.getMessageFor(LocaleService.RAID_GROUP_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String[] args = commandEvent.getArgs().split(" ");
        final Locale locale = localeService.getLocaleForUser(user);
        if (args.length < 2) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.BAD_SYNTAX,
                    localeService.getLocaleForUser(user), "!raid group 10:00 solna platform"));
        }

        String timeString = args[0];
        LocalTime startAtTime = Utils.parseTime(user, timeString, localeService);

        assertTimeNotInNoRaidTimespan(user, startAtTime, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        createRaidGroup(commandEvent.getChannel(), commandEvent.getGuild(),
                config, user, locale, startAtTime, raid.getId(),
                localeService, raidRepository, botService, serverConfigRepository, pokemonRepository, gymRepository,
                clockService, executorService, pokemonRaidStrategyService);
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }

    public static void createRaidGroup(MessageChannel channel, Guild guild, Config config, User user,
                                       Locale locale, LocalTime startAtTime, String raidId, LocaleService localeService,
                                       RaidRepository raidRepository, BotService botService,
                                       ServerConfigRepository serverConfigRepository,
                                       PokemonRepository pokemonRepository, GymRepository gymRepository,
                                       ClockService clockService, ExecutorService executorService,
                                       PokemonRaidStrategyService pokemonRaidStrategyService) {
        assertAllParametersNotNullOrEmpty(channel, config, user, locale, startAtTime, raidId, localeService,
                raidRepository, botService, serverConfigRepository, pokemonRepository, gymRepository,
                clockService, executorService);

        Raid raid = raidRepository.getById(raidId);
        final LocalDate raidDate = raid.getEndOfRaid().toLocalDate();
        final LocalDateTime raidStart = Utils.getStartOfRaid(raid.getEndOfRaid(), raid.isExRaid());
        LocalDateTime startAt = LocalDateTime.of(raidDate, startAtTime);
        checkValidInput(user, locale, startAtTime, localeService, raidRepository, raid, raidStart, startAt);

        final EmoticonSignUpMessageListener emoticonSignUpMessageListener =
                new EmoticonSignUpMessageListener(botService, localeService,
                        serverConfigRepository, raidRepository, pokemonRepository, gymRepository,
                        raid.getId(), startAt, user);
        TimeUnit delayTimeUnit = raid.isExRaid() ? TimeUnit.MINUTES : TimeUnit.SECONDS;
        int delay = raid.isExRaid() ? 1 : 15;
        final MessageEmbed messageEmbed = getRaidGroupMessageEmbed(startAt, raid.getId(), localeService,
                clockService, locale, delayTimeUnit, delay, raidRepository);
        channel.sendMessage(messageEmbed).queue(embed -> {
            final String messageId = embed.getId();
            emoticonSignUpMessageListener.setInfoMessageId(messageId);
            emoticonSignUpMessageListener.setEmoteMessageId(messageId);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Thread: " + Thread.currentThread().getId() +
                        " - Adding event listener and emotes for emote message with ID: " + messageId);
            }
            final MessageChannel embedChannel = embed.getChannel();
            RaidGroup group = new RaidGroup(config.getServer(), embedChannel.getName(),
                    messageId, messageId, user.getId(), startAt);
            group = raidRepository.newGroupForRaid(user, group, raid, guild, config);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Created group in channel " + channel.getName() +
                        " for emote message with ID: " + messageId + " - " + group);
            }
            // Add number icons for signups
            embedChannel.addReactionById(messageId, Emotes.ONE).queue();
            embedChannel.addReactionById(messageId, Emotes.TWO).queue();
            embedChannel.addReactionById(messageId, Emotes.THREE).queue();
            embedChannel.addReactionById(messageId, Emotes.FOUR).queue();
            embedChannel.addReactionById(messageId, Emotes.FIVE).queue();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Eventlistener and emotes added for emote message with ID: " + messageId);
            }
            if (config.isPinGroups()) {
                embedChannel.pinMessageById(embed.getId()).queueAfter(50, TimeUnit.MILLISECONDS);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Pinning info message for raid group. ID is: " + embed.getId());
                }
            }
            final Callable<Boolean> refreshEditThreadTask =
                        getMessageRefreshingTaskToSchedule(channel,
                                raid, emoticonSignUpMessageListener, messageId,
                                locale,
                                raidRepository, localeService,
                                clockService, executorService, botService,
                                delayTimeUnit, delay, group.getId());
            executorService.submit(refreshEditThreadTask);
        });
    }

    private static void checkValidInput(User user, Locale locale, LocalTime startAtTime,
                                        LocaleService localeService, RaidRepository raidRepository,
                                        Raid raid, LocalDateTime raidStart, LocalDateTime startAt) {
        Utils.assertGroupStartNotBeforeRaidStart(raidStart, startAt, user, localeService);
        if (!raid.isExRaid()) {
            assertTimeNotMoreThanXHoursFromNow(user, startAtTime, localeService, 2);
        }
        assertGroupTimeNotBeforeNow(user, startAt, localeService);
        if (!startAt.isBefore(raid.getEndOfRaid())) {
            final String errorText = localeService.getMessageFor(LocaleService.CANT_CREATE_GROUP_LATE,
                    locale);
            throw new UserMessedUpException(user, errorText);
        }

        if (raidRepository.userHasGroupForRaid(user, raid)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.TOO_MANY_GROUPS,
                    localeService.getLocaleForUser(user)));
        }

        if (raidRepository.existsGroupForRaidAt(raid, startAt)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.GROUP_NOT_ADDED,
                    localeService.getLocaleForUser(user), String.valueOf(raid)));
        }
    }

    private static void assertAllParametersNotNullOrEmpty(MessageChannel channel, Config config, User user, Locale locale,
                                                          LocalTime startAtTime, String raidId, LocaleService localeService,
                                                          RaidRepository raidRepository, BotService botService,
                                                          ServerConfigRepository serverConfigRepository,
                                                          PokemonRepository pokemonRepository, GymRepository gymRepository,
                                                          ClockService clockService, ExecutorService executorService) {
        Validate.notNull(channel, "Channel");
        Validate.notNull(config, "config");
        Validate.notNull(user, "User");
        Validate.notNull(locale, "Locale");
        Validate.notNull(startAtTime, "StartAtTime");
        Validate.notNull(localeService, "LocaleService");
        Validate.notNull(raidRepository, "RaidRepository");
        Validate.notNull(botService, "BotService");
        Validate.notNull(serverConfigRepository, "ServerConfigRepository");
        Validate.notNull(pokemonRepository, "PokemonRepository");
        Validate.notNull(gymRepository, "GymRepository");
        Validate.notNull(clockService, "ClockService");
        Validate.notNull(executorService, "ExecutorService");
        Validate.notEmpty(raidId, "Raid ID");
    }

    public static Callable<Boolean> getMessageRefreshingTaskToSchedule(MessageChannel messageChannel,
                                                                       Raid raid,
                                                                       EmoticonSignUpMessageListener emoticonSignUpMessageListener,
                                                                       String infoMessageId, Locale locale,
                                                                       RaidRepository raidRepository,
                                                                       LocaleService localeService,
                                                                       ClockService clockService,
                                                                       ExecutorService executorService,
                                                                       BotService botService,
                                                                       TimeUnit delayTimeUnit, int delay,
                                                                       String raidGroupId) {
        Callable<Boolean> refreshEditThreadTask = () -> {
            final String groupId = raidGroupId;
            final Raid currentStateOfRaid = raidRepository.getById(raid.getId());
            final Callable<Boolean> editTask = () -> {
                delayTimeUnit.sleep(delay);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Thread: " + Thread.currentThread().getId() +
                            " - Updating for group at gym " + currentStateOfRaid.getGym().getName() +
                            ": message ID=" + infoMessageId);
                }
                LocalDateTime start = emoticonSignUpMessageListener.getStartAt();
                final MessageEmbed newContent =
                        getRaidGroupMessageEmbed(start, raid.getId(),
                                localeService, clockService, locale, delayTimeUnit, delay, raidRepository
                        );
                messageChannel.editMessageById(infoMessageId,
                        newContent)
                        .queue(m -> {
                            if (LOGGER.isTraceEnabled()) {
                                LOGGER.trace("Message edit ok for " + infoMessageId);
                            }
                        }, m -> {
                            LOGGER.warn(m.getClass().getName() + " occurred in edit message loop: " + m.getMessage());
                            if (Utils.isExceptionOrCauseNetworkIssues(m)) {
                                LOGGER.info("Exception was due to timeout, so trying again soon. Could be temporary.");
                                try {
                                    // If we get a timeout, it's probably due to discord having issues.
                                    // Try to delay 60 seconds and try again.
                                    LOGGER.debug("Since we had a timeout, trying to wait 60 seconds before trying again.");
                                    delayTimeUnit.sleep(60000);
                                } catch (InterruptedException e) {
                                }
                            } else {
                                LOGGER.info("Exception was not due to timeout, so terminating this group.");
                                emoticonSignUpMessageListener.setStartAt(null);
                                LOGGER.info("Raid group will now be cleaned up for raid: " +
                                        raidCleanUpInfo(raid, emoticonSignUpMessageListener));
                                cleanUpRaidGroupAndDeleteSignUpsIfPossible(messageChannel,
                                        emoticonSignUpMessageListener.getStartAt(),
                                        currentStateOfRaid != null ? currentStateOfRaid.getId() : null,
                                        emoticonSignUpMessageListener, raidRepository, botService, groupId);
                            }
                        });
                return true;
            };
            while (raidIsActiveAndRaidGroupNotExpired(currentStateOfRaid.getEndOfRaid(),
                    emoticonSignUpMessageListener.getStartAt(), clockService)) {
                try {
                    executorService.submit(editTask).get();
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Edit task done for now: " + infoMessageId);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            LOGGER.info("Raid group will now be cleaned up for raid: " +
                    raidCleanUpInfo(raid, emoticonSignUpMessageListener));
            cleanUpRaidGroupAndDeleteSignUpsIfPossible(messageChannel, emoticonSignUpMessageListener.getStartAt(),
                    raid != null ? raid.getId() : null,
                    emoticonSignUpMessageListener, raidRepository, botService, groupId);
            return true;
        };
        return refreshEditThreadTask;
    }

    private static String raidCleanUpInfo(Raid raid, EmoticonSignUpMessageListener emoticonSignUpMessageListener) {
        return (raid == null ? emoticonSignUpMessageListener.getRaidId() : raid) +
                ", group at time " +
                (emoticonSignUpMessageListener.getStartAt() == null ? "N/A" :
                        printTime(emoticonSignUpMessageListener.getStartAt().toLocalTime())) +
                " and creator: " + emoticonSignUpMessageListener.getUserId();
    }

    private static boolean raidIsActiveAndRaidGroupNotExpired(LocalDateTime endOfRaid,
                                                              LocalDateTime raidGroupStartTime,
                                                              ClockService clockService) {
        final LocalDateTime currentDateTime = clockService.getCurrentDateTime();
        return raidGroupStartTime != null &&
                currentDateTime.isBefore(
                        raidGroupStartTime.plusMinutes(5))
                // 20 seconds here to match the 15 second sleep for the edit task
                && currentDateTime.isBefore(endOfRaid.minusSeconds(20));
    }

    public static void cleanUpRaidGroupAndDeleteSignUpsIfPossible(MessageChannel messageChannel,
                                                                  LocalDateTime startAt, String raidId,
                                                                  EmoticonSignUpMessageListener emoticonSignUpMessageListener,
                                                                  RaidRepository raidRepository,
                                                                  BotService botService, String groupId) {
        Raid raid = null;
        try {
            if (startAt != null && raidId != null) {
                // Clean up all signups that should have done their raid now, if there still is a time
                // (Could be set to null due to an error, in that case keep signups in database)
                raid = raidRepository.removeAllSignUpsAt(raidId, startAt);
            }
        } catch (Throwable t) {
            // Do nothing, just log
            LOGGER.warn("Exception occurred when removing signups: " + t + "-" + t.getMessage());
            if (t instanceof ConcurrentModificationException) {
                LOGGER.warn("This is probably due to raid being removed while cleaning up signups, which is normal.");
            }
        } finally {
            final String raidDescription = raid == null ? "null" : raid.toString();
            cleanUpGroupMessageAndEntity(messageChannel, raidId, emoticonSignUpMessageListener, raidRepository,
                    botService, groupId, raidDescription);
        }
    }

    public static void cleanUpGroupMessageAndEntity(MessageChannel messageChannel, String raidId,
                                                       EmoticonSignUpMessageListener emoticonSignUpMessageListener,
                                                       RaidRepository raidRepository, BotService botService,
                                                       String groupId, String raidDescription) {
        // Clean up after raid expires
        final String infoMessageId = emoticonSignUpMessageListener.getInfoMessageId();
        if (!StringUtils.isEmpty(infoMessageId)) {
            try {
                messageChannel.deleteMessageById(infoMessageId).queue(m -> {}, t -> {
                    LOGGER.warn("Exception occurred when removing group message: " + t.getMessage());
                    botService.getBot().removeEventListener(emoticonSignUpMessageListener);
                    raidRepository.deleteGroupInNewTransaction(raidId, groupId);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Cleaned up listener related to this group.");
                    }
                });
            } catch (Throwable t) {
                LOGGER.warn("Exception occurred when removing group message: " + t.getMessage());
            }
        } else {
            LOGGER.warn("Group message Id was null for raid group for raid: " +
                    emoticonSignUpMessageListener.getRaidId());
        }
        botService.getBot().removeEventListener(emoticonSignUpMessageListener);
        raidRepository.deleteGroupInNewTransaction(raidId, groupId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaned up listener and message related to the group with ID " + groupId +
                    " for raid " + raidDescription);
        }
    }

    private static MessageEmbed getRaidGroupMessageEmbed(LocalDateTime startAt, String raidId,
                                                         LocaleService localeService, ClockService clockService,
                                                         Locale locale, TimeUnit delayTimeUnit,
                                                         int delay, RaidRepository raidRepository) {
        Raid currentStateOfRaid = raidRepository.getById(raidId);
        final Gym gym = currentStateOfRaid.getGym();
        final Pokemon pokemon = currentStateOfRaid.getPokemon();
        MessageEmbed messageEmbed;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        final String headline = localeService.getMessageFor(LocaleService.GROUP_HEADLINE,
                locale, currentStateOfRaid.getPokemon().getName(), gym.getName()
                        + (gym.isExGym() ? Emotes.STAR : ""));
        final String getHereText = localeService.getMessageFor(LocaleService.GETTING_HERE,
                locale);
        embedBuilder.setTitle(getHereText + " " + gym.getName(),
                Utils.getNonStaticMapUrl(gym));
        final LocalDateTime endOfRaid = currentStateOfRaid.getEndOfRaid();
        embedBuilder.setAuthor(headline + " " +
                Utils.printTime(Utils.getStartOfRaid(endOfRaid, currentStateOfRaid.isExRaid()).toLocalTime()) +
                "-" + Utils.printTime(endOfRaid.toLocalTime()), null, Utils.getPokemonIcon(pokemon));
        final Set<SignUp> signUpsAt = currentStateOfRaid.getSignUpsAt(startAt.toLocalTime());
        final Set<String> signUpNames = getNamesOfThoseWithSignUps(signUpsAt, false);
        final String allSignUpNames = signUpNames.size() > 0 ? StringUtils.join(signUpNames, ", ") : "-";
        final int numberOfPeopleArrivingAt = signUpsAt.stream().mapToInt(SignUp::getHowManyPeople).sum();
        final String numberOfSignupsText = localeService.getMessageFor(LocaleService.SIGNED_UP,
                locale);
        final String totalSignUpsText = numberOfSignupsText + ": " + numberOfPeopleArrivingAt;
        final String thoseWhoAreComingText = localeService.getMessageFor(LocaleService.WHO_ARE_COMING,
                locale) + ":";
        embedBuilder.clearFields();
        final String handleSignUpText =
                localeService.getMessageFor(LocaleService.HANDLE_SIGNUP, locale);
        final Set<RaidGroup> groups = raidRepository.getGroups(currentStateOfRaid);
        final String descriptionAppendixText;
        if (groups.size() > 1) {
            descriptionAppendixText = raidRepository.listGroupsForRaid(currentStateOfRaid, groups);
        } else {
            descriptionAppendixText = "- " + handleSignUpText;
        }
        embedBuilder.setDescription("**Start: " + printTimeIfSameDay(startAt) + "** " + descriptionAppendixText);
        embedBuilder.addField(totalSignUpsText + ". " + thoseWhoAreComingText, allSignUpNames, true);
        final String updatedMessage = localeService.getMessageFor(LocaleService.UPDATED_EVERY_X,
                locale, LocaleService.asString(delayTimeUnit, locale),
                String.valueOf(delay)) + " " + localeService.getMessageFor(LocaleService.LAST_UPDATE,
                locale,
                printTime(clockService.getCurrentTime())) + ".";

        embedBuilder.setFooter(updatedMessage, null);
        messageEmbed = embedBuilder.build();
        return messageEmbed;
    }
}
