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
    public GymRepository getGymRepository(LocaleService localeService) {
        return new GymRepository(new CSVGymDataReader("/gyms_uppsala.csv").readAll(), localeService);
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
