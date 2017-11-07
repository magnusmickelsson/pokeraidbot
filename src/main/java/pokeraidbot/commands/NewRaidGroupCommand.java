package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
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

    public NewRaidGroupCommand(GymRepository gymRepository, RaidRepository raidRepository,
                               PokemonRepository pokemonRepository, LocaleService localeService,
                               ServerConfigRepository serverConfigRepository,
                               CommandListener commandListener, BotService botService,
                               ClockService clockService, ExecutorService executorService) {
        super(serverConfigRepository, commandListener, localeService, executorService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.botService = botService;
        this.clockService = clockService;
        this.name = "group";
        this.help = localeService.getMessageFor(LocaleService.RAID_GROUP_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final String[] args = commandEvent.getArgs().split(" ");
        final Locale locale = localeService.getLocaleForUser(user);
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
        final LocalDate raidDate = raid.getEndOfRaid().toLocalDate();
        final LocalDateTime raidStart = Utils.getStartOfRaid(raid.getEndOfRaid(), raid.isExRaid());
        LocalDateTime startAt = LocalDateTime.of(raidDate, startAtTime);
        Utils.assertGroupStartNotBeforeRaidStart(raidStart, startAt, user, localeService);
        if (!raid.isExRaid()) {
            assertTimeNotMoreThanXHoursFromNow(user, startAtTime, localeService, 2);
        }
        assertCreateRaidTimeNotBeforeNow(user, startAt, localeService);
        if (!startAt.isBefore(raid.getEndOfRaid())) {
            final String errorText = localeService.getMessageFor(LocaleService.CANT_CREATE_GROUP_LATE,
                    locale);
            throw new UserMessedUpException(userName, errorText);
        }

        if (raidRepository.hasGroupForRaid(user, raid, startAt)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.GROUP_NOT_ADDED,
                    localeService.getLocaleForUser(user), String.valueOf(raid)));
        }

        final EmoticonSignUpMessageListener emoticonSignUpMessageListener =
                new EmoticonSignUpMessageListener(botService, localeService,
                        serverConfigRepository, raidRepository, pokemonRepository, gymRepository,
                        raid.getId(), startAt, user);
        TimeUnit delayTimeUnit = raid.isExRaid() ? TimeUnit.MINUTES : TimeUnit.SECONDS;
        int delay = raid.isExRaid() ? 1 : 15;
        final MessageEmbed messageEmbed = getRaidGroupMessageEmbed(startAt, raid, localeService,
                clockService, locale, delayTimeUnit, delay);
        commandEvent.reply(messageEmbed, embed -> {
            emoticonSignUpMessageListener.setInfoMessageId(embed.getId());
            final String handleSignUpText =
                    localeService.getMessageFor(LocaleService.HANDLE_SIGNUP, locale);
            embed.getChannel().sendMessage(handleSignUpText).queue(
                    msg -> {
                        final String messageId = msg.getId();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Thread: " + Thread.currentThread().getId() +
                                    " - Adding event listener and emotes for emote message with ID: " + messageId);
                        }
                        emoticonSignUpMessageListener.setEmoteMessageId(messageId);
                        RaidGroup group = new RaidGroup(config.getServer(), msg.getChannel().getName(),
                                embed.getId(), messageId, user.getId(), startAt);
                        group = raidRepository.newGroupForRaid(user, group, raid);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Created group for emote message with ID: " + messageId + " - " + group);
                        }
//                        botService.getBot().addEventListener(emoticonSignUpMessageListener);
                        // Add number icons for pleb signups
                        msg.getChannel().addReactionById(msg.getId(), Emotes.ONE).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.TWO).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.THREE).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.FOUR).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.FIVE).queue();
//                        msg.getChannel().addReactionById(msg.getId(), Emotes.SIX).queue();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Eventlistener and emotes added for emote message with ID: " + messageId);
                        }
                        msg.getChannel().pinMessageById(embed.getId()).queueAfter(50, TimeUnit.MILLISECONDS);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Pinning info message for raid group. ID is: " + embed.getId());
                        }
                    });
            final Callable<Boolean> refreshEditThreadTask =
                    getMessageRefreshingTaskToSchedule(commandEvent.getChannel(),
                            raid, emoticonSignUpMessageListener, embed.getId(),
                            locale,
                            raidRepository, localeService, clockService, executorService, botService,
                            delayTimeUnit, delay);
            executorService.submit(refreshEditThreadTask);
        });

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
                                                                 TimeUnit delayTimeUnit, int delay) {
        Callable<Boolean> refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                delayTimeUnit.sleep(delay);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Thread: " + Thread.currentThread().getId() +
                            " - Updating message with ID " + infoMessageId);
                }
                LocalDateTime start = emoticonSignUpMessageListener.getStartAt();
                final MessageEmbed newContent =
                        getRaidGroupMessageEmbed(start, raidRepository.getById(raid.getId()),
                                localeService, clockService, locale, delayTimeUnit, delay);
                messageChannel.editMessageById(infoMessageId,
                        newContent)
                        .queue(m -> {
                        }, m -> {
                            LOGGER.warn(m.getClass().getName() + " occurred in edit message loop: " + m.getMessage());
                            emoticonSignUpMessageListener.setStartAt(null);
                        });
                return true;
            };
            while (raidIsActiveAndRaidGroupNotExpired(raid.getEndOfRaid(),
                    emoticonSignUpMessageListener.getStartAt(), clockService)) {
                try {
                    executorService.submit(editTask).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            LOGGER.info("Raid group will now be cleaned up. Raid ID: " + emoticonSignUpMessageListener.getRaidId() +
                    ", creator: " + emoticonSignUpMessageListener.getUserId());
            cleanUp(messageChannel, emoticonSignUpMessageListener.getStartAt(), raid != null ? raid.getId() : null,
                    emoticonSignUpMessageListener, raidRepository, botService);

            // todo: Have a "removed message" message?
//            final LocalDateTime startAt = emoticonSignUpMessageListener.getStartAt();
//            final String removedGroupText = localeService.getMessageFor(LocaleService.REMOVED_GROUP,
//                    localeService.getLocaleForUser(user),
//                    startAt == null ? "N/A" : printTimeIfSameDay(startAt), gymName);
//            channel.sendMessage(user.getAsMention() + ": " + removedGroupText).queue(
//                    msg -> {
//                        msg.delete().queueAfter(20, TimeUnit.SECONDS);
//                    }
//            );
            return true;
        };
        return refreshEditThreadTask;
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

    private static void cleanUp(MessageChannel messageChannel, LocalDateTime startAt, String raidId,
                                EmoticonSignUpMessageListener emoticonSignUpMessageListener,
                                RaidRepository raidRepository,
                                BotService botService) {
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
            // Clean up after raid expires
            final String emoteMessageId = emoticonSignUpMessageListener.getEmoteMessageId();
            if (!StringUtils.isEmpty(emoteMessageId)) {
                try {
                    messageChannel.deleteMessageById(emoteMessageId).queue();
                } catch (Throwable t) {
                    LOGGER.warn("Exception occurred when removing emote message: " + t.getMessage());
                }
            } else {
                LOGGER.warn("Emote message Id was null for raid group for raid: " +
                        emoticonSignUpMessageListener.getRaidId());
            }
            final String infoMessageId = emoticonSignUpMessageListener.getInfoMessageId();
            if (!StringUtils.isEmpty(emoteMessageId)) {
                try {
                    messageChannel.deleteMessageById(infoMessageId).queue();
                } catch (Throwable t) {
                    LOGGER.warn("Exception occurred when removing group message: " + t.getMessage());
                }
            } else {
                LOGGER.warn("Group message Id was null for raid group for raid: " +
                        emoticonSignUpMessageListener.getRaidId());
            }
            botService.getBot().removeEventListener(emoticonSignUpMessageListener);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Cleaned up listener and messages related to this group - raid: " + (raid == null ?
                        "not cleaned up :( - had ID: " + raidId : raid) +
                        " , start time: " + startAt);
            }
        }
    }

    public static MessageEmbed getRaidGroupMessageEmbed(LocalDateTime startAt, Raid raid,
                                                        LocaleService localeService, ClockService clockService,
                                                        Locale locale, TimeUnit delayTimeUnit,
                                                        int delay) {
        final Gym gym = raid.getGym();
        final Pokemon pokemon = raid.getPokemon();
        MessageEmbed messageEmbed;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        final String headline = localeService.getMessageFor(LocaleService.GROUP_HEADLINE,
                locale, raid.getPokemon().getName(), gym.getName()); //,
//                Utils.printTimeIfSameDay(startAt));
        final String getHereText = localeService.getMessageFor(LocaleService.GETTING_HERE,
                locale);
        embedBuilder.setTitle(getHereText, Utils.getNonStaticMapUrl(gym));
        embedBuilder.setAuthor(headline, null, Utils.getPokemonIcon(pokemon));
        final Set<SignUp> signUpsAt = raid.getSignUpsAt(startAt.toLocalTime());
        final Set<String> signUpNames = getNamesOfThoseWithSignUps(signUpsAt, false);
        final String allSignUpNames = signUpNames.size() > 0 ? StringUtils.join(signUpNames, ", ") : "-";
        final int numberOfPeopleArrivingAt = signUpsAt.stream().mapToInt(s -> s.getHowManyPeople()).sum();
        final String numberOfSignupsText = localeService.getMessageFor(LocaleService.SIGNED_UP,
                locale);
        final String totalSignUpsText = numberOfSignupsText + ": " + numberOfPeopleArrivingAt;
        final String thoseWhoAreComingText = localeService.getMessageFor(LocaleService.WHO_ARE_COMING,
                locale) + ":";
        embedBuilder.clearFields();
        // todo: i18n
        embedBuilder.setDescription("Start: " + printTimeIfSameDay(startAt));
        embedBuilder.addField(totalSignUpsText + ". " + thoseWhoAreComingText, allSignUpNames, true);
        final String footerMessage = localeService.getMessageFor(LocaleService.UPDATED_EVERY_X,
                locale, LocaleService.asString(delayTimeUnit, locale),
                String.valueOf(delay)) + " " + localeService.getMessageFor(LocaleService.LAST_UPDATE,
                locale,
                printTime(clockService.getCurrentTime()));
        embedBuilder.setFooter(footerMessage, null);
        messageEmbed = embedBuilder.build();
        return messageEmbed;
    }
}
