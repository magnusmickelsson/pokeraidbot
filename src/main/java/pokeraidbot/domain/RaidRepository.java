package pokeraidbot.domain;

import org.apache.commons.lang3.Validate;
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
//        clockService.setMockTime(LocalTime.of(10, 30));
        Utils.setClockService(clockService);
    }

    public void newRaid(String raidCreatorName, Raid raid) {
        RaidEntity raidEntity = raidEntityRepository.findDistinctFirstByGymAndRegion(raid.getGym().getName(),
                raid.getRegion());

        if (raidEntity != null) {
            if (raidEntity.isActive(clockService)) {
                throw new RaidExistsException(raidCreatorName, getRaidInstance(raidEntity),
                        localeService, LocaleService.DEFAULT);
            } else {
                // Clean up expired raid
                raidEntityRepository.delete(raidEntity);
            }
        }

        raidEntityRepository.save(new RaidEntity(UUID.randomUUID().toString(),
                raid.getPokemon().getName(),
                raid.getEndOfRaid(),
                raid.getGym().getName(),
                raidCreatorName,
                raid.getRegion()));
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
                    LocalTime.parse(signUp.getEta(), Utils.dateTimeParseFormatter)));
        }
        raid.setSignUps(signUps);
        return raid;
    }

    public Raid getRaid(Gym gym, String region) {
        RaidEntity raidEntity = raidEntityRepository.findDistinctFirstByGymAndRegion(gym.getName(), region);

        if (raidEntity == null) {
            throw new RaidNotFoundException(gym, localeService);
        }
        if (raidEntity.getEndOfRaid().isBefore(clockService.getCurrentTime())) {
            raidEntityRepository.delete(raidEntity);
            throw new RaidNotFoundException(gym, localeService);
        }
        final Raid raid = getRaidInstance(raidEntity);
        return raid;
    }

    public Set<Raid> getAllRaidsForRegion(String region) {
        removeExpiredRaids();
        List<RaidEntity> raidEntityList = raidEntityRepository.findByRegionOrderByPokemon(region);
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
        if (!raidEntity.isActive(clockService)) {
            // Clean up expired raid
            raidEntityRepository.delete(raidEntity);
        }
    }

    public void addSignUp(String userName, Raid raid, SignUp theSignUp) {
        RaidEntity entity = raidEntityRepository.findDistinctFirstByGymAndRegion(raid.getGym().getName(),
                raid.getRegion());
        entity.addSignUp(new RaidEntitySignUp(userName, theSignUp.getHowManyPeople(),
                Utils.printTime(theSignUp.getArrivalTime())));
        raidEntityRepository.save(entity);
    }

    public void removeSignUp(String userName, Raid raid, SignUp theSignUp) {
        RaidEntity entity = raidEntityRepository.findDistinctFirstByGymAndRegion(raid.getGym().getName(),
                raid.getRegion());
        entity.removeSignUp(new RaidEntitySignUp(userName, theSignUp.getHowManyPeople(),
                Utils.printTime(theSignUp.getArrivalTime())));
        raidEntityRepository.save(entity);
    }
}
