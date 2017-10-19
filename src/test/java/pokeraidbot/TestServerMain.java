package pokeraidbot;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRaidStrategyService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"pokeraidbot"})
public class TestServerMain {
    @Autowired
    ConfigRepository configRepository;

    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        SpringApplication.run(pokeraidbot.TestServerMain.class, args);
    }

    @Bean
    public LocaleService getLocaleService() {
        return new LocaleService("sv");
    }

    @Bean
    public GymRepository getGymRepository(LocaleService localeService, ConfigRepository configRepository) {
        Map<String, Config> configMap = configRepository.getAllConfig();
        return getGymRepositoryForConfig(localeService, configRepository);
    }

    @PostConstruct
    @Transactional
    public void initializeConfig() {
        if (configRepository.findAll().size() == 0) {
            // My test servers
            configRepository.save(new Config("uppsala", "zhorhn tests stuff"));
            configRepository.save(new Config("uppsala", "pokeraidbot_lab"));
            configRepository.save(new Config("uppsala", "pokeraidbot_lab2"));
            configRepository.save(new Config("uppsala", "pokeraidbot_stage"));
            configRepository.save(new Config("uppsala", "pokeraidbot_test"));

            // External user's servers
            configRepository.save(new Config("luleå", "pokémon luleå"));
            configRepository.save(new Config("ängelholm", "test pokemongo ängelholm"));
            configRepository.save(new Config("norrköping", true, "raid-test-nkpg"));
            configRepository.save(new Config("norrköping", true, "raid - pokemon go norrköping"));
        }
    }

    public static GymRepository getGymRepositoryForConfig(LocaleService localeService, ConfigRepository configRepository) {
        Map<String, Set<Gym>> gymsPerRegion = new HashMap<>();
        final Map<String, Config> configMap = configRepository.getAllConfig();
        System.out.println("Config has following servers: " + configMap.keySet());
        for (String server : configMap.keySet()) {
            final Config config = configRepository.getConfigForServer(server);
            final String region = config.getRegion();
            if (!gymsPerRegion.containsKey(region)) {
                final Set<Gym> gymsInRegion = new CSVGymDataReader("/gyms_" + region + ".csv").readAll();
                gymsPerRegion.put(region, gymsInRegion);
                System.out.println("Loaded " + gymsInRegion.size() + " gyms for server " + server + ".");
            }
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
}
