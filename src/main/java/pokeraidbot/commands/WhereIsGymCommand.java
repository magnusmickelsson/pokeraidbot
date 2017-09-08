package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import pokeraidbot.GymRepository;
import pokeraidbot.Utils;
import pokeraidbot.domain.Gym;

public class WhereIsGymCommand extends Command {
    private final GymRepository gymRepository;

    public WhereIsGymCommand(GymRepository gymRepository) {
        this.name = "map";
        this.help = "Get map link for gym - !raid map [Pokestop/Gym name].";
        this.gymRepository = gymRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            // todo: error handling
            String gymName = commandEvent.getArgs();
            final Gym gym = gymRepository.search(commandEvent.getAuthor().getName(), gymName);
            String staticUrl = Utils.getStaticMapUrl(gym);
            String nonStaticUrl = Utils.getNonStaticMapUrl(gym);
            commandEvent.reply(new EmbedBuilder().setImage(staticUrl).setTitle(gym.getName(), nonStaticUrl).build());
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }

}
