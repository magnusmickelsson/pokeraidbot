package main;

import net.dv8tion.jda.api.exceptions.RateLimitedException;
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
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.PokemonRaidStrategyService;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.tracking.TrackingService;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main Spring Boot application class
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = "pokeraidbot.infrastructure.jpa")
@EnableJpaRepositories(basePackages = "pokeraidbot.infrastructure.jpa")
@ComponentScan(basePackages = {"pokeraidbot"})
@EnableTransactionManagement
public class BotServerMain {
    public static String BOT_CREATOR_USERID = "199969842021793792";
    @Value("${ownerId}")
    private String ownerId;
    @Value("${token}")
    private String token;

    public static final String version = "1.9.0"; // todo: should be filter copied from pom.xml
    public static final int timeToRemoveFeedbackInSeconds = 30; // todo: should be setting?

    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        SpringApplication.run(BotServerMain.class, args);
    }

    @Bean
    public LocaleService getLocaleService(UserConfigRepository userConfigRepository, @Value("${locale:sv}")String locale) {
        return new LocaleService(locale, userConfigRepository);
    }

    @Bean
    public ClockService getClockService() {
        final ClockService clockService = new ClockService();
        // If you want to test, and it's currently in the "dead time" where raids can't be created, set time manually like this
        //clockService.setMockTime(LocalTime.of(9, 0));
        Utils.setClockService(clockService);
        return clockService;
    }

    @Bean
    public TrackingService getTrackingService(LocaleService localeService,
                                              UserConfigRepository userConfigRepository,
                                              PokemonRepository pokemonRepository) {
        return new TrackingService(localeService, userConfigRepository,
                pokemonRepository);
    }

    @Bean
    public BotService getBotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                                    PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                                    ServerConfigRepository serverConfigRepository,
                                    UserConfigRepository userConfigRepository, ClockService clockService,
                                    ExecutorService executorService, TrackingService trackingService) {
        return new BotService(localeService, gymRepository, raidRepository, pokemonRepository, raidInfoService,
                serverConfigRepository, userConfigRepository, executorService, clockService, trackingService, ownerId, token);
    }

    @Bean
    public ExecutorService getExecutorService() {
        return new ThreadPoolExecutor(100, Integer.MAX_VALUE,
                65L, TimeUnit.SECONDS,
                new LinkedTransferQueue<>());
    }

    @Bean
    public GymRepository getGymRepository(LocaleService localeService, ServerConfigRepository serverConfigRepository) {
        return new GymRepository(serverConfigRepository, localeService);
    }

    @Bean
    public RaidRepository getRaidRepository(LocaleService localeService, RaidEntityRepository entityRepository,
                                            PokemonRepository pokemonRepository, GymRepository gymRepository,
                                            ClockService clockService, TrackingService trackingService) {
        return new RaidRepository(clockService, localeService, entityRepository, pokemonRepository, gymRepository,
                trackingService);
    }

    @Bean
    public PokemonRepository getPokemonRepository(LocaleService localeService) {
        return new PokemonRepository("/pokemons.csv", localeService);
    }

    @Bean
    public PokemonRaidStrategyService getRaidInfoService(PokemonRepository pokemonRepository) {
        return new PokemonRaidStrategyService(pokemonRepository);
    }
}
