package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import pokeraidbot.Utils;
import pokeraidbot.domain.*;

public class WhereIsGymCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final LocaleService localeService;

    public WhereIsGymCommand(GymRepository gymRepository, LocaleService localeService,
                             ConfigRepository configRepository) {
        super(configRepository);
        this.localeService = localeService;
        this.name = "map";
        this.help = localeService.getMessageFor(LocaleService.WHERE_GYM_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final Gym gym = gymRepository.search(commandEvent.getAuthor().getName(), gymName, config.region);
        String staticUrl = Utils.getStaticMapUrl(gym);
        String nonStaticUrl = Utils.getNonStaticMapUrl(gym);
        replyBasedOnConfig(config, commandEvent,
                new EmbedBuilder().setImage(staticUrl).setTitle(gym.getName(), nonStaticUrl).build());
    }
}
