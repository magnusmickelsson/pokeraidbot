package main;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.Config;
import pokeraidbot.domain.config.ConfigRepository;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRaidStrategyService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.RaidEntityRepository;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = "pokeraidbot.infrastructure.jpa")
@EnableJpaRepositories(basePackages = "pokeraidbot.infrastructure.jpa")
@ComponentScan(basePackages = {"pokeraidbot"})
@EnableTransactionManagement
public class BotServerMain {
    @Value("${ownerId}")
    private String ownerId;
    @Value("${token}")
    private String token;

    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        SpringApplication.run(BotServerMain.class, args);
    }

    @Bean
    public LocaleService getLocaleService() {
        return new LocaleService();
    }

    @Bean
    public ClockService getClockService() {
        return new ClockService();
    }

    @Bean
    public BotService getBotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                                    PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                                    ConfigRepository configRepository) {
        return new BotService(localeService, gymRepository, raidRepository, pokemonRepository, raidInfoService,
                configRepository, ownerId, token);
    }

    @Bean
    public GymRepository getGymRepository(LocaleService localeService, ConfigRepository configRepository) {
        Map<String, Config> configMap = configRepository.getAllConfig();
        Map<String, Set<Gym>> gymsPerRegion = new HashMap<>();
        System.out.println("Config has following servers: " + configMap.keySet());
        for (String server : configMap.keySet()) {
            final Config config = configRepository.getConfigForServer(server);
            final Set<Gym> existingGyms = gymsPerRegion.get(config.region);
            if (existingGyms == null) {
                final Set<Gym> gymsInRegion = new CSVGymDataReader("/gyms_" + config.region + ".csv").readAll();
                gymsPerRegion.put(config.region, gymsInRegion);
                System.out.println("Loaded " + gymsInRegion.size() + " gyms for region " + config.region + ".");
            }
        }
        return new GymRepository(gymsPerRegion, localeService);
    }

    @Bean
    public RaidRepository getRaidRepository(LocaleService localeService, RaidEntityRepository entityRepository,
                                            PokemonRepository pokemonRepository, GymRepository gymRepository,
                                            ClockService clockService) {
        return new RaidRepository(clockService, localeService, entityRepository, pokemonRepository, gymRepository);
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
    public ConfigRepository getConfigRepository() {
        final HashMap<String, Config> configurationMap = new HashMap<>();
        // My test servers
        configurationMap.put("zhorhn tests stuff", new Config("uppsala"));
        configurationMap.put("pokeraidbot_lab", new Config("uppsala"));
        configurationMap.put("pokeraidbot_lab2", new Config("uppsala"));
        configurationMap.put("pokeraidbot_stage", new Config("stockholm"));
        configurationMap.put("pokeraidbot_test", new Config("uppsala")); //, true));

        // External user's servers
        configurationMap.put("pokémon luleå", new Config("luleå"));
        configurationMap.put("test pokemongo ängelholm", new Config("ängelholm"));
        configurationMap.put("raid-test-nkpg", new Config("norrköping", true));
        configurationMap.put("raid - pokemon go norrköping", new Config("norrköping", true));

        return new ConfigRepository(configurationMap);
    }
}
