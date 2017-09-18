package pokeraidbot;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pokeraidbot.domain.*;
import pokeraidbot.infrastructure.CSVGymDataReader;

import javax.security.auth.login.LoginException;
import java.io.IOException;

@SpringBootApplication
@EnableAutoConfiguration
public class BotServerMain {
    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        SpringApplication.run(BotServerMain.class, args);
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
    public RaidRepository getRaidRepository(LocaleService localeService) {
        return new RaidRepository(new ClockService(), localeService);
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
