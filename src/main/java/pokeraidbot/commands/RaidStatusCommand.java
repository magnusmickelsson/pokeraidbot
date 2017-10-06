package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.printTimeIfSameDay;

/**
 * !raid status [Pokestop name]
 */
public class RaidStatusCommand extends ConfigAwareCommand {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(RaidStatusCommand.class);
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final BotService botService;
    private final PokemonRepository pokemonRepository;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                             ConfigRepository configRepository, BotService botService, CommandListener commandListener,
                             PokemonRepository pokemonRepository) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.botService = botService;
        this.pokemonRepository = pokemonRepository;
        this.name = "status";
        this.help = localeService.getMessageFor(LocaleService.RAIDSTATUS_HELP, LocaleService.DEFAULT);

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final String userName = commandEvent.getAuthor().getName();
        final Gym gym = gymRepository.search(userName, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());
        final Set<SignUp> signUps = raid.getSignUps();
        final int numberOfPeople = raid.getNumberOfPeopleSignedUp();

        final Locale localeForUser = localeService.getLocaleForUser(userName);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(null, null, null);
        final Pokemon pokemon = raid.getPokemon();
        embedBuilder.setImage("https://raw.githubusercontent.com/kvangent/PokeAlarm/master/icons/" + pokemon.getNumber() + ".png");
        embedBuilder.setTitle(localeService.getMessageFor(LocaleService.RAIDSTATUS, localeForUser, gym.getName()));
        StringBuilder sb = new StringBuilder();
        // todo: i18n
        sb.append("**Aktiv:** ")
                .append(printTimeIfSameDay(raid.getEndOfRaid().minusHours(1)))
                .append("-").append(printTimeIfSameDay(raid.getEndOfRaid()))
                .append("\t**").append(numberOfPeople).append(" ")
                .append(localeService.getMessageFor(LocaleService.SIGNED_UP, localeForUser)).append("**")
                .append(signUps.size() > 0 ? ":\n" + signUps : "")
                // todo: i18n
        .append("\nStarta grupp - skriv (med egen tid):\n!raid group ")
                .append(printTimeIfSameDay(raid.getEndOfRaid().minusMinutes(15))).append(" ").append(gymName)
                .append("\nHitta dit: [Google Maps](").append(Utils.getNonStaticMapUrl(gym)).append(")")
                .append("\nRaidboss: **").append(pokemon).append("**\n")
                .append("För tips - skriv: *!raid vs ").append(pokemon.getName()).append("*\n");
        embedBuilder.setDescription(sb.toString());
        final MessageEmbed messageEmbed = embedBuilder.build();

        commandEvent.reply(messageEmbed);
    }

    private String getNumberAndFillToThree(Pokemon pokemon) {
        String numberAsString = "" + pokemon.getNumber();
        if (pokemon.getNumber() < 10) {
            numberAsString = "00" + numberAsString;
        } else if (pokemon.getNumber() < 100) {
            numberAsString = "0" + numberAsString;
        }
        return numberAsString;
    }
}
