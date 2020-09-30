package pokeraidbot;

import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.mockito.Mockito;
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
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.tracking.TrackingService;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"pokeraidbot"})
public class TestServerMain {
    @Autowired
    ServerConfigRepository serverConfigRepository;

    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        SpringApplication.run(pokeraidbot.TestServerMain.class, args);
    }

    @Bean
    public LocaleService getLocaleService() {
        // Emulate no user configuration, which means server config is always used unless explicitly specified in a test
        UserConfigRepository userConfigRepository = Mockito.mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        return new LocaleService("sv", userConfigRepository);
    }

    @Bean
    public GymRepository getGymRepository(LocaleService localeService, ServerConfigRepository serverConfigRepository) {
        Map<String, Config> configMap = serverConfigRepository.getAllConfig();
        return getGymRepositoryForConfig(localeService, serverConfigRepository);
    }

    @PostConstruct
    @Transactional
    public void initializeConfig() {
        if (serverConfigRepository.findAll().size() == 0) {
            // My test servers
            serverConfigRepository.save(new Config("uppsala", "zhorhn tests stuff"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_lab"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_lab2"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_stage"));
            serverConfigRepository.save(new Config("uppsala", "pokeraidbot_test"));

            // External user's servers
            serverConfigRepository.save(new Config("luleå", "pokémon luleå"));
            serverConfigRepository.save(new Config("ängelholm", "test pokemongo ängelholm"));
            serverConfigRepository.save(new Config("norrköping", true, "raid-test-nkpg"));
            serverConfigRepository.save(new Config("norrköping", true, "raid - pokemon go norrköping"));
        }
    }

    public static GymRepository getGymRepositoryForConfig(LocaleService localeService, ServerConfigRepository serverConfigRepository) {
        Map<String, Set<Gym>> gymsPerRegion = new HashMap<>();
        final Map<String, Config> configMap = serverConfigRepository.getAllConfig();
        System.out.println("Config has following servers: " + configMap.keySet());
        for (String server : configMap.keySet()) {
            final Config config = serverConfigRepository.getConfigForServer(server);
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
    public TrackingService getTrackingService(LocaleService localeService,
                                              UserConfigRepository userConfigRepository,
                                              PokemonRepository pokemonRepository) {
        return new TrackingService(localeService, userConfigRepository,
                pokemonRepository);
    }

    @Bean
    public PokemonRepository getPokemonRepository(LocaleService localeService) {
        return new PokemonRepository("/pokemons.csv", localeService);
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
                                            ClockService clockService, TrackingService trackingService) {
        return new RaidRepository(clockService, localeService, entityRepository, pokemonRepository, gymRepository,
                trackingService);
    }
}
