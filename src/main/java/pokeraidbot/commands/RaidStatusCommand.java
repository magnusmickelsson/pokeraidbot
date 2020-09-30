package pokeraidbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.emote.Emotes;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.*;

/**
 * !raid status [Pokestop name]
 */
public class RaidStatusCommand extends ConfigAwareCommand {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(RaidStatusCommand.class);
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                             ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.name = "status";
        this.aliases = new String[]{"stat"};
        this.help = localeService.getMessageFor(LocaleService.RAIDSTATUS_HELP, LocaleService.DEFAULT);

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final User user = commandEvent.getAuthor();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        final Set<SignUp> signUps = raid.getSignUps();
        final int numberOfPeople = raid.getNumberOfPeopleSignedUp();

        final Locale localeForUser = localeService.getLocaleForUser(user);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(null, null, null);
        final Pokemon pokemon = raid.getPokemon();
        embedBuilder.setTitle(localeService.getMessageFor(LocaleService.RAIDSTATUS, localeForUser,
                gym.getName() + (gym.isExGym() ? Emotes.STAR + "" : "")),
                Utils.getNonStaticMapUrl(gym));
        StringBuilder sb = new StringBuilder();
        final String activeText = localeService.getMessageFor(LocaleService.ACTIVE, localeForUser);
        final String startGroupText = localeService.getMessageFor(LocaleService.START_GROUP, localeForUser);
        final String findYourWayText = localeService.getMessageFor(LocaleService.FIND_YOUR_WAY, localeForUser);
        final String raidBossText = localeService.getMessageFor(LocaleService.RAID_BOSS, localeForUser);
        final Set<String> signUpNames = getNamesOfThoseWithSignUps(raid.getSignUps(), true);
        final String allSignUpNames = StringUtils.join(signUpNames, ", ");
        sb.append(raidBossText).append(" **").append(pokemon).append("** - ")
                .append("*!raid vs ").append(pokemon.getName()).append("*\n");

        sb.append("**").append(activeText).append(":** ")
                .append(printTimeIfSameDay(getStartOfRaid(raid.getEndOfRaid(), false)))
                .append("-").append(printTimeIfSameDay(raid.getEndOfRaid()))
                .append("\t**").append(numberOfPeople).append(" ")
                .append(localeService.getMessageFor(LocaleService.SIGNED_UP, localeForUser)).append("**")
                .append(signUps.size() > 0 ? ":\n" + allSignUpNames : "").append("\n").append(startGroupText)
                .append(":\n*!raid group ")
                .append(printTime(raid.getEndOfRaid().toLocalTime().minusMinutes(15))).append(" ")
                .append(gymName).append("*\n\n");
        sb.append(localeService.getMessageFor(LocaleService.CREATED_BY, localeForUser)).append(": ")
                .append(raid.getCreator());
        embedBuilder.setFooter(findYourWayText + localeService.getMessageFor(LocaleService.GOOGLE_MAPS,
                localeService.getLocaleForUser(user)),
                Utils.getPokemonIcon(pokemon));
        embedBuilder.setDescription(sb.toString());
        final MessageEmbed messageEmbed = embedBuilder.build();

        replyBasedOnConfig(config, commandEvent, messageEmbed);
    }
}
