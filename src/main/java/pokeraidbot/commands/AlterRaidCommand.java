package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.EmoticonSignUpMessageListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static pokeraidbot.Utils.*;

/**
 * !raid change when [New time (HH:MM)] [Pokestop name] (Only administrators or raid creator)
 * !raid change pokemon [Pokemon] [Pokestop name] (Only administrators or raid creator)
 * !raid change remove [Pokestop name] (Only administrators)
 */
// todo: refactor and clean up
public class AlterRaidCommand extends ConfigAwareCommand {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(AlterRaidCommand.class);
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final BotService botService;

    public AlterRaidCommand(GymRepository gymRepository, RaidRepository raidRepository,
                            PokemonRepository pokemonRepository, LocaleService localeService,
                            ServerConfigRepository serverConfigRepository,
                            CommandListener commandListener, BotService botService) {
        super(serverConfigRepository, commandListener, localeService);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.botService = botService;
        this.name = "change";
        this.help = localeService.getMessageFor(LocaleService.CHANGE_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final String[] args = commandEvent.getArgs().split(" ");
        String whatToChange = args[0].trim().toLowerCase();

        switch (whatToChange) {
            case "when":
                changeWhen(commandEvent, config, user, args);
                break;
            case "pokemon":
                changePokemon(this, gymRepository, localeService, pokemonRepository, raidRepository,
                        commandEvent, config, user, userName, args[1].trim().toLowerCase(),
                        ArrayUtils.removeAll(args, 0, 1));
                break;
            case "remove":
                deleteRaid(commandEvent, config, user, userName, args);
                break;
            case "group":
                changeGroup(commandEvent, config, user, userName, args);
                break;
            default:
                throw new UserMessedUpException(userName,
                        localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user)));
        }
    }

    private void changeGroup(CommandEvent commandEvent, Config config, User user, String userName, String[] args) {
        String whatToChangeTo;
        StringBuilder gymNameBuilder;
        String gymName;
        Gym gym;
        Raid raid;
        whatToChangeTo = args[1].trim().toLowerCase();
        gymNameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        gymName = gymNameBuilder.toString().trim();
        gym = gymRepository.search(user, gymName, config.getRegion());
        raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        verifyGroupPermission(commandEvent, user, raid, config);
        LocalTime newTime = parseTime(user, whatToChangeTo, localeService);
        LocalDateTime newDateTime = LocalDateTime.of(raid.getEndOfRaid().toLocalDate(), newTime);

        assertTimeInRaidTimespan(user, newDateTime, raid, localeService);
        assertGroupTimeNotBeforeNow(user, newDateTime, localeService);
        if (raidRepository.existsGroupForRaidAt(raid, newDateTime)) { // Check for any user
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.GROUP_NOT_ADDED,
                    localeService.getLocaleForUser(user), String.valueOf(raid)));
        }

        if (raidRepository.hasManyGroupsForRaid(user, raid)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(
                    LocaleService.MANY_GROUPS_FOR_RAID,
                    localeService.getLocaleForUser(user), String.valueOf(raid)));
        }

        boolean groupChanged = false;
        final Set<EmoticonSignUpMessageListener> listenersToCheck = new HashSet<>();
        for (Object o : botService.getBot().getRegisteredListeners()) {
            if (o instanceof  EmoticonSignUpMessageListener) {
                EmoticonSignUpMessageListener listener = (EmoticonSignUpMessageListener) o;
                final String raidId = raid.getId();
                final boolean isCorrectRaid = raidId.equals(listener.getRaidId());
                final boolean isUsersGroup = user.getId().equals(listener.getUserId());
                if (isCorrectRaid && isUsersGroup) {
                    listenersToCheck.add(listener);
                    break; // If we found user's group, that's the one to change primarily
                }
            }
        }

        // If admin doesn't have his/her own group, they can change group for others, as long as it's just one
        // to choose from. Will be fixed later on
        if (listenersToCheck.size() == 0) {
            for (Object o : botService.getBot().getRegisteredListeners()) {
                if (o instanceof EmoticonSignUpMessageListener) {
                    EmoticonSignUpMessageListener listener = (EmoticonSignUpMessageListener) o;
                    final String raidId = raid.getId();
                    final boolean isCorrectRaid = raidId.equals(listener.getRaidId());
                    if (isCorrectRaid && (isUserAdministrator(commandEvent) ||
                            isUserServerMod(commandEvent, config))) {
                        listenersToCheck.add(listener);
                    }
                }
            }
        }

        if (listenersToCheck.size() > 1) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.MANY_GROUPS_FOR_RAID,
                            localeService.getLocaleForUser(user), String.valueOf(raid)));
        } else if (listenersToCheck.size() == 0) { // todo: could also be due to raid not having any groups
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_PERMISSION,
                            localeService.getLocaleForUser(user)));
        }

        for (EmoticonSignUpMessageListener listener : listenersToCheck) {
            final String raidId = raid.getId();
                final LocalDateTime currentStartAt = listener.getStartAt();
                if (currentStartAt != null && currentStartAt.equals(newDateTime)) {
                    // This group is already at the time to change to
                    // todo: message back?
                    commandEvent.reactError();
                } else if (currentStartAt != null) {
                    LOGGER.info("Changing group time from " + currentStartAt + " to " + newDateTime);
                    RaidGroup raidGroup = raidRepository.changeGroup(user, raidId, listener.getUserId(),
                            currentStartAt, newDateTime);
                    raidRepository.moveAllSignUpsForTimeToNewTime(raidId, currentStartAt, newDateTime, user);
                    listener.setStartAt(newDateTime);
                    groupChanged = true;
                    replyBasedOnConfigAndRemoveAfter(config, commandEvent,
                            localeService.getMessageFor(LocaleService.MOVED_GROUP,
                                    localeService.getLocaleForUser(user),
                                    printTimeIfSameDay(currentStartAt),
                                    printTimeIfSameDay(newDateTime), raid.getGym().getName()),
                            BotServerMain.timeToRemoveFeedbackInSeconds);
                    LOGGER.info("Group time changed. Group: " + raidGroup);
                    commandEvent.reactSuccess();
                } else {
                    commandEvent.reactError();
                    // This group is about to get cleaned up since its start time is null
                    replyBasedOnConfigAndRemoveAfter(config, commandEvent,
                            localeService.getMessageFor(LocaleService.GROUP_CLEANING_UP,
                                    localeService.getLocaleForUser(user)),
                            BotServerMain.timeToRemoveFeedbackInSeconds);
                    return;
                }
        }
        if (!groupChanged) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user)));
        }
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }

    private void deleteRaid(CommandEvent commandEvent, Config config, User user, String userName, String[] args) {
        StringBuilder gymNameBuilder;
        String gymName;
        Gym gym;
        Raid raid;
        gymNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        gymName = gymNameBuilder.toString().trim();
        gym = gymRepository.search(user, gymName, config.getRegion());
        Raid deleteRaid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        verifyPermission(localeService, commandEvent, user, deleteRaid, config);
        final boolean userIsNotAdministrator = !isUserAdministrator(commandEvent);
        final boolean userIsNotMod = !isUserServerMod(commandEvent, config);
        if ((userIsNotAdministrator && userIsNotMod) && deleteRaid.getSignUps().size() > 0) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.ONLY_ADMINS_REMOVE_RAID,
                            localeService.getLocaleForUser(user))
            );
        }
        if (raidRepository.delete(deleteRaid)) {
            raid = null;
            commandEvent.reactSuccess();
            removeOriginMessageIfConfigSaysSo(config, commandEvent);
        } else {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.RAID_NOT_EXISTS,
                            localeService.getLocaleForUser(user)));
        }
    }

    public static void changePokemon(Command command, GymRepository gymRepository, LocaleService localeService,
                                     PokemonRepository pokemonRepository, RaidRepository raidRepository,
                                     CommandEvent commandEvent,
                                     Config config, User user, String userName,
                                     String newPokemonName, String... gymArguments) {
        String whatToChangeTo;
        StringBuilder gymNameBuilder;
        String gymName;
        Gym gym;
        Raid raid;
        whatToChangeTo = newPokemonName;
        gymNameBuilder = new StringBuilder();
        for (String arg : gymArguments) {
            gymNameBuilder.append(arg).append(" ");
        }
        gymName = gymNameBuilder.toString().trim();
        gym = gymRepository.search(user, gymName, config.getRegion());
        Raid pokemonRaid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        final Pokemon pokemon = pokemonRepository.search(whatToChangeTo, user);
        if (pokemonRaid.isExRaid()) {
            throw new UserMessedUpException(userName, localeService.getMessageFor(
                    LocaleService.EX_NO_CHANGE_POKEMON,
                    localeService.getLocaleForUser(user)));
        }
        // Anybody should be able to report hatched eggs
        if (!pokemonRaid.getPokemon().isEgg()) {
            verifyPermission(localeService, commandEvent, user, pokemonRaid, config);
        }
        if (Utils.isRaidExPokemon(whatToChangeTo)) {
            throw new UserMessedUpException(userName, localeService.getMessageFor(
                    LocaleService.EX_CANT_CHANGE_RAID_TYPE, localeService.getLocaleForUser(user)));
        }
        raid = raidRepository.changePokemon(pokemonRaid, pokemon, commandEvent.getGuild(), config, user);
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }

    private void changeWhen(CommandEvent commandEvent, Config config, User user, String[] args) {
        String whatToChangeTo;
        StringBuilder gymNameBuilder;
        String gymName;
        Gym gym;
        LocalTime endsAtTime;
        LocalDateTime endsAt;
        Raid raid;
        whatToChangeTo = args[1].trim().toLowerCase();
        gymNameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        gymName = gymNameBuilder.toString().trim();
        gym = gymRepository.search(user, gymName, config.getRegion());
        Raid tempRaid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        verifyPermission(localeService, commandEvent, user, tempRaid, config);
        endsAtTime = parseTime(user, whatToChangeTo, localeService);
        endsAt = LocalDateTime.of(tempRaid.getEndOfRaid().toLocalDate(), endsAtTime);

        assertTimeNotInNoRaidTimespan(user, endsAtTime, localeService);
        if (!tempRaid.isExRaid()) {
            assertTimeNotMoreThanXHoursFromNow(user, endsAtTime, localeService, 2);
        }
        assertCreateRaidTimeNotBeforeNow(user, endsAt, localeService);
        raid = raidRepository.changeEndOfRaid(tempRaid.getId(), endsAt);
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }

    private void verifyGroupPermission(CommandEvent commandEvent, User user, Raid raid, Config config) {
        final boolean isServerMod = isUserServerMod(commandEvent, config);
        final boolean userIsNotAdministrator = !isUserAdministrator(commandEvent) && !isServerMod;
        final boolean userHasNoGroupForRaid = !raidRepository.userHasGroupForRaid(user, raid);
        if (userIsNotAdministrator && userHasNoGroupForRaid) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_PERMISSION,
                    localeService.getLocaleForUser(user)));
        }
    }

}
