package pokeraidbot.infrastructure.jpa.config;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.Map;

public interface ConfigRepository extends JpaRepository<Config, String> {
    Config findByServer(String server);

    default Config getConfigForServer(String server) {
        final Config config = findByServer(server);
        if (config == null) {
            // todo: i18n
            throw new RuntimeException("Configuration not found for server: \"" + server +
                    "\" - have the channel owner run the command !raid install");
        }
        return config;
    }

    default Map<String, Config> getAllConfig() {
        Map<String, Config> configs = new HashMap<>();
        for (Config config : findAll()) {
            configs.put(config.getServer(), config);
        }
        return configs;
    }
}
