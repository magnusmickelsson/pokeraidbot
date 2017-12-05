package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class WhereIsGymInChatCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final LocaleService localeService;

    public WhereIsGymInChatCommand(GymRepository gymRepository, LocaleService localeService,
                                   ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.name = "mapinchat";
        this.aliases = new String[]{"m"};
        this.help = localeService.getMessageFor(LocaleService.WHERE_GYM_IN_CHAT_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final Gym gym = gymRepository.search(commandEvent.getAuthor(), gymName, config.getRegion());
        String staticUrl = Utils.getStaticMapUrl(gym);
        String nonStaticUrl = Utils.getNonStaticMapUrl(gym);
        final MessageEmbed messageEmbed = new EmbedBuilder().setImage(staticUrl).setTitle(gym.getName(), nonStaticUrl)
                .build();
        replyMapBasedOnConfig(config, commandEvent, messageEmbed);
    }
}
