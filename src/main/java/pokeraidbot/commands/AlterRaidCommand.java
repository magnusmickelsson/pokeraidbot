package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.PermissionUtil;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

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

    public AlterRaidCommand(GymRepository gymRepository, RaidRepository raidRepository,
                            PokemonRepository pokemonRepository, LocaleService localeService,
                            ConfigRepository configRepository,
                            CommandListener commandListener) {
        super(configRepository, commandListener);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.name = "change";
        // todo: i18n
        this.help = " Ändra något som blev fel vid skapandet av en raid. Skriv \"!raid man change\" för detaljer.";
        //localeService.getMessageFor(LocaleService.NEW_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String userName = commandEvent.getAuthor().getName();
        final String[] args = commandEvent.getArgs().split(" ");
        String whatToChange = args[0].trim().toLowerCase();
        String whatToChangeTo;
        Gym gym;
        Raid raid;
        switch (whatToChange) {
            case "when":
                whatToChangeTo = args[1].trim().toLowerCase();
                StringBuilder gymNameBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    gymNameBuilder.append(args[i]).append(" ");
                }
                String gymName = gymNameBuilder.toString().trim();
                gym = gymRepository.search(userName, gymName, config.getRegion());
                raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());
                verifyPermission(commandEvent, userName, raid);
                LocalTime endsAtTime = parseTime(userName, whatToChangeTo);
                LocalDateTime endsAt = LocalDateTime.of(LocalDate.now(), endsAtTime);

                assertTimeNotInNoRaidTimespan(userName, endsAtTime, localeService);
                assertTimeNotMoreThanXHoursFromNow(userName, endsAtTime, localeService, 2);
                assertCreateRaidTimeNotBeforeNow(userName, endsAt, localeService);
                raid = raidRepository.changeEndOfRaid(raid, endsAt);
                break;
            case "pokemon":
                whatToChangeTo = args[1].trim().toLowerCase();
                gymNameBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    gymNameBuilder.append(args[i]).append(" ");
                }
                gymName = gymNameBuilder.toString().trim();
                gym = gymRepository.search(userName, gymName, config.getRegion());
                raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());
                if (Utils.isRaidExPokemon(raid.getPokemon().getName())) {
                    throw new UserMessedUpException(userName, "Kan inte ändra pokemon för en EX raid. " +
                            "Om du vill ändra EX raiden, ta bort den och skapa en ny. Använd !raid usage");
                }
                verifyPermission(commandEvent, userName, raid);
                final Pokemon pokemon = pokemonRepository.getByName(whatToChangeTo);
                if (pokemon.getName().equalsIgnoreCase("mewtwo")) {
                    // i18n
                    throw new UserMessedUpException(userName, "Kan inte ändra en vanlig raid till att bli en EX raid. " +
                            "Ta bort den vanliga raiden och skapa en ny EX raid. Använd !raid usage");
//                    throw new UserMessedUpException(userName, "Can't change a standard raid to be an EX raid. " +
//                            "Remove the standard raid and then create an EX raid instead. Refer to !raid usage");
                }
                raid = raidRepository.changePokemon(raid, pokemon);
                break;
            case "remove":
                gymNameBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    gymNameBuilder.append(args[i]).append(" ");
                }
                gymName = gymNameBuilder.toString().trim();
                gym = gymRepository.search(userName, gymName, config.getRegion());
                raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());
                verifyPermission(commandEvent, userName, raid);
                final boolean userIsNotAdministrator = !PermissionUtil.checkPermission(commandEvent.getTextChannel(),
                        commandEvent.getMember(), Permission.ADMINISTRATOR);
                if (userIsNotAdministrator) {
                    // todo: i18n
                    throw new UserMessedUpException(userName, "Bara administratörer kan ta bort raids, tyvärr."); //"Only administrators can delete raids, sorry.");
                }
                // todo: got "Zhorhn: Kunde inte hitta ett unikt gym/pokestop, din sökning returnerade mer än 5 resultat. Försök vara mer precis."
                // while trying to delete. Check what that was.
                if (raidRepository.delete(raid)) {
                    raid = null;
                } else {
                    throw new UserMessedUpException(userName,
                            // todo: i18n
                            "Kunde inte ta bort raid, eftersom den fanns inte.");
//                            "Could not delete raid since you tried to delete one that doesn't exist.");
                }
                break;
            default:
                // todo: i18n
                throw new UserMessedUpException(userName, "Dålig syntax för kommandot. Se !raid usage");//"Bad syntax of command. Refer to command help: !raid help");
        }
        // todo: i18n
        if (raid != null) {
            replyBasedOnConfig(config, commandEvent, "Korrigerade raid: " + raid.toString());
        } else {
            replyBasedOnConfig(config, commandEvent, "Tog bort raid.");
        }
        //localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
//                localeService.getLocaleForUser(userName), raid.toString()));
    }

    private void verifyPermission(CommandEvent commandEvent, String userName, Raid raid) {
        final boolean userIsNotAdministrator = !PermissionUtil.checkPermission(commandEvent.getTextChannel(),
                commandEvent.getMember(), Permission.ADMINISTRATOR);
        final boolean userIsNotRaidCreator = !userName.equalsIgnoreCase(raid.getCreator());
        if (userIsNotAdministrator && userIsNotRaidCreator) {
            // todo: i18n
            throw new UserMessedUpException(userName, "Du är inte skapare av denna raid, eller en administratör. " +
                    "Du får inte göra det du försökte göra. :p"); //"You are not the creator of this raid, nor an administrator!");
        }
    }
}
