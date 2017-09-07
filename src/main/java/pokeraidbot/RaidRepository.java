package pokeraidbot;

import org.apache.commons.lang3.tuple.Pair;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.Raid;
import pokeraidbot.domain.errors.RaidExistsException;
import pokeraidbot.domain.errors.RaidNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RaidRepository {
    private Map<Gym, Pair<String, Raid>> raids = new ConcurrentHashMap<>();

    public void newRaid(String raidCreatorName, Raid raid) {
        final Pair<String, Raid> pair = raids.get(raid.getGym());
        if (pair != null && raid.equals(pair.getRight())) {
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
        return pair.getRight();
    }
}
