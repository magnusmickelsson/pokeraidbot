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
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRaidStrategyService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.LocalTime;

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
        final ClockService clockService = new ClockService();
        // If you want to test, and it's currently in the "dead time" where raids can't be created, set time manually like this
        clockService.setMockTime(LocalTime.of(9, 0));
        Utils.setClockService(clockService);
        return clockService;
    }

    @Bean
    public BotService getBotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                                    PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                                    ConfigRepository configRepository, ClockService clockService) {
        return new BotService(localeService, gymRepository, raidRepository, pokemonRepository, raidInfoService,
                configRepository, clockService, ownerId, token);
    }

    @Bean
    public GymRepository getGymRepository(LocaleService localeService, ConfigRepository configRepository) {
        return new GymRepository(configRepository, localeService);
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
}
