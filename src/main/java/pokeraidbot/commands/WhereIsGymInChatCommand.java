package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

public class WhereIsGymInChatCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final LocaleService localeService;

    public WhereIsGymInChatCommand(GymRepository gymRepository, LocaleService localeService,
                                   ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener, localeService);
        this.localeService = localeService;
        this.name = "mapinchat";
        this.help = localeService.getMessageFor(LocaleService.WHERE_GYM_IN_CHAT_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final Gym gym = gymRepository.search(commandEvent.getAuthor().getName(), gymName, config.getRegion());
        String staticUrl = Utils.getStaticMapUrl(gym);
        String nonStaticUrl = Utils.getNonStaticMapUrl(gym);
        commandEvent.reply(new EmbedBuilder().setImage(staticUrl).setTitle(gym.getName(), nonStaticUrl).build());
    }
}
