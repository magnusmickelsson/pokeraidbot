package pokeraidbot.domain;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.domain.errors.RaidExistsException;
import pokeraidbot.domain.errors.RaidNotFoundException;
import pokeraidbot.infrastructure.jpa.RaidEntity;
import pokeraidbot.infrastructure.jpa.RaidEntityRepository;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RaidRepository {
    private ClockService clockService;
    private LocaleService localeService;
    private RaidEntityRepository raidEntityRepository;
    private PokemonRepository pokemonRepository;
//    private Map<Gym, Pair<String, Raid>> raids = new ConcurrentHashMap<>();

    // Byte code instrumentation
    protected RaidRepository() {
    }

    @Autowired
    public RaidRepository(ClockService clockService, LocaleService localeService,
                          RaidEntityRepository raidEntityRepository, PokemonRepository pokemonRepository) {
        this.clockService = clockService;
        this.localeService = localeService;
        this.raidEntityRepository = raidEntityRepository;
        this.pokemonRepository = pokemonRepository;
    }

    @Transactional
    public void newRaid(String raidCreatorName, Raid raid) {
        RaidEntity raidEntity = raidEntityRepository.findDistinctFirstByGym(raid.getGym().getName());
//        final Pair<String, Raid> pair = raids.get(raid.getGym());
//        if (pair != null && (raid.equals(pair.getRight()) || raid.getGym().equals(pair.getRight().getGym()))) {
        if (raidEntity != null) {
            if (raidEntity.isActive(clockService)) {
                throw new RaidExistsException(raidCreatorName, getRaidInstance(raidEntity), localeService, LocaleService.SWEDISH);
            } else {
                // Clean up expired raid
                raidEntityRepository.delete(raidEntity);
            }
        raidEntityRepository.save(new RaidEntity(UUID.randomUUID().toString(),
                raid.getPokemon().getName(),
                raid.getEndOfRaid(),
                raid.getGym().getName(),
        ));
    }

    private Raid getRaidInstance(RaidEntity raidEntity) {
        return new Raid();
    }

    private Raid getRaidInstance(RaidEntity raidEntity) {
        return new Raid();
    }

    public Raid getRaid(Gym gym) {
        final Pair<String, Raid> pair = raids.get(gym);
        if (pair == null) {
            throw new RaidNotFoundException(gym, localeService);
        }
        final Raid raid = pair.getRight();
        if (raid.getEndOfRaid().isBefore(clockService.getCurrentTime())) {
            raids.remove(raid.getGym());
            throw new RaidNotFoundException(gym, localeService);
        }
        return raid;
    }

    public Set<Raid> getAllRaids() {
        LocalTime now = clockService.getCurrentTime();
        final Set<Raid> currentRaids = new HashSet<>(this.raids.values().stream().filter(pair -> pair.getRight().getEndOfRaid().isAfter(now)).map(Pair::getRight).collect(Collectors.toSet()));
        removeExpiredRaids(now);
        return currentRaids;
    }

    private void removeExpiredRaids(LocalTime now) {
        final Set<Raid> oldRaids = new HashSet<>(this.raids.values().stream().filter(pair -> pair.getRight().getEndOfRaid().isBefore(now)).map(Pair::getRight).collect(Collectors.toSet()));
        for (Raid r : oldRaids) {
            raids.remove(r.getGym());
        }
    }
}
