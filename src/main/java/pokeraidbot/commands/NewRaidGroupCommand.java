package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.Emotes;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static pokeraidbot.Utils.*;

/**
 * !raid group [start raid at (HH:MM)] [Pokestop name]
 */
public class NewRaidGroupCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final PokemonRepository pokemonRepository;
    private final LocaleService localeService;
    private final BotService botService;

    public NewRaidGroupCommand(GymRepository gymRepository, RaidRepository raidRepository,
                               PokemonRepository pokemonRepository, LocaleService localeService,
                               ConfigRepository configRepository,
                               CommandListener commandListener, BotService botService) {
        super(configRepository, commandListener);
        this.pokemonRepository = pokemonRepository;
        this.localeService = localeService;
        this.botService = botService;
        this.name = "group";
        this.help = " Skapa ett tillfälle för en grupp att köra vid en skapad raid: !raid group [start time (HH:MM)] [gym name]";
                //localeService.getMessageFor(LocaleService.NEW_RAID_HELP, LocaleService.DEFAULT);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final List<Emote> mystic = commandEvent.getGuild().getEmotesByName("mystic", true);
        final List<Emote> instinct = commandEvent.getGuild().getEmotesByName("instinct", true);
        final List<Emote> valor = commandEvent.getGuild().getEmotesByName("valor", true);
        assertAtLeastOneEmote(mystic);
        assertAtLeastOneEmote(instinct);
        assertAtLeastOneEmote(valor);

        final String userName = commandEvent.getAuthor().getName();
        final String[] args = commandEvent.getArgs().split(" ");
        String timeString = args[0];
        LocalTime startAtTime = Utils.parseTime(userName, timeString);
        LocalDateTime startAt = LocalDateTime.of(LocalDate.now(), startAtTime);

        assertTimeNotInNoRaidTimespan(userName, startAtTime, localeService);
        assertTimeNotMoreThanXHoursFromNow(userName, startAtTime, localeService, 2);
        assertCreateRaidTimeNotBeforeNow(userName, startAt, localeService);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(userName, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());
        if (!startAt.isBefore(raid.getEndOfRaid())) {
            // todo: i18n
            throw new UserMessedUpException(userName, "Can't create a group to raid after raid has ended. :(");
        }
        final Pokemon pokemon = raid.getPokemon();

//        replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.NEW_RAID_CREATED,
//                localeService.getLocaleForUser(userName), raid.toString()));

        // todo: Link emoticons to actions against the bot
        // todo: locale service
        // todo: i18n
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(userName + "'s group @ " + gym.getName() + ", starting at " +
                Utils.printTimeIfSameDay(startAt));
        embedBuilder.setAuthor(null, null, null);
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("**Raid boss:** ").append(pokemon).append(".");
        descriptionBuilder.append("\nTotal signups (incl. this group): ")
                .append(raid.getNumberOfPeopleSignedUp());
        descriptionBuilder.append("\nSignups for this group: ")
                .append("todo: sum(emotes)"); // todo: sum(emotes), uppdatera text
        descriptionBuilder.append("\nFor boss counter info, type:" +
                "\n!raid vs ").append(pokemon.getName()).append("\n");
        descriptionBuilder.append("\nHow to get here: [Google Maps](").append(Utils.getNonStaticMapUrl(gym))
                .append(")");
        embedBuilder.setDescription(descriptionBuilder.toString());
        commandEvent.reply(embedBuilder.build());
        commandEvent.reply("Hantera anmälning via knapparna nedan. För hjälp, skriv \"!raid how group\".", message -> {
            // todo: start listener for the signups
            message.getChannel().addReactionById(message.getId(), mystic.iterator().next()).queue();
            message.getChannel().addReactionById(message.getId(), valor.iterator().next()).queue();
            message.getChannel().addReactionById(message.getId(), instinct.iterator().next()).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.ONE).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.TWO).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.THREE).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.FOUR).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.FIVE).queue();
            message.getChannel().addReactionById(message.getId(), Emotes.SIX).queue();
        });
    }

    private void assertAtLeastOneEmote(List<Emote> mystic) {
        if (mystic == null || mystic.size() < 1) {
            // todo: i18n
            throw new RuntimeException("Administrator has not installed pokeraidbot's emotes. " +
                    "Ensure he/her runs the following command: !raid install-emotes");
        }
    }
}
