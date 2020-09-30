package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

/**
 * !raid map [gym name]
 */
public class WhereIsGymCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;

    public WhereIsGymCommand(GymRepository gymRepository, LocaleService localeService,
                             ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.name = "map";
        this.help = localeService.getMessageFor(LocaleService.WHERE_GYM_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.aliases = new String[]{"whereis", "find"};
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final Gym gym = gymRepository.search(commandEvent.getAuthor(), gymName, config.getRegion());
        String staticUrl = Utils.getStaticMapUrl(gym);
        String nonStaticUrl = Utils.getNonStaticMapUrl(gym);
        replyMapBasedOnConfig(config, commandEvent,
                new EmbedBuilder().setImage(staticUrl).setTitle(gym.getName(), nonStaticUrl).build());
    }
}
