package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.GymRepository;
import pokeraidbot.domain.RaidRepository;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.LocaleService;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.SignUp;

import java.util.Locale;

public class RemoveSignUpCommand extends Command {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public RemoveSignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService) {
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.name = "remove";
        this.help = localeService.getMessageFor(LocaleService.REMOVE_SIGNUP_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            final String user = commandEvent.getAuthor().getName();
            final Locale localeForUser = localeService.getLocaleForUser(user);
            String gymName = commandEvent.getArgs();
            final Gym gym = gymRepository.search(user, gymName);
            final Raid raid = raidRepository.getRaid(gym);
            final SignUp removed = raid.remove(user, raidRepository);
            if (removed != null) {
                commandEvent.reply(localeService.getMessageFor(LocaleService.SIGNUP_REMOVED, localeForUser,
                        gym.getName(), removed.toString()));
            } else {
                commandEvent.reply(localeService.getMessageFor(LocaleService.NO_SIGNUP_AT_GYM, localeForUser, user, gym.getName()));
            }
        } catch (RuntimeException e) {
            commandEvent.reply(e.getMessage());
        }
    }
}
