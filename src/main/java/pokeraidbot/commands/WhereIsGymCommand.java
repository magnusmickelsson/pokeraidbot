package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
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
            final String[] args = commandEvent.getArgs().split(" ");
            // todo: error handling
            String gymName = args[0];
            final Gym gym = gymRepository.findByName(gymName);
            commandEvent.reply("Gym " + gym + ":");
            commandEvent.reply("https://maps.googleapis.com/maps/api/staticmap?center=" + gym.getX() + "," + gym.getY() + "&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C" + gym.getX() + "," + gym.getY() + "&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU");
//            commandEvent.reply("Gym can be found via Swepocks map, click this: http://uppsalagym.000webhostapp.com/?id=" + gym.getId());
        } catch (Throwable t) {
            commandEvent.reply(t.getMessage());
        }
    }
}
