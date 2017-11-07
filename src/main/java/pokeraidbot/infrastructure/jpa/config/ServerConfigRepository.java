package pokeraidbot.infrastructure.jpa.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ServerConfigRepository extends JpaRepository<Config, String> {
    Config findByServer(String server);

    default Config getConfigForServer(String server) {
        final Config config = findByServer(server);
        return config;
    }

    default Map<String, Config> getAllConfig() {
        Map<String, Config> configs = new HashMap<>();
        for (Config config : findAll()) {
            configs.put(config.getServer(), config);
        }
        return configs;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default void setOverviewMessageIdForServer(String server, String overviewMessageId) {
        final Config config = findByServer(server);
        config.setOverviewMessageId(overviewMessageId);
        save(config);
    }
}
