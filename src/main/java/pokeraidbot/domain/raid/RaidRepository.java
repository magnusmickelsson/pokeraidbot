package pokeraidbot.domain.raid;

import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.RaidExistsException;
import pokeraidbot.domain.errors.RaidNotFoundException;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.errors.WrongNumberOfArgumentsException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.raid.RaidEntity;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntitySignUp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static pokeraidbot.Utils.*;

@Transactional
public class RaidRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaidRepository.class);

    private ClockService clockService;
    private LocaleService localeService;
    private RaidEntityRepository raidEntityRepository;
    private PokemonRepository pokemonRepository;
    private GymRepository gymRepository;

    // Byte code instrumentation
    protected RaidRepository() {
    }

    @Autowired
    public RaidRepository(ClockService clockService, LocaleService localeService,
                          RaidEntityRepository raidEntityRepository, PokemonRepository pokemonRepository,
                          GymRepository gymRepository) {
        this.clockService = clockService;
        this.localeService = localeService;
        this.raidEntityRepository = raidEntityRepository;
        this.pokemonRepository = pokemonRepository;
        this.gymRepository = gymRepository;
        removeExpiredRaids();
    }

    public String executeSignUpCommand(Config config,
                                     User user,
                                     Locale localeForUser,
                                     String[] args,
                                     String help) {
        String people = args[0];
        String userName = user.getName();
        if (args.length < 3 || args.length > 10) {
            throw new WrongNumberOfArgumentsException(user, localeService, 3, args.length, help);
        }
        Integer numberOfPeople = Utils.assertNotTooManyOrNoNumber(user, localeService, people);

        String timeString = args[1];

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);

        LocalTime eta = Utils.parseTime(user, timeString, localeService);
        LocalDateTime realEta = LocalDateTime.of(raid.getEndOfRaid().toLocalDate(), eta);

        assertEtaNotAfterRaidEnd(user, raid, realEta, localeService);
        assertSignupTimeNotBeforeNow(user, realEta, localeService);

        raid.signUp(user, numberOfPeople, eta, this);
        final String currentSignupText = localeService.getMessageFor(LocaleService.CURRENT_SIGNUPS, localeForUser);
        final Set<SignUp> signUps = raid.getSignUps();
        Set<String> signUpNames = Utils.getNamesOfThoseWithSignUps(signUps, true);
        final String allSignUpNames = StringUtils.join(signUpNames, ", ");
        final String signUpText = raid.getSignUps().size() > 1 ? currentSignupText + "\n" + allSignUpNames : "";
        return localeService.getMessageFor(LocaleService.SIGNUPS, localeForUser, userName,
                gym.getName(), signUpText);
    }

    public void newRaid(String raidCreatorName, Raid raid) {
        RaidEntity raidEntity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());

        final String pokemonName = raid.getPokemon().getName();

        if (raidEntity != null) {
            final String existingEntityPokemon = raidEntity.getPokemon();
            final boolean oneRaidIsEx = Utils.isRaidExPokemon(pokemonName) || Utils.isRaidExPokemon(existingEntityPokemon);
            if ((!oneRaidIsEx) || Utils.raidsCollide(raid.getEndOfRaid(), raidEntity.getEndOfRaid())) {
                throw new RaidExistsException(raidCreatorName, getRaidInstance(raidEntity),
                        localeService, LocaleService.DEFAULT);
            }
        }

        saveRaid(raidCreatorName, raid);
    }

    private void saveRaid(String raidCreatorName, Raid raid) {
        final RaidEntity toBeSaved = new RaidEntity(UUID.randomUUID().toString(),
                raid.getPokemon().getName(),
                raid.getEndOfRaid(),
                raid.getGym().getName(),
                raidCreatorName,
                raid.getRegion());
        raidEntityRepository.save(toBeSaved);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created raid: " + toBeSaved);
        }
    }

    private Raid getRaidInstance(RaidEntity raidEntity) {
        Validate.notNull(raidEntity);
        final String region = raidEntity.getRegion();
        final Raid raid = new Raid(pokemonRepository.getPokemon(raidEntity.getPokemon()),
                raidEntity.getEndOfRaid(),
                gymRepository.findByName(raidEntity.getGym(), region), localeService, region);
        raid.setCreator(raidEntity.getCreator());
        raid.setId(raidEntity.getId());
        Map<String, SignUp> signUps = new HashMap<>();
        for (RaidEntitySignUp signUp : raidEntity.getSignUps()) {
            signUps.put(signUp.getResponsible(), new SignUp(signUp.getResponsible(), signUp.getNumberOfPeople(),
                    LocalTime.parse(signUp.getEta(), Utils.timeParseFormatter)));
        }
        raid.setSignUps(signUps);
        return raid;
    }

    public Raid getActiveRaidOrFallbackToExRaid(Gym gym, String region, User user) {
        RaidEntity raidEntity = getActiveOrFallbackToExRaidEntity(gym, region);
        if (raidEntity == null) {
            throw new RaidNotFoundException(gym, localeService, user);
        }
        final Raid raid = getRaidInstance(raidEntity);
        return raid;
    }

    private RaidEntity getActiveOrFallbackToExRaidEntity(Gym gym, String region) {
        RaidEntity raidEntity = null;
        List<RaidEntity> raidEntities = raidEntityRepository.findByGymAndRegionOrderByEndOfRaidAsc(gym.getName(), region);
        RaidEntity exEntity = null;
        for (RaidEntity entity : raidEntities) {
            if (entity.isExpired(clockService)) {
                raidEntityRepository.delete(entity);
            } else if (Utils.isRaidExPokemon(entity.getPokemon())) {
                exEntity = entity;
                break;
            } else {
                if (raidEntity != null) {
                    throw new IllegalStateException("Raid state in database seems off. " +
                            "Please notify the bot developer so it can be checked: " + raidEntity);
                }
                raidEntity = entity;
            }
        }

        if (raidEntity == null) {
            if (exEntity != null) {
                raidEntity = exEntity;
            }
        }
        return raidEntity;
    }

    public Set<Raid> getAllRaidsForRegion(String region) {
        removeExpiredRaids();
        List<RaidEntity> raidEntityList = raidEntityRepository.findByRegionOrderByPokemonAscEndOfRaidAsc(region);
        Set<Raid> activeRaids = new LinkedHashSet<>();
        for (RaidEntity entity : raidEntityList) {
            activeRaids.add(getRaidInstance(entity));
        }
        return activeRaids;
    }

    private void removeExpiredRaids() {
        List<RaidEntity> raidEntityList = raidEntityRepository.findAll();
        for (RaidEntity entity : raidEntityList) {
            removeRaidIfExpired(entity);
        }
    }

    private void removeRaidIfExpired(RaidEntity raidEntity) {
        if (raidEntity.isExpired(clockService)) {
            // Clean up expired raid
            raidEntityRepository.delete(raidEntity);
        }
    }

    public void addSignUp(User user, Raid raid, SignUp theSignUp) {
        RaidEntity entity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());
        RaidEntitySignUp entitySignUp = entity.getSignUp(user.getName());
        if (entitySignUp == null) {
            entity.addSignUp(new RaidEntitySignUp(user.getName(), theSignUp.getHowManyPeople(),
                    Utils.printTime(theSignUp.getArrivalTime())));
        } else {
            entitySignUp.setNumberOfPeople(theSignUp.getHowManyPeople(), localeService, user);
            entitySignUp.setEta(Utils.printTime(theSignUp.getArrivalTime()));
        }
        raidEntityRepository.save(entity);
    }

    public void removeSignUp(String userName, Raid raid, SignUp theSignUp) {
        RaidEntity entity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());
        entity.removeSignUp(new RaidEntitySignUp(userName, theSignUp.getHowManyPeople(),
                Utils.printTime(theSignUp.getArrivalTime())));
        raidEntityRepository.save(entity);
    }

    public Set<Raid> getRaidsInRegionForPokemon(String region, Pokemon pokemon) {
        removeExpiredRaids();
        List<RaidEntity> raidEntityList = raidEntityRepository.findByPokemonAndRegionOrderByEndOfRaidAsc(pokemon.getName(), region);
        Set<Raid> activeRaids = new LinkedHashSet<>();
        for (RaidEntity entity : raidEntityList) {
            activeRaids.add(getRaidInstance(entity));
        }
        return activeRaids;
    }

    public Raid changePokemon(Raid raid, Pokemon pokemon) {
        RaidEntity raidEntity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());
        if (!raidEntity.getPokemon().equalsIgnoreCase(raid.getPokemon().getName())) {
            throw new IllegalStateException("Database issues. Please notify the developer: magnus.mickelsson@gmail.com and describe what happened.");
        }
        raidEntity.setPokemon(pokemon.getName());
        raidEntity = raidEntityRepository.save(raidEntity);
        return getRaidInstance(raidEntity);
    }

    public Raid changeEndOfRaid(Raid raid, LocalDateTime newEndOfRaid) {
        RaidEntity raidEntity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());
        if (!raidEntity.getPokemon().equalsIgnoreCase(raid.getPokemon().getName())) {
            throw new IllegalStateException("Database issues. Please notify the developer: magnus.mickelsson@gmail.com and describe what happened.");
        }
        raidEntity.setEndOfRaid(newEndOfRaid);
        raidEntity = raidEntityRepository.save(raidEntity);
        return getRaidInstance(raidEntity);
    }

    public boolean delete(Raid raid) {
        RaidEntity raidEntity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());
        if (raidEntity != null) {
            raidEntityRepository.delete(raidEntity);
            return true;
        } else {
            return false;
        }
    }

    public Raid getById(String id) {
        return getRaidInstance(raidEntityRepository.getOne(id));
    }

    public Raid modifySignUp(String raidId, User user, int mystic, int instinct, int valor, int plebs, LocalDateTime startAt) {
        RaidEntity raidEntity = raidEntityRepository.findOne(raidId);
        RaidEntitySignUp signUp = raidEntity.getSignUp(user.getName());
        final String startAtTime = Utils.printTime(startAt.toLocalTime());
        if (signUp == null) {
            final int sum = mystic + instinct + valor + plebs;
            assertSumNotLessThanOne(user, sum);
            raidEntity.addSignUp(new RaidEntitySignUp(user.getName(), sum, startAtTime));
        } else {
            int sum = signUp.getNumberOfPeople();
            if (startAt.toLocalTime().equals(Utils.parseTime(user, signUp.getEta(), localeService))) {
                sum = sum + mystic + instinct + valor + plebs;
            } else {
                signUp.setEta(startAtTime);
                // Reset number of signups to what the input gives since we changed time
                sum = mystic + instinct + valor + plebs;
            }
            assertSumNotLessThanOne(user, sum);
            signUp.setNumberOfPeople(sum, localeService, user);
        }
        raidEntity = raidEntityRepository.save(raidEntity);

        return getRaidInstance(raidEntity);
    }

    private void assertSumNotLessThanOne(User user, int sum) {
        if (sum <= 0) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.ERROR_PARSE_PLAYERS,
                    localeService.getLocaleForUser(user),
                    "" + sum, String.valueOf(HIGH_LIMIT_FOR_SIGNUPS)));
        }
    }

    public Raid removeFromSignUp(String raidId, User user, int mystic, int instinct, int valor, int plebs, LocalDateTime startAt) {
        RaidEntity raidEntity = raidEntityRepository.findOne(raidId);
        RaidEntitySignUp signUp = raidEntity.getSignUp(user.getName());
        final String startAtTime = Utils.printTime(startAt.toLocalTime());
        if (signUp == null) {
            // Ignore this case, when there is no signup to remove from. Silent ignore.
        } else if (startAtTime.equals(signUp.getEta())){
            final int sum = signUp.getNumberOfPeople() - mystic - instinct - valor - plebs;
            if (sum <= 0) {
                // Remove signup
                raidEntity.removeSignUp(signUp);
            } else {
                signUp.setNumberOfPeople(sum, localeService, user);
            }
            raidEntity = raidEntityRepository.save(raidEntity);
        } else {
            // Ignore if they're trying to remove signups for a group they're no longer signed up for - we let them untick their emote
        }
        return getRaidInstance(raidEntity);
    }

    public void removeAllSignUpsAt(Raid raid, LocalDateTime startAt) {
        Validate.notNull(raid, "Raid cannot be null");
        Validate.notNull(startAt, "Start time cannot be null");
        RaidEntity entity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());
        if (entity != null) {
            for (SignUp signUp : raid.getSignUps()) {
                if (signUp.getArrivalTime().equals(startAt.toLocalTime())) {
                    RaidEntitySignUp removed = entity.removeSignUp(new RaidEntitySignUp(signUp.getUserName(), signUp.getHowManyPeople(),
                            Utils.printTime(signUp.getArrivalTime())));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Removed signup: " + removed);
                    }
                }
            }
            raidEntityRepository.save(entity);
        }
    }
}
