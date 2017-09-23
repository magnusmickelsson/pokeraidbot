package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.*;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.printTime;

/**
 * !raid status [Pokestop name]
 */
public class RaidStatusCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                             ConfigRepository configRepository) {
        super(configRepository);
        this.localeService = localeService;
        this.name = "status";
        this.help = localeService.getMessageFor(LocaleService.RAIDSTATUS_HELP, LocaleService.DEFAULT);

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final String userName = commandEvent.getAuthor().getName();
        final Gym gym = gymRepository.search(userName, gymName, config.region);
        final Raid raid = raidRepository.getRaid(gym, config.region);
        final Set<SignUp> signUps = raid.getSignUps();
        final int numberOfPeople = raid.getNumberOfPeopleSignedUp();

        final Locale localeForUser = localeService.getLocaleForUser(userName);
        commandEvent.reply("**" +
                localeService.getMessageFor(LocaleService.RAIDSTATUS, localeForUser, gym.getName()) + "**\n" +
                "Pokemon: " + raid.getPokemon() + "\n" +
                localeService.getMessageFor(LocaleService.ENDS_AT, localeForUser, printTime(raid.getEndOfRaid())) + "\n" +
                numberOfPeople + " " + localeService.getMessageFor(LocaleService.SIGNED_UP, localeForUser) + "." +
                (signUps.size() > 0 ? "\n" + signUps : ""));
    }
}
