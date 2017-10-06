package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
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
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.*;

import static pokeraidbot.Utils.*;

/**
 * !raid group [start raid at (HH:MM)] [Pokestop name]
 */
public class NewRaidGroupCommand extends ConfigAwareCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewRaidGroupCommand.class);

    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final BotService botService;
    private final ClockService clockService;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public NewRaidGroupCommand(GymRepository gymRepository, RaidRepository raidRepository,
                               PokemonRepository pokemonRepository, LocaleService localeService,
                               ConfigRepository configRepository,
                               CommandListener commandListener, BotService botService, ClockService clockService) {
        super(configRepository, commandListener);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.botService = botService;
        this.clockService = clockService;
        this.name = "group";
        this.help = " Skapa ett tillfälle för en grupp att köra vid en skapad raid: " +
                "!raid group [start time (HH:MM)] [gym name]";
        //localeService.getMessageFor(LocaleService.NEW_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final List<Emote> mystic = commandEvent.getGuild().getEmotesByName("mystic", true);
        final List<Emote> instinct = commandEvent.getGuild().getEmotesByName("instinct", true);
        final List<Emote> valor = commandEvent.getGuild().getEmotesByName("valor", true);
        assertAtLeastOneEmote(mystic);
        assertAtLeastOneEmote(instinct);
        assertAtLeastOneEmote(valor);

        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final String[] args = commandEvent.getArgs().split(" ");
        String timeString = args[0];
        LocalTime startAtTime = Utils.parseTime(userName, timeString);
        LocalDateTime startAt = LocalDateTime.of(LocalDate.now(), startAtTime);

        assertTimeNotInNoRaidTimespan(userName, startAtTime, localeService);
        assertTimeNotMoreThanXHoursFromNow(userName, startAtTime, localeService, 2);
        assertCreateRaidTimeNotBeforeNow(userName, startAt, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(userName, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());
        if (!startAt.isBefore(raid.getEndOfRaid())) {
            // todo: i18n
//            throw new UserMessedUpException(userName, "Can't create a group to raid after raid has ended. :(");
            throw new UserMessedUpException(userName, "Kan inte skapa en grupp som ska samlas efter att raiden slutat.");
        }
        // todo: Link emoticons to actions against the bot
        // todo: locale service
        // todo: i18n
        final EmoticonSignUpMessageListener emoticonSignUpMessageListener = new EmoticonSignUpMessageListener(botService, localeService,
                configRepository, raidRepository, pokemonRepository, gymRepository, raid.getId(), startAt);
        final MessageEmbed messageEmbed = getRaidGroupMessageEmbed(userName, startAt, raid);
        commandEvent.reply(messageEmbed, embed -> {
            emoticonSignUpMessageListener.setInfoMessageId(embed.getId());
            embed.getChannel().sendMessage(
                    "Hantera anmälning via knapparna nedan. För hjälp, skriv \"!raid man group\".").queue(
                    msg -> {
                        final String messageId = msg.getId();
                        LOGGER.debug("Thread: " + Thread.currentThread().getId() +
                                " - Adding event listener and emotes for emote message with ID: " + messageId);
                        emoticonSignUpMessageListener.setEmoteMessageId(messageId);
                        botService.getBot().addEventListener(emoticonSignUpMessageListener);
                        // Get first icon of each team (if there is more than one)
                        msg.getChannel().addReactionById(msg.getId(), mystic.iterator().next()).queue();
                        msg.getChannel().addReactionById(msg.getId(), valor.iterator().next()).queue();
                        msg.getChannel().addReactionById(msg.getId(), instinct.iterator().next()).queue();
                        // Add number icons for pleb signups
                        msg.getChannel().addReactionById(msg.getId(), Emotes.ONE).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.TWO).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.THREE).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.FOUR).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.FIVE).queue();
                        msg.getChannel().addReactionById(msg.getId(), Emotes.SIX).queue();
                        LOGGER.debug("Eventlistener and emotes added for emote message with ID: " + messageId);
                        msg.getChannel().pinMessageById(embed.getId()).queueAfter(50, TimeUnit.MILLISECONDS);
                        LOGGER.debug("Pinning info message for raid group. ID is: " + embed.getId());
                    });
            final Callable<Boolean> refreshEditThreadTask =
                    getMessageRefreshingTaskToSchedule(commandEvent, user, startAt, gymName,
                            raid, emoticonSignUpMessageListener, embed);
            executorService.submit(refreshEditThreadTask);
        });

    }

    private Callable<Boolean> getMessageRefreshingTaskToSchedule(CommandEvent commandEvent, User user,
                                                                 LocalDateTime startAt,
                                                                 String gymName, Raid raid,
                                                                 EmoticonSignUpMessageListener emoticonSignUpMessageListener,
                                                                 Message embed) {
        Callable<Boolean> refreshEditThreadTask;
        final String userName = user.getName();
        refreshEditThreadTask = () -> {
            final Callable<Boolean> editTask = () -> {
                try {
                    TimeUnit.SECONDS.sleep(15);
                    LOGGER.debug("Thread: " + Thread.currentThread().getId() + " - Updating message with ID " + embed.getId());
                    embed.getChannel().editMessageById(embed.getId(),
                            getRaidGroupMessageEmbed(userName, startAt, raidRepository.getById(raid.getId())))
                            .queue();
                    return true;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            do {
                try {
                    executorService.submit(editTask).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } while (clockService.getCurrentDateTime().isBefore(startAt));
            LOGGER.debug("Raid has now expired, will clean up listener and messages..");
            // Clean up after raid expires
            final String emoteMessageId = emoticonSignUpMessageListener.getEmoteMessageId();
            if (!StringUtils.isEmpty(emoteMessageId)) {
                commandEvent.getChannel().deleteMessageById(emoteMessageId).queue();
            }
            final String infoMessageId = emoticonSignUpMessageListener.getInfoMessageId();
            if (!StringUtils.isEmpty(emoteMessageId)) {
                commandEvent.getChannel().deleteMessageById(infoMessageId).queue();
            }
            botService.getBot().removeEventListener(emoticonSignUpMessageListener);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cleaned up listener and messages related to this group - raid: " + raid +
                        " , start time: " + startAt);
            }
            // todo: should we automatically remove signups for this group when time expires from total? Makes sense.
            commandEvent.reply(user.getAsMention() + " Tog bort din grupp som skulle börja raiden vid " +
                    printTimeIfSameDay(startAt) + ", tiden har nu passerat. Era signups står kvar på raidens total, tills " +
                    "ni kör kommandot \"!raid remove " + gymName + "\" eller raiden tar slut.\n" +
                    "Om ni vill köra en ny grupp lite senare, " +
                    "så kör kommandot \"!raid group {tid}\" igen med en senare tid."
            );
            return true;
        };
        return refreshEditThreadTask;
    }

    public static MessageEmbed getRaidGroupMessageEmbed(String userName, LocalDateTime startAt, Raid raid) {
        final Gym gym = raid.getGym();
        final Pokemon pokemon = raid.getPokemon();
        MessageEmbed messageEmbed;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(userName + "s grupp @ " + gym.getName() + ", samling " +
                Utils.printTimeIfSameDay(startAt));
        embedBuilder.setAuthor(null, null, null);
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("Pokemon: **").append(pokemon).append("**.");
        descriptionBuilder.append("\nAnmälda totalt till raiden: ")
                .append("**").append(raid.getNumberOfPeopleSignedUp()).append("**");
        // todo: lista över alla signups som ska komma vid den här tiden? Summera per lag?
        final LocalTime startAtTime = startAt.toLocalTime();
        descriptionBuilder.append("\nAnmälda att komma ").append(printTime(startAtTime)).append(": ")
                .append("**").append(raid.getNumberOfPeopleArrivingAt(startAtTime)).append("**");
        descriptionBuilder.append("\nFör tips, skriv:" +
                "\n*!raid vs ").append(pokemon.getName()).append("*\n");
        descriptionBuilder.append("Hitta hit: [Google Maps](").append(Utils.getNonStaticMapUrl(gym))
                .append(")");
        embedBuilder.setDescription(descriptionBuilder.toString());
        messageEmbed = embedBuilder.build();
        return messageEmbed;
    }

    private void assertAtLeastOneEmote(List<Emote> mystic) {
        if (mystic == null || mystic.size() < 1) {
            // todo: i18n
            throw new RuntimeException("Administratören för denna server har inte installerat pokeraidbot's emotes. " +
                    "Se till att hen kör följande kommando: !raid install-emotes");
//            throw new RuntimeException("Administrator has not installed pokeraidbot's emotes. " +
//                    "Ensure he/she runs the following command: !raid install-emotes");
        }
    }
}
