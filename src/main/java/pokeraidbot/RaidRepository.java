package pokeraidbot;

import org.apache.commons.lang3.tuple.Pair;
import pokeraidbot.domain.*;
import pokeraidbot.domain.errors.RaidExistsException;
import pokeraidbot.domain.errors.RaidNotFoundException;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RaidRepository {
    private Map<Gym, Pair<String, Raid>> raids = new HashMap<>();

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
