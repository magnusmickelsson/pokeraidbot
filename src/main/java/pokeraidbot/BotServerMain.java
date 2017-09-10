package pokeraidbot;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pokeraidbot.domain.*;
import pokeraidbot.infrastructure.CSVGymDataReader;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
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

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }
}
