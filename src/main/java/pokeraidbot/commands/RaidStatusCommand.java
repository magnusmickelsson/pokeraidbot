package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.GymRepository;
import pokeraidbot.RaidRepository;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.SignUp;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.printTime;

/**
 * !raid status [Pokestop name]
 */
public class RaidStatusCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService) {
        this.localeService = localeService;
        this.name = "status";
        this.help = localeService.getMessageFor(LocaleService.RAIDSTATUS_HELP, LocaleService.DEFAULT);

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            String gymName = commandEvent.getArgs();
            final String userName = commandEvent.getAuthor().getName();
            final Gym gym = gymRepository.search(userName, gymName);
            final Raid raid = raidRepository.getRaid(gym);
            final Set<SignUp> signUps = raid.getSignUps();
            final int numberOfPeople = raid.getNumberOfPeopleSignedUp();

            final Locale localeForUser = localeService.getLocaleForUser(userName);
            commandEvent.reply("**" +
                    localeService.getMessageFor(LocaleService.RAIDSTATUS, localeForUser, gym.getName()) + "**\n" +
                    "Pokemon: " + raid.getPokemon() + "\n" +
                    localeService.getMessageFor(LocaleService.ENDS_AT, localeForUser, printTime(raid.getEndOfRaid())) + "\n" +
                    numberOfPeople + " " + localeService.getMessageFor(LocaleService.SIGNED_UP, localeForUser) + "." +
                    (signUps.size() > 0 ? "\n" + signUps : ""));
        } catch (Throwable e) {
            commandEvent.reply(e.getMessage());
        }
    }
}
