package pokeraidbot.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigRepository {
    private Map<String, Config> configurationMap = new HashMap<>();

    public ConfigRepository(Map<String, Config> configurationMap) {
        this.configurationMap = configurationMap;
    }

    public Config getConfigForServer(String server) {
        final Config config = configurationMap.get(server);
        if (config == null) {
            // todo: i18n
            throw new RuntimeException("Configuration not found for server: \"" + server +
                    "\" - contact bot developer at magnus.mickelsson@gmail.com to fix this.");
        }

        return config;
    }

    public Map<String, Config> getAllConfig() {
        return Collections.unmodifiableMap(configurationMap);
    }
}
