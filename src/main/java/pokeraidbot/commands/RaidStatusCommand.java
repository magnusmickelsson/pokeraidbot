package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import net.dv8tion.jda.core.entities.impl.MessageImpl;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.*;
import pokeraidbot.jda.Control;
import pokeraidbot.jda.EmoticonMessageListener;
import pokeraidbot.jda.Menu;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static pokeraidbot.Utils.printTime;

/**
 * !raid status [Pokestop name]
 */
public class RaidStatusCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final BotService botService;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                             ConfigRepository configRepository, BotService botService, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.botService = botService;
        this.name = "status";
        this.help = localeService.getMessageFor(LocaleService.RAIDSTATUS_HELP, LocaleService.DEFAULT);

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final String userName = commandEvent.getAuthor().getName();
        final Gym gym = gymRepository.search(userName, gymName, config.region);
        final Raid raid = raidRepository.getRaid(gym, config.region);
        final Set<SignUp> signUps = raid.getSignUps();
        final int numberOfPeople = raid.getNumberOfPeopleSignedUp();

        final Locale localeForUser = localeService.getLocaleForUser(userName);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(localeService.getMessageFor(LocaleService.RAIDSTATUS, localeForUser, gym.getName()));
        StringBuilder sb = new StringBuilder();
        sb.append("Pokemon: ").append(raid.getPokemon()).append("\n")
                .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, localeForUser,
                        printTime(raid.getEndOfRaid().minusHours(1)), printTime(raid.getEndOfRaid())))
                .append("\n").append(numberOfPeople).append(" ")
                .append(localeService.getMessageFor(LocaleService.SIGNED_UP, localeForUser)).append(".")
                .append(signUps.size() > 0 ? "\n" + signUps : "")
                .append("\n[Google Maps](").append(Utils.getNonStaticMapUrl(gym)).append(")");
        embedBuilder.setDescription(sb.toString());
        final MessageEmbed messageEmbed = embedBuilder.build();

        commandEvent.reply(messageEmbed);
        // todo: Link emoticons to actions against the bot
        // todo: locale service
        commandEvent.reply("Anmäl dig via knapparna nedan. För hjälp, skriv \"!raid help-signup\".", message -> {
            message.getChannel().addReactionById(message.getId(), "\uD83D\uDE00").queue();
            message.getChannel().addReactionById(message.getId(), "➕").queue();
            message.getChannel().addReactionById(message.getId(), "➖").queue();
            message.getChannel().addReactionById(message.getId(), "\uD83D\uDEB7").queue();
        });
    }
}
