package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static pokeraidbot.Utils.*;

/**
 * !raid change when [New time (HH:MM)] [Pokestop name] (Only administrators or raid creator)
 * !raid change pokemon [Pokemon] [Pokestop name] (Only administrators or raid creator)
 * !raid change remove [Pokestop name] (Only administrators)
 */
public class AlterRaidCommand extends ConfigAwareCommand {
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
        String whatToChangeTo;
        StringBuilder gymNameBuilder;
        String gymName;
        Gym gym;
        Raid raid;
        LocalTime endsAtTime;
        LocalDateTime endsAt;

        switch (whatToChange) {
            case "when":
                whatToChangeTo = args[1].trim().toLowerCase();
                gymNameBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    gymNameBuilder.append(args[i]).append(" ");
                }
                gymName = gymNameBuilder.toString().trim();
                gym = gymRepository.search(user, gymName, config.getRegion());
                raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
                verifyPermission(commandEvent, user, raid);
                endsAtTime = parseTime(user, whatToChangeTo, localeService);
                endsAt = LocalDateTime.of(LocalDate.now(), endsAtTime);

                assertTimeNotInNoRaidTimespan(user, endsAtTime, localeService);
                assertTimeNotMoreThanXHoursFromNow(user, endsAtTime, localeService, 2);
                assertCreateRaidTimeNotBeforeNow(user, endsAt, localeService);
                raid = raidRepository.changeEndOfRaid(raid, endsAt);
                break;
            case "pokemon":
                whatToChangeTo = args[1].trim().toLowerCase();
                gymNameBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    gymNameBuilder.append(args[i]).append(" ");
                }
                gymName = gymNameBuilder.toString().trim();
                gym = gymRepository.search(user, gymName, config.getRegion());
                raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
                final Pokemon pokemon = pokemonRepository.search(whatToChangeTo, user);
                if (Utils.isRaidExPokemon(raid.getPokemon().getName())) {
                    throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.EX_NO_CHANGE_POKEMON,
                            localeService.getLocaleForUser(user)));
                }
                verifyPermission(commandEvent, user, raid);
                if (pokemon.getName().equalsIgnoreCase("mewtwo")) {
                    throw new UserMessedUpException(userName, localeService.getMessageFor(
                            LocaleService.EX_CANT_CHANGE_RAID_TYPE, localeService.getLocaleForUser(user)));
                }
                raid = raidRepository.changePokemon(raid, pokemon);
                break;
            case "remove":
                gymNameBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    gymNameBuilder.append(args[i]).append(" ");
                }
                gymName = gymNameBuilder.toString().trim();
                gym = gymRepository.search(user, gymName, config.getRegion());
                raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
                verifyPermission(commandEvent, user, raid);
                final boolean userIsNotAdministrator = !isUserAdministrator(commandEvent);
                if (userIsNotAdministrator && raid.getSignUps().size() > 0) {
                    throw new UserMessedUpException(userName,
                            localeService.getMessageFor(LocaleService.ONLY_ADMINS_REMOVE_RAID,
                                    localeService.getLocaleForUser(user))
                    );
                }
                if (raidRepository.delete(raid)) {
                    raid = null;
                } else {
                    throw new UserMessedUpException(userName,
                            localeService.getMessageFor(LocaleService.RAID_NOT_EXISTS,
                                    localeService.getLocaleForUser(user)));
                }
                break;
            case "group":
                whatToChangeTo = args[1].trim().toLowerCase();
                gymNameBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    gymNameBuilder.append(args[i]).append(" ");
                }
                gymName = gymNameBuilder.toString().trim();
                gym = gymRepository.search(user, gymName, config.getRegion());
                raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
                verifyPermission(commandEvent, user, raid);
                LocalTime newTime = parseTime(user, whatToChangeTo, localeService);
                LocalDateTime newDateTime = LocalDateTime.of(LocalDate.now(), newTime);

                assertTimeNotInNoRaidTimespan(user, newTime, localeService);
                assertTimeNotMoreThanXHoursFromNow(user, newTime, localeService, 2);
                assertCreateRaidTimeNotBeforeNow(user, newDateTime, localeService);
                boolean groupChanged = false;
                for (Object o : botService.getBot().getRegisteredListeners()) {
                    if (o instanceof EmoticonSignUpMessageListener) {
                        EmoticonSignUpMessageListener listener = (EmoticonSignUpMessageListener) o;
                        final String raidId = raid.getId();
                        final boolean isCorrectRaid = raidId.equals(listener.getRaidId());
                        final boolean isUsersGroup = user.getId().equals(listener.getUserId());
                        if (isCorrectRaid && (isUsersGroup || isUserAdministrator(commandEvent))) {
                            final LocalDateTime currentStartAt = listener.getStartAt();
                            raidRepository.moveAllSignUpsForTimeToNewTime(raid, currentStartAt, newDateTime, user);
                            listener.setStartAt(newDateTime);
                            groupChanged = true;
                            replyBasedOnConfigAndRemoveAfter(config, commandEvent,
                                    localeService.getMessageFor(LocaleService.MOVED_GROUP,
                                            localeService.getLocaleForUser(user),
                                            printTimeIfSameDay(currentStartAt),
                                            printTimeIfSameDay(newDateTime), raid.getGym().getName()),
                                    30);
                        }
                    }
                }
                if (!groupChanged) {
                    throw new UserMessedUpException(userName,
                            localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user)));
                }
                break;
            default:
                throw new UserMessedUpException(userName,
                        localeService.getMessageFor(LocaleService.BAD_SYNTAX, localeService.getLocaleForUser(user)));
        }
        commandEvent.reactSuccess();
    }

    private void verifyPermission(CommandEvent commandEvent, User user, Raid raid) {
        final boolean userIsNotAdministrator = !isUserAdministrator(commandEvent);
        final boolean userIsNotRaidCreator = !user.getName().equalsIgnoreCase(raid.getCreator());
        if (userIsNotAdministrator && userIsNotRaidCreator) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_PERMISSION,
                    localeService.getLocaleForUser(user)));
        }
    }

    private boolean isUserAdministrator(CommandEvent commandEvent) {
        return PermissionUtil.checkPermission(commandEvent.getTextChannel(),
                commandEvent.getMember(), Permission.ADMINISTRATOR);
    }
}
