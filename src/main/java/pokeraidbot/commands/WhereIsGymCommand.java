package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import pokeraidbot.GymRepository;
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
            String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + gym.getX() + "," + gym.getY() +
                    "&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C" +
                    gym.getX() + "," + gym.getY() + "&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU";
//            commandEvent.reply("Gym " + gym + ":");
            commandEvent.reply(new EmbedBuilder().setImage(url).setTitle(gym.getName()).build());
//            commandEvent.reply("https://maps.googleapis.com/maps/api/staticmap?center=" + gym.getX() + "," + gym.getY() + "&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C" + gym.getX() + "," + gym.getY() + "&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU");
//            commandEvent.reply("Gym can be found via Swepocks map, click this: http://uppsalagym.000webhostapp.com/?id=" + gym.getId());
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
