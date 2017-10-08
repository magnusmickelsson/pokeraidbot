package pokeraidbot.domain.raid;

import net.dv8tion.jda.core.entities.User;
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
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.raid.RaidEntity;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntitySignUp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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

    public Raid getActiveRaidOrFallbackToExRaid(Gym gym, String region) {
        RaidEntity raidEntity = getActiveOrFallbackToExRaidEntity(gym, region);
        if (raidEntity == null) {
            throw new RaidNotFoundException(gym, localeService);
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

    public void addSignUp(String userName, Raid raid, SignUp theSignUp) {
        RaidEntity entity = getActiveOrFallbackToExRaidEntity(raid.getGym(), raid.getRegion());
        RaidEntitySignUp entitySignUp = entity.getSignUp(userName);
        if (entitySignUp == null) {
            entity.addSignUp(new RaidEntitySignUp(userName, theSignUp.getHowManyPeople(),
                    Utils.printTime(theSignUp.getArrivalTime())));
        } else {
            entitySignUp.setNumberOfPeople(theSignUp.getHowManyPeople());
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

    public Raid addToOrCreateSignup(String raidId, String userName, int mystic, int instinct, int valor, int plebs, LocalDateTime startAt) {
        final RaidEntity raidEntity = raidEntityRepository.findOne(raidId);
        RaidEntitySignUp signUp = raidEntity.getSignUp(userName);
        final String startAtTime = Utils.printTime(startAt.toLocalTime());
        if (signUp == null) {
            raidEntity.addSignUp(new RaidEntitySignUp(userName, mystic + instinct + valor + plebs, startAtTime));
        } else {
            signUp.setNumberOfPeople(signUp.getNumberOfPeople() + mystic + instinct + valor + plebs);
            signUp.setEta(startAtTime);
        }
        return getRaidInstance(raidEntity);
    }

    public Raid removeFromSignUp(String raidId, User user, int mystic, int instinct, int valor, int plebs, LocalDateTime startAt) {
        final RaidEntity raidEntity = raidEntityRepository.findOne(raidId);
        RaidEntitySignUp signUp = raidEntity.getSignUp(user.getName());
        final String startAtTime = Utils.printTime(startAt.toLocalTime());
        if (signUp == null) {
            // todo: i18n
            throw new UserMessedUpException(user, "Det fanns ingen anmälan att ta bort folk från!");//"There was no signup to remove people from!");
        } else {
            signUp.setNumberOfPeople(signUp.getNumberOfPeople() - mystic - instinct - valor - plebs);
            signUp.setEta(startAtTime);
        }
        return getRaidInstance(raidEntity);
    }
}
