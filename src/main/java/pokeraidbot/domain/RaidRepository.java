package pokeraidbot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
                          RaidEntityRepository raidEntityRepository, PokemonRepository pokemonRepository, GymRepository gymRepository) {
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
        RaidEntity raidEntity = raidEntityRepository.findDistinctFirstByGym(raid.getGym().getName());

        if (raidEntity != null) {
            if (raidEntity.isActive(clockService)) {
                throw new RaidExistsException(raidCreatorName, getRaidInstance(raidEntity), localeService, LocaleService.SWEDISH);
            } else {
                // Clean up expired raid
                raidEntityRepository.delete(raidEntity);
            }
        }

        raidEntityRepository.save(new RaidEntity(UUID.randomUUID().toString(),
                raid.getPokemon().getName(),
                raid.getEndOfRaid(),
                raid.getGym().getName(),
                raidCreatorName
        ));
    }

    private Raid getRaidInstance(RaidEntity raidEntity) {
        final Raid raid = new Raid(pokemonRepository.getPokemon(raidEntity.getPokemon()), raidEntity.getEndOfRaid(),
                gymRepository.findByName(raidEntity.getGym()), localeService);
        Map<String, SignUp> signUps = new HashMap<>();
        for (RaidEntitySignUp signUp : raidEntity.getSignUps()) {
            signUps.put(signUp.getUser(), new SignUp(signUp.getUser(), signUp.getNumberOfPeople(), LocalTime.parse(signUp.getEta(), Utils.dateTimeParseFormatter)));
        }
        raid.setSignUps(signUps);
        return raid;
    }

    public Raid getRaid(Gym gym) {
        RaidEntity raidEntity = raidEntityRepository.findDistinctFirstByGym(gym.getName());

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

    public Set<Raid> getAllRaids() {
        removeExpiredRaids();
        List<RaidEntity> raidEntityList = raidEntityRepository.findAll();
        Set<Raid> activeRaids = new HashSet<>();
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
        RaidEntity entity = raidEntityRepository.findDistinctFirstByGym(raid.getGym().getName());
        entity.addSignUp(new RaidEntitySignUp(userName, theSignUp.getHowManyPeople(), Utils.printTime(theSignUp.getArrivalTime())));
        raidEntityRepository.save(entity);
    }

    public void removeSignUp(String userName, Raid raid, SignUp theSignUp) {
        RaidEntity entity = raidEntityRepository.findDistinctFirstByGym(raid.getGym().getName());
        entity.removeSignUp(new RaidEntitySignUp(userName, theSignUp.getHowManyPeople(), Utils.printTime(theSignUp.getArrivalTime())));
        raidEntityRepository.save(entity);
    }
}
