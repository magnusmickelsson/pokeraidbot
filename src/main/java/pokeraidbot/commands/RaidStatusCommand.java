package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.config.Config;
import pokeraidbot.domain.config.ConfigRepository;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.Emotes;
import pokeraidbot.domain.raid.signup.SignUp;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.printTimeIfSameDay;

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
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.region);
        final Set<SignUp> signUps = raid.getSignUps();
        final int numberOfPeople = raid.getNumberOfPeopleSignedUp();

        final Locale localeForUser = localeService.getLocaleForUser(userName);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(localeService.getMessageFor(LocaleService.RAIDSTATUS, localeForUser, gym.getName()));
        StringBuilder sb = new StringBuilder();
        sb.append("Pokemon: ").append(raid.getPokemon()).append("\n")
                .append(localeService.getMessageFor(LocaleService.RAID_BETWEEN, localeForUser,
                        printTimeIfSameDay(raid.getEndOfRaid().minusHours(1)), printTimeIfSameDay(raid.getEndOfRaid())))
                .append("\n").append(numberOfPeople).append(" ")
                .append(localeService.getMessageFor(LocaleService.SIGNED_UP, localeForUser)).append(".")
                .append(signUps.size() > 0 ? "\n" + signUps : "")
                .append("\n[Google Maps](").append(Utils.getNonStaticMapUrl(gym)).append(")");
        embedBuilder.setDescription(sb.toString());
        final MessageEmbed messageEmbed = embedBuilder.build();

        commandEvent.reply(messageEmbed);
        // todo: Link emoticons to actions against the bot
        // todo: locale service
        commandEvent.reply("Hantera anmälning via knapparna nedan. För hjälp, skriv \"!raid help-signup\".", message -> {
            message.getChannel().addReactionById(message.getId(), Emotes.SIGN_UP_NOW_EMOTE).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.SIGNUP_HELP_EMOTE).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.UNSIGN_EMOTE).queue();
        });
    }
}
