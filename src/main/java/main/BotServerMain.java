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
import pokeraidbot.domain.tracking.TrackingCommandListener;
import pokeraidbot.domain.tracking.TrackingCommandListenerBean;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.*;

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
    public static final String version = "1.3.0-SNAPSHOT"; // todo: should be filter copied from pom.xml
    public static final int timeToRemoveFeedbackInSeconds = 20; // todo: should be setting?

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
//        clockService.setMockTime(LocalTime.of(9, 0));
        Utils.setClockService(clockService);
        return clockService;
    }

    @Bean
    public TrackingCommandListener getTrackingCommandListener(ServerConfigRepository serverConfigRepository,
                                                              LocaleService localeService,
                                                              UserConfigRepository userConfigRepository,
                                                              PokemonRepository pokemonRepository) {
        return new TrackingCommandListenerBean(serverConfigRepository, localeService, userConfigRepository,
                pokemonRepository);
    }

    @Bean
    public BotService getBotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                                    PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                                    ServerConfigRepository serverConfigRepository,
                                    UserConfigRepository userConfigRepository, ClockService clockService,
                                    TrackingCommandListener trackingCommandListener, ExecutorService executorService) {
        return new BotService(localeService, gymRepository, raidRepository, pokemonRepository, raidInfoService,
                serverConfigRepository, userConfigRepository, executorService, clockService, ownerId, token,
                trackingCommandListener);
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
