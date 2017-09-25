package pokeraidbot.jda;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.BotService;
import pokeraidbot.domain.*;

import java.util.concurrent.TimeUnit;

public class EmoticonMessageListener implements CommandListener {
    private final BotService botService;
    private final LocaleService localeService;
    private final ConfigRepository configRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final GymRepository gymRepository;

    public EmoticonMessageListener(BotService botService, LocaleService localeService, ConfigRepository configRepository,
                                   RaidRepository raidRepository, PokemonRepository pokemonRepository,
                                   GymRepository gymRepository) {
        this.botService = botService;

        this.localeService = localeService;
        this.configRepository = configRepository;
        this.raidRepository = raidRepository;
        this.pokemonRepository = pokemonRepository;
        this.gymRepository = gymRepository;
    }

    @Override
    public void onCommand(CommandEvent event, Command command) {

    }

    @Override
    public void onCompletedCommand(CommandEvent event, Command command) {
//        if (StringUtils.containsIgnoreCase(event.getMessage().getRawContent(), "!raid status")) {
////            EmbedBuilder builder = new EmbedBuilder((MessageEmbed) event.getMessage());
////            final MessageEmbed messageEmbed = builder.build();
////            messageEmbed.
////            event.getMessage().editMessage()
//            final MessageChannel channel = event.getMessage().getChannel();
//            RestAction<Void> restAction = event.getMessage().addReaction("\uD83D\uDE00");
//            restAction.queue();
//            restAction = event.getMessage().addReaction("➕");
//            restAction.queue();
//            restAction = event.getMessage().addReaction("➖");
//            restAction.queue();
//            restAction = event.getMessage().addReaction("\uD83D\uDEB7");
//            restAction.queue();
//        }
    }

    @Override
    public void onTerminatedCommand(CommandEvent event, Command command) {

    }

    @Override
    public void onNonCommandMessage(MessageReceivedEvent event) {

    }
}
