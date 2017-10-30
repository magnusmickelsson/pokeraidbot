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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.*;

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
        String timeString = args[0];
        LocalTime startAtTime = Utils.parseTime(user, timeString, localeService);
        LocalDateTime startAt = LocalDateTime.of(LocalDate.now(), startAtTime);

        assertTimeNotInNoRaidTimespan(user, startAtTime, localeService);
        assertTimeNotMoreThanXHoursFromNow(user, startAtTime, localeService, 2);
        assertCreateRaidTimeNotBeforeNow(user, startAt, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        if (!startAt.isBefore(raid.getEndOfRaid())) {
            final String errorText = localeService.getMessageFor(LocaleService.CANT_CREATE_GROUP_LATE,
                    localeService.getLocaleForUser(user));
            throw new UserMessedUpException(userName, errorText);
        }

        // todo: Check that user doesn't have more than one group for a raid?
        // In that case he/she should use raid change to change time

        final EmoticonSignUpMessageListener emoticonSignUpMessageListener =
                new EmoticonSignUpMessageListener(botService, localeService,
                        serverConfigRepository, raidRepository, pokemonRepository, gymRepository,
                        raid.getId(), startAt, user);
        final MessageEmbed messageEmbed = getRaidGroupMessageEmbed(user, startAt, raid, localeService);
        commandEvent.reply(messageEmbed, embed -> {
            emoticonSignUpMessageListener.setInfoMessageId(embed.getId());
            final String handleSignUpText =
                    localeService.getMessageFor(LocaleService.HANDLE_SIGNUP, localeService.getLocaleForUser(user));
            embed.getChannel().sendMessage(handleSignUpText).queue(
                    msg -> {
                        final String messageId = msg.getId();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Thread: " + Thread.currentThread().getId() +
                                    " - Adding event listener and emotes for emote message with ID: " + messageId);
                        }
                        emoticonSignUpMessageListener.setEmoteMessageId(messageId);
                        botService.getBot().addEventListener(emoticonSignUpMessageListener);
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
                    getMessageRefreshingTaskToSchedule(commandEvent, user,
                            raid, emoticonSignUpMessageListener, embed);
            executorService.submit(refreshEditThreadTask);
        });

    }

    private Callable<Boolean> getMessageRefreshingTaskToSchedule(CommandEvent commandEvent, User user,
                                                                 Raid raid,
                                                                 EmoticonSignUpMessageListener emoticonSignUpMessageListener,
                                                                 Message embed) {
        final MessageChannel channel = commandEvent.getTextChannel();
        Callable<Boolean> refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                TimeUnit.SECONDS.sleep(15);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Thread: " + Thread.currentThread().getId() +
                            " - Updating message with ID " + embed.getId());
                }
                LocalDateTime start = emoticonSignUpMessageListener.getStartAt();
                final MessageEmbed newContent =
                        getRaidGroupMessageEmbed(user, start, raidRepository.getById(raid.getId()), localeService);
                embed.getChannel().editMessageById(embed.getId(),
                        newContent)
                        .queue(m -> {
                        }, m -> {
                            emoticonSignUpMessageListener.setStartAt(null);
                        });
                return true;
            };
            while (raidIsActiveAndRaidGroupNotExpired(raid.getEndOfRaid(), emoticonSignUpMessageListener.getStartAt())) {
                try {
                    executorService.submit(editTask).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            LOGGER.debug("Raid group will now be cleaned up. Raid ID: " + emoticonSignUpMessageListener.getRaidId() +
                    ", creator: " + emoticonSignUpMessageListener.getUserId());
            cleanUp(commandEvent, emoticonSignUpMessageListener.getStartAt(), raid.getId(),
                    emoticonSignUpMessageListener);

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

    private boolean raidIsActiveAndRaidGroupNotExpired(LocalDateTime endOfRaid,
                                                       LocalDateTime raidGroupStartTime) {
        final LocalDateTime currentDateTime = clockService.getCurrentDateTime();
        return raidGroupStartTime != null &&
                currentDateTime.isBefore(
                        raidGroupStartTime.plusMinutes(5))
                // 20 seconds here to match the 15 second sleep for the edit task
                && currentDateTime.isBefore(endOfRaid.minusSeconds(20));
    }

    private void cleanUp(CommandEvent commandEvent, LocalDateTime startAt, String raidId,
                         EmoticonSignUpMessageListener emoticonSignUpMessageListener) {
        Raid raid = null;
        try {
            if (startAt != null) {
                // Clean up all signups that should have done their raid now, if there still is a time
                // (Could be set to null due to an error, in that case keep signups in database)
                raid = raidRepository.removeAllSignUpsAt(raidId, startAt);
            }
        } catch (Throwable t) {
            // Do nothing, just log
            LOGGER.warn("Exception occurred when removing signups: " + t + "-" + t.getMessage());
        } finally {
            // Clean up after raid expires
            final String emoteMessageId = emoticonSignUpMessageListener.getEmoteMessageId();
            if (!StringUtils.isEmpty(emoteMessageId)) {
                try {
                    commandEvent.getChannel().deleteMessageById(emoteMessageId).queue();
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
                    commandEvent.getChannel().deleteMessageById(infoMessageId).queue();
                } catch (Throwable t) {
                    LOGGER.warn("Exception occurred when removing group message: " + t.getMessage());
                }
            } else {
                LOGGER.warn("Group message Id was null for raid group for raid: " +
                        emoticonSignUpMessageListener.getRaidId());
            }
            botService.getBot().removeEventListener(emoticonSignUpMessageListener);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cleaned up listener and messages related to this group - raid: " + (raid == null ?
                        "not cleaned up :( - had ID: " + raidId : raid) +
                        " , start time: " + startAt);
            }
        }
    }

    public static MessageEmbed getRaidGroupMessageEmbed(User user, LocalDateTime startAt, Raid raid,
                                                        LocaleService localeService) {
        final Gym gym = raid.getGym();
        final Pokemon pokemon = raid.getPokemon();
        MessageEmbed messageEmbed;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        final String headline = localeService.getMessageFor(LocaleService.GROUP_HEADLINE,
                localeService.getLocaleForUser(user), raid.getPokemon().getName(), gym.getName(),
                Utils.printTimeIfSameDay(startAt));
        final String getHereText = localeService.getMessageFor(LocaleService.GETTING_HERE,
                localeService.getLocaleForUser(user));
        embedBuilder.setTitle(getHereText, Utils.getNonStaticMapUrl(gym));
        embedBuilder.setAuthor(headline, null, Utils.getPokemonIcon(pokemon));
        final Set<SignUp> signUpsAt = raid.getSignUpsAt(startAt.toLocalTime());
        final Set<String> signUpNames = getNamesOfThoseWithSignUps(signUpsAt, false);
        final String allSignUpNames = signUpNames.size() > 0 ? StringUtils.join(signUpNames, ", ") : "-";
        final int numberOfPeopleArrivingAt = signUpsAt.stream().mapToInt(s -> s.getHowManyPeople()).sum();
        final String numberOfSignupsText = localeService.getMessageFor(LocaleService.SIGNED_UP,
                localeService.getLocaleForUser(user));
        final String totalSignUpsText = numberOfSignupsText + ": " + numberOfPeopleArrivingAt;
        final String thoseWhoAreComingText = localeService.getMessageFor(LocaleService.WHO_ARE_COMING,
                localeService.getLocaleForUser(user)) + ":";
        embedBuilder.clearFields();
        embedBuilder.addField(totalSignUpsText + ". " + thoseWhoAreComingText, allSignUpNames, true);
        embedBuilder.setFooter(localeService.getMessageFor(LocaleService.GROUP_MESSAGE_TO_BE_REMOVED,
                localeService.getLocaleForUser(user)), null);
        messageEmbed = embedBuilder.build();
        return messageEmbed;
    }
}
