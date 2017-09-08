package pokeraidbot;

import org.apache.commons.lang3.tuple.Pair;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.errors.RaidExistsException;
import pokeraidbot.domain.errors.RaidNotFoundException;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RaidRepository {
    private Map<Gym, Pair<String, Raid>> raids = new ConcurrentHashMap<>();

    public void newRaid(String raidCreatorName, Raid raid) {
        final Pair<String, Raid> pair = raids.get(raid.getGym());
        if (pair != null && (raid.equals(pair.getRight()) || raid.getGym().equals(pair.getRight().getGym()))) {
            throw new RaidExistsException(raidCreatorName, raid);
        } else if (pair == null) {
            raids.put(raid.getGym(), Pair.of(raidCreatorName, raid));
        } else {
            throw new IllegalStateException("Unknown problem when trying to create raid: " + raid);
        }
    }

    public Raid getRaid(Gym gym) {
        final Pair<String, Raid> pair = raids.get(gym);
        if (pair == null) {
            throw new RaidNotFoundException(gym);
        }
        final Raid raid = pair.getRight();
        if (raid.getEndOfRaid().isBefore(LocalTime.now())) {
            raids.remove(raid.getGym());
            throw new RaidNotFoundException(gym);
        }
        return raid;
    }

    public Set<Raid> getAllRaids() {
        LocalTime now = LocalTime.now();
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
