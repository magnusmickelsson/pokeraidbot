package pokeraidbot.domain;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.Utils;
import pokeraidbot.domain.errors.RaidExistsException;
import pokeraidbot.domain.errors.RaidNotFoundException;
import pokeraidbot.infrastructure.jpa.RaidEntity;
import pokeraidbot.infrastructure.jpa.RaidEntityRepository;
import pokeraidbot.infrastructure.jpa.RaidEntitySignUp;

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
        // If you want to test, and it's currently in the "dead time" where raids can't be created, set time manually like this
        clockService.setMockTime(LocalTime.of(10, 30));
        Utils.setClockService(clockService);
    }

    public void newRaid(String raidCreatorName, Raid raid) {
        List<RaidEntity> raidEntities = raidEntityRepository.findByGymAndRegion(raid.getGym().getName(),
                raid.getRegion());

        // There can only be one EX raid and one active raid at the most, and if we already have two, this new raid is one too many
        if (raidEntities != null && raidEntities.size() > 1) {
            throw new RaidExistsException(raidCreatorName, raid, localeService, LocaleService.DEFAULT);
        }

        final String pokemonName = raid.getPokemon().getName();

        RaidEntity raidEntity = null;
        assert raidEntities != null;
        for (RaidEntity entity : raidEntities) {
            if (Utils.isRaidExPokemon(pokemonName) && (!Utils.isSamePokemon(pokemonName, entity.getPokemon()))) {
                saveRaid(raidCreatorName, raid);
                removeRaidIfExpired(entity);
                break;
            } else if (!Utils.isRaidExPokemon(pokemonName) && (Utils.isRaidExPokemon(entity.getPokemon()))) {
                saveRaid(raidCreatorName, raid);
                break;
            } else {
                throw new RaidExistsException(raidCreatorName, getRaidInstance(raidEntity),
                        localeService, LocaleService.DEFAULT);
            }
        }

        if (raidEntity != null) {
            final String existingEntityPokemon = raidEntity.getPokemon();
            if (Utils.isSamePokemon(pokemonName, existingEntityPokemon) || (!Utils.oneIsMewTwo(pokemonName, existingEntityPokemon))) {
            }

            if (Utils.raidsCollide(raid.getEndOfRaid(), raidEntity.getEndOfRaid())) {
                throw new RaidExistsException(raidCreatorName, getRaidInstance(raidEntity),
                        localeService, LocaleService.DEFAULT);
            }
        } else {
        }
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
        final Raid raid = getRaidInstance(raidEntity);
        return raid;
    }

    private RaidEntity getActiveOrFallbackToExRaidEntity(Gym gym, String region) {
        RaidEntity raidEntity = null;
        List<RaidEntity> raidEntities = raidEntityRepository.findByGymAndRegion(gym.getName(), region);
        RaidEntity exEntity = null;
        for (RaidEntity entity : raidEntities) {
            if (entity.isActive(clockService)) {
                raidEntity = entity;
            } else if (entity.isExpired(clockService)) {
                raidEntityRepository.delete(entity);
                throw new RaidNotFoundException(gym, localeService);
            } else if (Utils.isRaidExPokemon(entity.getPokemon())) {
                exEntity = entity;
            }
        }

        if (raidEntity == null) {
            if (exEntity != null) {
                raidEntity = exEntity;
            } else {
                throw new RaidNotFoundException(gym, localeService);
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
        entity.addSignUp(new RaidEntitySignUp(userName, theSignUp.getHowManyPeople(),
                Utils.printTime(theSignUp.getArrivalTime())));
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
}
