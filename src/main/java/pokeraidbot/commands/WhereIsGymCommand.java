package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import pokeraidbot.domain.*;
import pokeraidbot.Utils;

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
        try {
            String gymName = commandEvent.getArgs();
            final Gym gym = gymRepository.search(commandEvent.getAuthor().getName(), gymName, config.region);
            String staticUrl = Utils.getStaticMapUrl(gym);
            String nonStaticUrl = Utils.getNonStaticMapUrl(gym);
            commandEvent.replyInDM(new EmbedBuilder().setImage(staticUrl).setTitle(gym.getName(), nonStaticUrl).build());
            commandEvent.reactSuccess();
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
