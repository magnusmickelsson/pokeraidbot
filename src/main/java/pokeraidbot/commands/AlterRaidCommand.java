package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import main.BotServerMain;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
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
import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.*;

/**
 * !raid change when [New time (HH:MM)] [Pokestop name] (Only administrators or raid creator)
 * !raid change pokemon [Pokemon] [Pokestop name] (Only administrators or raid creator)
 * !raid change remove [Pokestop name] (Only administrators)
 * !raid change group [new time] [Pokestop name] (Only administrators or raid group creator)
 * !raid change group [new time] [current time] [Pokestop name] (Only administrators or raid group creator)
 * !raid change group remove [group time] [Pokestop name] (Only administrators or raid group creator - if no one signed up)
 */
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
        this.aliases = new String[]{"c"};
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
                if (args.length < 2) {
                    throw new UserMessedUpException(userName,
                            localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user),
                                    "!raid change {when/pokemon/remove/group} {params}"));
                }
                changePokemon(gymRepository, localeService, pokemonRepository, raidRepository,
                        commandEvent, config, user, userName, args[1].trim().toLowerCase(),
                        ArrayUtils.removeAll(args, 0, 1));
                break;
            case "remove":
                deleteRaidAndGroups(commandEvent, config, user, userName, args);
                break;
            case "group":
                changeOrDeleteGroup(commandEvent, config, user, userName, args);
                break;
            default:
                throw new UserMessedUpException(userName,
                        localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user),
                                "!raid change {when/pokemon/remove/group} {params}"));
        }
    }

    private void changeOrDeleteGroup(CommandEvent commandEvent, Config config, User user, String userName,
                                     String[] args) {
        String whatToChangeTo;
        String gymName;
        Gym gym;
        Raid raid;
        whatToChangeTo = args[1].trim().toLowerCase();
        String originalTime = preProcessTimeString(args[2].trim());
        LocalTime existingGroupTimeIfAvailable = null;
        try {
            existingGroupTimeIfAvailable = Utils.parseTime(user, originalTime, localeService);
        } catch (UserMessedUpException e) {
            // Input was not a time
        }
        gymName = getGymName(args, existingGroupTimeIfAvailable);
        gym = gymRepository.search(user, gymName, config.getRegion());
        raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        if (whatToChangeTo.equals("remove")) {
            final Set<RaidGroup> groups = raidRepository.getGroups(raid);
            final Set<EmoticonSignUpMessageListener> listenersToCheck =
                    getListenersToCheck(commandEvent, config, user, raid, groups);
            EmoticonSignUpMessageListener listener = listenersToCheck.iterator().next();
            verifyIsModOrHasGroupForRaid(commandEvent, user, raid, config);
            RaidGroup theGroupToDelete = getGroupToDelete(user, existingGroupTimeIfAvailable, groups, raid);
            assertPermissionToManageThisGroup(user, theGroupToDelete, commandEvent, config);
            NewRaidGroupCommand.cleanUpGroupMessageAndEntity(commandEvent.getChannel(), raid.getId(),
                    listener, raidRepository, botService, theGroupToDelete.getId(), raid.toString());
            final String message = localeService.getMessageFor(LocaleService.GROUP_DELETED,
                    localeService.getLocaleForUser(user));
            replyBasedOnConfig(config, commandEvent, message);
        } else {
            LocalTime newTime = parseTime(user, whatToChangeTo, localeService);
            LocalDateTime newDateTime = LocalDateTime.of(raid.getEndOfRaid().toLocalDate(), newTime);

            checkIfInputIsValidAndUserHasRights(commandEvent, config, user, raid, newDateTime);

            if (changeGroupTime(commandEvent, config, user, userName, raid, newDateTime)) return;
        }
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
    }

    private String getGymName(String[] args, LocalTime existingGroupTimeIfAvailable) {
        StringBuilder gymNameBuilder;
        String gymName;
        int gymIndex = 2;
        if (existingGroupTimeIfAvailable != null) {
            gymIndex = 3;
        }
        gymNameBuilder = new StringBuilder();
        for (int i = gymIndex; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        gymName = gymNameBuilder.toString().trim();
        return gymName;
    }

    private RaidGroup getGroupToDelete(User user, LocalTime existingGroupTimeIfAvailable, Set<RaidGroup> groups,
                                       Raid raid) {
        RaidGroup theGroupToDelete = null;
        final Locale localeForUser = localeService.getLocaleForUser(user);
        if (groups.size() > 1 && (existingGroupTimeIfAvailable == null)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.MANY_GROUPS_FOR_RAID,
                    localeForUser, raid.toString(localeForUser)));
        } else if (groups.size() == 1) {
            theGroupToDelete = groups.iterator().next();
        } else {
            for (RaidGroup g : groups) {
                if (g.getStartsAt().toLocalTime().equals(existingGroupTimeIfAvailable)) {
                    theGroupToDelete = g;
                    break;
                }
            }
        }
        if (theGroupToDelete == null) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_SUCH_GROUP,
                    localeForUser));
        }
        return theGroupToDelete;
    }

    private boolean changeGroupTime(CommandEvent commandEvent, Config config, User user, String userName, Raid raid,
                                    LocalDateTime newDateTime) {
        boolean groupChanged = false;
        final Set<RaidGroup> groups = raidRepository.getGroups(raid);
        final Set<EmoticonSignUpMessageListener> listenersToCheck =
                getListenersToCheck(commandEvent, config, user, raid, groups);

        for (EmoticonSignUpMessageListener listener : listenersToCheck) {
            final String raidId = raid.getId();
            final LocalDateTime currentStartAt = listener.getStartAt();
            if (currentStartAt != null && currentStartAt.equals(newDateTime)) {
                LOGGER.info("Group is already at input time.");
                // This group is already at the time to change to
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
                LOGGER.info("Group is about to get cleaned up.");
                commandEvent.reactError();
                // This group is about to get cleaned up since its start time is null
                replyBasedOnConfigAndRemoveAfter(config, commandEvent,
                        localeService.getMessageFor(LocaleService.GROUP_CLEANING_UP,
                                localeService.getLocaleForUser(user)),
                        BotServerMain.timeToRemoveFeedbackInSeconds);
                return true;
            }
        }
        if (!groupChanged) {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user),
                            "!raid change group 10:00 solna platform"));
        }
        return false;
    }

    private Set<EmoticonSignUpMessageListener> getListenersToCheck(CommandEvent commandEvent, Config config,
                                                                   User user, Raid raid, Set<RaidGroup> raidGroups) {
        Set<EmoticonSignUpMessageListener> listenersToCheck = getListenersForUser(user, raid);

        // If admin doesn't have his/her own group, they can change group for others, as long as it's just one
        // to choose from. Will be fixed later on
        if (listenersToCheck.size() == 0) {
            listenersToCheck = getListenersAdminCanChange(commandEvent, config, raid);
        }

        assertOkListenerToChange(user, raid, raidGroups, listenersToCheck);
        return listenersToCheck;
    }

    private void assertOkListenerToChange(User user, Raid raid, Set<RaidGroup> raidGroups,
                                          Set<EmoticonSignUpMessageListener> listenersToCheck) {
        if (listenersToCheck.size() > 1) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.MANY_GROUPS_FOR_RAID,
                            localeService.getLocaleForUser(user), String.valueOf(raid)));
        } else if (listenersToCheck.size() == 0) {
            if (raidGroups != null && raidGroups.size() > 0) {
                throw new UserMessedUpException(user,
                        localeService.getMessageFor(LocaleService.NO_PERMISSION,
                                localeService.getLocaleForUser(user)));
            } else {
                throw new UserMessedUpException(user,
                        localeService.getMessageFor(LocaleService.NO_SUCH_GROUP,
                                localeService.getLocaleForUser(user)));
            }
        }
    }

    private Set<EmoticonSignUpMessageListener> getListenersAdminCanChange(CommandEvent commandEvent,
                                                                          Config config, Raid raid) {
        Set<EmoticonSignUpMessageListener> listenersToCheck = new HashSet<>();
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
        return listenersToCheck;
    }

    private Set<EmoticonSignUpMessageListener> getListenersForUser(User user, Raid raid) {
        Set<EmoticonSignUpMessageListener> listenersToCheck;
        listenersToCheck = new HashSet<>();
        for (Object o : botService.getBot().getRegisteredListeners()) {
            if (o instanceof EmoticonSignUpMessageListener) {
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
        return listenersToCheck;
    }

    private EmoticonSignUpMessageListener getListenerForGroup(Raid raid, RaidGroup raidGroup) {
        for (Object o : botService.getBot().getRegisteredListeners()) {
            if (o instanceof EmoticonSignUpMessageListener) {
                EmoticonSignUpMessageListener listener = (EmoticonSignUpMessageListener) o;
                final String raidId = raid.getId();
                final boolean isCorrectRaid = raidId.equals(listener.getRaidId());
                final boolean isCorrectGroup = raidGroup.getEmoteMessageId().equals(listener.getEmoteMessageId()) &&
                        raidGroup.getInfoMessageId().equals(listener.getInfoMessageId());
                if (isCorrectRaid && isCorrectGroup) {
                    return listener;
                }
            }
        }
        return null;
    }

    private void checkIfInputIsValidAndUserHasRights(CommandEvent commandEvent, Config config, User user, Raid raid,
                                                     LocalDateTime newDateTime) {
        verifyIsModOrHasGroupForRaid(commandEvent, user, raid, config);
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
    }

    private void deleteRaidAndGroups(CommandEvent commandEvent, Config config, User user, String userName, String[] args) {
        StringBuilder gymNameBuilder;
        String gymName;
        Gym gym;
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
        final Set<RaidGroup> groups = raidRepository.getGroups(deleteRaid);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Deleting " + groups.size() + " groups associated with raid " + deleteRaid + "... ");
        }
        final int numberOfGroups = groups.size();
        for (RaidGroup group : groups) {
            final EmoticonSignUpMessageListener listener = getListenerForGroup(deleteRaid, group);
            if (listener != null) {
                final MessageChannel channel = getChannel(commandEvent.getGuild(), group.getChannel());
                if (channel != null) {
                    NewRaidGroupCommand.cleanUpRaidGroupAndDeleteSignUpsIfPossible(channel,
                            group.getStartsAt(), deleteRaid.getId(),
                            listener, raidRepository, botService, group.getId());
                } else {
                    LOGGER.debug("Could not find channel " + group.getChannel() +
                            " in guild " + commandEvent.getGuild().getName());
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Groups deleted for raid " + deleteRaid);
        }
        if (raidRepository.delete(deleteRaid)) {
            commandEvent.reactSuccess();
            removeOriginMessageIfConfigSaysSo(config, commandEvent);
            LOGGER.info("Deleted raid (and " + numberOfGroups + " related groups): " + deleteRaid);
        } else {
            throw new UserMessedUpException(userName,
                    localeService.getMessageFor(LocaleService.RAID_NOT_EXISTS,
                            localeService.getLocaleForUser(user)));
        }
    }

    private MessageChannel getChannel(Guild guild, String channel) {
        for (MessageChannel c : guild.getTextChannelsByName(channel, true)) {
            return c;
        }
        return null;
    }

    // todo: move to service
    public static void changePokemon(GymRepository gymRepository, LocaleService localeService,
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
        // TODO: temporary since the EX pokemon right now is the same as tier 5
//        if (pokemonRaid.isExRaid()) {
//            throw new UserMessedUpException(userName, localeService.getMessageFor(
//                    LocaleService.EX_NO_CHANGE_POKEMON,
//                    localeService.getLocaleForUser(user)));
//        }
        // Anybody should be able to report hatched eggs
        if (!pokemonRaid.getPokemon().isEgg()) {
            verifyPermission(localeService, commandEvent, user, pokemonRaid, config);
        }
        // TODO: same as above
//        if (Utils.isRaidExPokemon(whatToChangeTo)) {
//            throw new UserMessedUpException(userName, localeService.getMessageFor(
//                    LocaleService.EX_CANT_CHANGE_RAID_TYPE, localeService.getLocaleForUser(user)));
//        }
        raid = raidRepository.changePokemon(pokemonRaid, pokemon, commandEvent.getGuild(), config, user,
                "!raid change pokemon " + pokemon.getName() + " " + gymName);
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
        LOGGER.info("Changed pokemon for raid: " + raid);
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
        raid = raidRepository.changeEndOfRaid(tempRaid.getId(), endsAt, commandEvent.getGuild(), config, user,
                "!raid change when " + printTimeIfSameDay(endsAt) + " " + gym.getName());
        commandEvent.reactSuccess();
        removeOriginMessageIfConfigSaysSo(config, commandEvent);
        LOGGER.info("Changed time for raid: " + raid);
    }

    private void verifyIsModOrHasGroupForRaid(CommandEvent commandEvent, User user, Raid raid,
                                              Config config) {
        final boolean isServerMod = isUserServerMod(commandEvent, config);
        final boolean userIsNotAdministrator = !isUserAdministrator(commandEvent) && !isServerMod;
        final boolean userHasNoGroupForRaid = !raidRepository.userHasGroupForRaid(user, raid);
        if (userIsNotAdministrator && userHasNoGroupForRaid) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_PERMISSION,
                    localeService.getLocaleForUser(user)));
        }
    }

    private void assertPermissionToManageThisGroup(User user, RaidGroup raidGroup,
                                                   CommandEvent commandEvent, Config config) {
        if (!isUserServerMod(commandEvent, config) && !raidGroup.getCreatorId().equals(user.getId())) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_PERMISSION,
                    localeService.getLocaleForUser(user)));
        }
    }
}
