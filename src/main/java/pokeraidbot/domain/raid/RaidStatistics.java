package pokeraidbot.domain.raid;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RaidStatistics {
    private Map<String, ServerRaidStatistics> statisticsMap = new ConcurrentHashMap<>();

    public RaidStatistics() {
    }

    public Map<String, ServerRaidStatistics> getAll() {
        return Collections.unmodifiableMap(statisticsMap);
    }

    public void updateForRaid(Raid raid) {

    }
}
