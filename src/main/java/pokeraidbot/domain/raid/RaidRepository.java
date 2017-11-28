package pokeraidbot.domain.raid;

import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
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
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        removeAllExpiredRaids();
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
        assertSignupTimeNotBeforeRaidStartAndNow(user, realEta, raid.getEndOfRaid(), localeService, raid.isExRaid());

        raid.signUp(user, numberOfPeople, eta, this);
        final String currentSignupText = localeService.getMessageFor(LocaleService.CURRENT_SIGNUPS, localeForUser);
        final Set<SignUp> signUps = raid.getSignUps();
        Set<String> signUpNames = Utils.getNamesOfThoseWithSignUps(signUps, true);
        final String allSignUpNames = StringUtils.join(signUpNames, ", ");
        final String signUpText = raid.getSignUps().size() > 1 ? currentSignupText + "\n" + allSignUpNames : "";
        return localeService.getMessageFor(LocaleService.SIGNUPS, localeForUser, userName,
                gym.getName(), signUpText);
    }

    public String executeUnsignCommand(Config config,
                                       User user,
                                       Locale localeForUser,
                                       String[] args,
                                       String help) {
        String people = args[0];
        String userName = user.getName();
        if (args.length < 2 || args.length > 10) {
            throw new WrongNumberOfArgumentsException(user, localeService, 2, args.length, help);
        }
        Integer numberOfPeople = Utils.assertNotTooManyOrNoNumber(user, localeService, people);

        StringBuilder gymNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            gymNameBuilder.append(args[i]).append(" ");
        }
        String gymName = gymNameBuilder.toString().trim();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        Raid raid = getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        final RaidEntitySignUp signUp = findEntityByRaidId(raid.getId()).getSignUp(user.getName());
        if (signUp == null) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_SIGNUP_AT_GYM,
                    localeForUser, user.getName(), gym.getName()));
        }

        raid = removeFromSignUp(raid.getId(), user, 0, 0, 0, numberOfPeople,
                LocalDateTime.of(raid.getEndOfRaid().toLocalDate(),
                signUp.getArrivalTime()));
        final String currentSignupText = localeService.getMessageFor(LocaleService.CURRENT_SIGNUPS, localeForUser);
        final Set<SignUp> signUps = raid.getSignUps();
        Set<String> signUpNames = Utils.getNamesOfThoseWithSignUps(signUps, true);
        final String allSignUpNames = StringUtils.join(signUpNames, ", ");
        final String signUpText = raid.getSignUps().size() > 1 ? currentSignupText + "\n" + allSignUpNames : "";
        return localeService.getMessageFor(LocaleService.UNSIGN, localeForUser, userName,
                gym.getName(), signUpText);
    }

    public Raid newRaid(User raidCreator, Raid raid) {
        RaidEntity raidEntity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());

        if (raidEntity != null) {
            if ((raidEntity.isExRaid() && !raid.isExRaid()) || (!raidEntity.isExRaid() && raid.isExRaid())) {
                // This is a situation we should allow, in case Niantic now allows for raids to happen at EX gyms
            } else {
                throw new RaidExistsException(raidCreator, getRaidInstance(raidEntity),
                        localeService, localeService.getLocaleForUser(raidCreator));
            }
        }

        return getRaidInstance(saveRaid(raidCreator, raid));
    }

    private RaidEntity findEntityByRaidId(String raidId) {
        final RaidEntity raidEntity = raidId == null ? null : raidEntityRepository.findOne(raidId);
        removeRaidIfExpired(raidEntity);
        return raidEntity;
    }

    private RaidEntity saveRaid(User raidCreator, Raid raid) {
        final RaidEntity toBeSaved = new RaidEntity(UUID.randomUUID().toString(),
                raid.getPokemon().getName(),
                raid.getEndOfRaid(),
                raid.getGym().getName(),
                raidCreator.getName(),
                raid.getRegion());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating raid: " + toBeSaved);
        }
        return raidEntityRepository.save(toBeSaved);

    }

    private Raid getRaidInstance(RaidEntity raidEntity) {
        Validate.notNull(raidEntity);
        final String region = raidEntity.getRegion();
        final Raid raid = new Raid(pokemonRepository.getByName(raidEntity.getPokemon()),
                raidEntity.getEndOfRaid(),
                gymRepository.findByName(raidEntity.getGym(), region), localeService, region);
        raid.setCreator(raidEntity.getCreator());
        raid.setId(raidEntity.getId());
        Map<String, SignUp> signUps = new ConcurrentHashMap<>();
        for (RaidEntitySignUp signUp : raidEntity.getSignUpsAsSet()) {
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
                LOGGER.info("Removing expired raid: " + entity.getId());
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
        removeExpiredRaids(region);
        List<RaidEntity> raidEntityList = raidEntityRepository.findByRegionOrderByPokemonAscEndOfRaidAsc(region);
        Set<Raid> activeRaids = new LinkedHashSet<>();
        for (RaidEntity entity : raidEntityList) {
            activeRaids.add(getRaidInstance(entity));
        }
        return activeRaids;
    }

    private void removeAllExpiredRaids() {
        List<RaidEntity> raidEntityList = raidEntityRepository.findAll();
        for (RaidEntity entity : raidEntityList) {
            removeRaidIfExpired(entity);
        }
    }

    private void removeExpiredRaids(String region) {
        List<RaidEntity> raidEntityList = raidEntityRepository.findByRegion(region);
        for (RaidEntity entity : raidEntityList) {
            removeRaidIfExpired(entity);
        }
    }

    // Returns null if raid is expired
    private RaidEntity removeRaidIfExpired(RaidEntity raidEntity) {
        if (raidEntity != null && raidEntity.isExpired(clockService)) {
            final String id = raidEntity.getId();
            // Clean up expired raid
            raidEntityRepository.delete(raidEntity);
            LOGGER.info("Removed expired raid with ID: " + id);
            return null;
        } else {
            return raidEntity;
        }
    }

    public void addSignUp(User user, Raid raid, SignUp theSignUp) {
        RaidEntity entity = findEntityByRaidId(raid.getId());

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

    public void removeSignUp(User user, Raid raid, SignUp theSignUp) {
        RaidEntity entity = findEntityByRaidId(raid.getId());
        entity.removeSignUp(new RaidEntitySignUp(user.getName(), theSignUp.getHowManyPeople(),
                Utils.printTime(theSignUp.getArrivalTime())));
        raidEntityRepository.save(entity);
    }

    public Set<Raid> getRaidsInRegionForPokemon(String region, Pokemon pokemon) {
        removeExpiredRaids(region);
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Changed pokemon for raid " + raid + " to " + pokemon + ".");
        }
        return getRaidInstance(raidEntity);
    }

    public Raid changeEndOfRaid(String raidId, LocalDateTime newEndOfRaid) {
        RaidEntity raidEntity = findEntityByRaidId(raidId);
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
        final RaidEntity entity = raidEntityRepository.findOne(id);
        return getRaidInstance(entity);
    }

    public Raid modifySignUp(String raidId, User user, int mystic, int instinct, int valor, int plebs,
                             LocalDateTime startAt) {
        RaidEntity raidEntity = findEntityByRaidId(raidId);
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

    public Raid removeFromSignUp(String raidId, User user, int mystic, int instinct, int valor, int plebs,
                                 LocalDateTime startAt) {
        RaidEntity raidEntity = findEntityByRaidId(raidId);
        if (raidEntity == null) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_RAID_AT_GYM, localeService.getLocaleForUser(user)));
        }
        RaidEntitySignUp signUp = raidEntity.getSignUp(user.getName());
        final String startAtTime = Utils.printTime(startAt.toLocalTime());
        if (signUp == null) {
            // Ignore this case, when there is no signup to remove from. Silent ignore.
        } else if (startAtTime.equals(signUp.getEta())) {
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

    public Raid removeAllSignUpsAt(String raidId, LocalDateTime startAt) {
        Validate.notNull(raidId, "Raid ID cannot be null");
        Validate.notNull(startAt, "Start time cannot be null");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to remove signups for raid " + raidId + " at " + printTimeIfSameDay(startAt));
        }
        RaidEntity entity = findEntityByRaidId(raidId);
        if (entity != null) {
            for (RaidEntitySignUp signUp : entity.getSignUpsAsSet()) {
                if (signUp.getArrivalTime().equals(startAt.toLocalTime())) {
                    RaidEntitySignUp removed = entity.removeSignUp(signUp);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Removed signup: " + removed);
                    }
                }
            }
            entity = raidEntityRepository.save(entity);
        }

        return getRaidInstance(entity);
    }

    public void moveAllSignUpsForTimeToNewTime(String raidId, LocalDateTime currentStartAt, LocalDateTime newDateTime, User user) {
        Validate.notNull(raidId, "Raid ID cannot be null");
        Validate.notNull(currentStartAt, "Current start time cannot be null");
        Validate.notNull(newDateTime, "New start time cannot be null");
        Validate.notNull(user, "User cannot be null");
        RaidEntity entity = raidEntityRepository.findOne(raidId);
        if (entity != null) {
            for (RaidEntitySignUp signUp : entity.getSignUpsAsSet()) {
                if (signUp.getArrivalTime().equals(currentStartAt.toLocalTime())) {
                    signUp.setEta(Utils.printTime(newDateTime.toLocalTime()));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Changed ETA for signup: " + signUp);
                    }
                }
            }
            raidEntityRepository.save(entity);
        } else {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_RAID_AT_GYM, localeService.getLocaleForUser(user)));
        }
        // todo: throw error if problem?
    }

    public RaidGroup newGroupForRaid(User user, RaidGroup group, Raid raid) {
        Validate.notNull(user, "User");
        Validate.notNull(group, "Group");
        Validate.notNull(raid, "Raid");
        Validate.notEmpty(raid.getId(), "Raid ID");
        RaidEntity raidEntity = findEntityByRaidId(raid.getId());
        final boolean added = raidEntity.addGroup(group);
        if (!added) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.GROUP_NOT_ADDED,
                    localeService.getLocaleForUser(user), String.valueOf(raid)));
        }
        return group;
    }

    public List<RaidGroup> getGroupsForServer(String server) {
        Validate.notEmpty(server, "Server is empty");
        return raidEntityRepository.findGroupsForServer(server);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RaidGroup deleteGroupInNewTransaction(String raidId, String groupId) {
        final RaidEntity entity = findEntityByRaidId(raidId);
        final RaidGroup removedGroup = entity.removeGroup(groupId);
        if (removedGroup == null) {
            throw new RuntimeException("No group with ID " + groupId + " for raid " + entity);
        }
        return removedGroup;
    }

    public RaidGroup deleteGroup(String raidId, String groupId) {
        final RaidEntity entity = findEntityByRaidId(raidId);
        final RaidGroup removedGroup = entity.removeGroup(groupId);
        if (removedGroup == null) {
            throw new RuntimeException("No group with ID " + groupId + " for raid " + entity);
        }
        return removedGroup;
    }

    public boolean userHasGroupForRaid(User user, Raid raid) {
        RaidEntity entity = findEntityByRaidId(raid.getId());
        if (entity.userHasGroup(user)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasGroupForRaid(User user, Raid raid, LocalDateTime startAt) {
        RaidEntity entity = findEntityByRaidId(raid.getId());
        if (entity.hasGroupAt(user, startAt)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean existsGroupForRaidAt(Raid raid, LocalDateTime startAt) {
        RaidEntity entity = findEntityByRaidId(raid.getId());
        if (entity.existsGroupAt(startAt)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasManyGroupsForRaid(User user, Raid raid) {
        RaidEntity entity = findEntityByRaidId(raid.getId());
        if (entity.hasManyGroupsBy(user)) {
            return true;
        } else {
            return false;
        }
    }

    public RaidGroup changeGroup(User user, String raidId, String groupCreatorId, LocalDateTime currentStartAt,
                                 LocalDateTime newDateTime) {
        final RaidEntity entityByRaidId = findEntityByRaidId(raidId);
        if (entityByRaidId == null) {
                throw new UserMessedUpException(user,
                        localeService.getMessageFor(LocaleService.NO_RAID_AT_GYM, localeService.getLocaleForUser(user)));
        }
        RaidGroup group = entityByRaidId.getGroupByCreatorAndStart(groupCreatorId, currentStartAt);
        if (group == null) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.NO_SUCH_GROUP,
                    localeService.getLocaleForUser(user)));
        }
        group.setStartsAt(newDateTime);
        raidEntityRepository.save(entityByRaidId);
        return group;
    }
}
