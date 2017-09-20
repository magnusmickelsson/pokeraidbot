package pokeraidbot;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import pokeraidbot.domain.*;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.RaidEntityRepository;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"pokeraidbot"})
public class TestServerMain {
    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        SpringApplication.run(pokeraidbot.TestServerMain.class, args);
    }

    @Bean
    public LocaleService getLocaleService() {
        return new LocaleService();
    }

    @Bean
    public GymRepository getGymRepository(LocaleService localeService, ConfigRepository configRepository) {
        Map<String, Config> configMap = configRepository.getAllConfig();
        return getGymRepositoryForConfig(localeService, configRepository);
    }

    public static GymRepository getGymRepositoryForConfig(LocaleService localeService, ConfigRepository configRepository) {
        Map<String, Set<Gym>> gymsPerRegion = new HashMap<>();
        final Map<String, Config> configMap = configRepository.getAllConfig();
        System.out.println("Config has following servers: " + configMap.keySet());
        for (String server : configMap.keySet()) {
            final Config config = configRepository.getConfigForServer(server);
            final Set<Gym> gymsInRegion = new CSVGymDataReader("/gyms_" + config.region + ".csv").readAll();
            gymsPerRegion.put(server, gymsInRegion);
            System.out.println("Loaded " + gymsInRegion.size() + " gyms for server " + server + ".");
        }
        return new GymRepository(gymsPerRegion, localeService);
    }

    @Bean
    public PokemonRepository getPokemonRepository(LocaleService localeService) {
        return new PokemonRepository("/mons.json", localeService);
    }

    @Bean
    public PokemonRaidStrategyService getRaidInfoService(PokemonRepository pokemonRepository) {
        return new PokemonRaidStrategyService(pokemonRepository);
    }

    @Bean
    public ClockService getClockService() {
        return new ClockService();
    }

    @Bean
    public RaidRepository getRaidRepository(LocaleService localeService, RaidEntityRepository entityRepository,
                                            PokemonRepository pokemonRepository, GymRepository gymRepository,
                                            ClockService clockService) {
        return new RaidRepository(clockService, localeService, entityRepository, pokemonRepository, gymRepository);
    }

    @Bean
    public ConfigRepository getConfigRepository() {
        return configRepositoryForTests();
    }

    public static ConfigRepository configRepositoryForTests() {
        final HashMap<String, Config> configurationMap = new HashMap<>();
        configurationMap.put("uppsala", new Config("uppsala"));
        configurationMap.put("ängelholm", new Config("ängelholm"));
        return new ConfigRepository(configurationMap);
    }
}
