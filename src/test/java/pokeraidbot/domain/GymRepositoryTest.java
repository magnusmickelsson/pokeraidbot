package pokeraidbot.domain;

import net.dv8tion.jda.core.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.TestServerMain;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class GymRepositoryTest {
    public static final ServerConfigRepository SERVER_CONFIG_REPOSITORY = Mockito.mock(ServerConfigRepository.class);
    GymRepository repo;
    private final Gym gym = new Gym("Hästen", "3690325", "59.844542", "17.63993",
            "Uppsala");
    private LocaleService localeService;

    @Before
    public void setUp() throws Exception {
        UserConfigRepository userConfigRepository = Mockito.mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        localeService = new LocaleService("sv", userConfigRepository);
        final Config dalarnaConfig = new Config("dalarna", "dalarna");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("dalarna")).thenReturn(dalarnaConfig);
        final Config uppsalaConfig = new Config("uppsala", "uppsala");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("uppsala")).thenReturn(uppsalaConfig);
        final Config angelholmConfig = new Config("ängelholm", "ängelholm");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("ängelholm")).thenReturn(angelholmConfig);
        final Config luleConfig = new Config("luleå", "luleå");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("luleå")).thenReturn(luleConfig);
        final Config umeConfig = new Config("umeå", "umeå");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("umeå")).thenReturn(umeConfig);
        final Config vannasConfig = new Config("vännäs", "vännäs");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("vännäs")).thenReturn(vannasConfig);
        final Config lyckseleConfig = new Config("lycksele", "lycksele");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("lycksele")).thenReturn(lyckseleConfig);
        final Config norrkopingConfig = new Config("norrköping", "norrköping");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("norrköping")).thenReturn(norrkopingConfig);
        final HashMap<String, Config> configMap = new HashMap<>();
        configMap.put("dalarna", dalarnaConfig);
        configMap.put("uppsala", uppsalaConfig);
        configMap.put("ängelholm", angelholmConfig);
        configMap.put("luleå", luleConfig);
        configMap.put("umeå", umeConfig);
        configMap.put("vännäs", vannasConfig);
        configMap.put("norrköping", norrkopingConfig);
        configMap.put("lycksele", lyckseleConfig);
        when(SERVER_CONFIG_REPOSITORY.getAllConfig()).thenReturn(configMap);
        repo = TestServerMain.getGymRepositoryForConfig(localeService, SERVER_CONFIG_REPOSITORY);
    }

    @Test
    public void noDuplicatesInDataFiles() {
        Map<String, Set<Gym>> gymData = repo.getAllGymData();
        for (String region : gymData.keySet()) {
            Set<Gym> gyms = gymData.get(region);
            for (Gym gym : gyms) {
                final Gym gymFromRepo = repo.findByName(gym.getName(), region);
                assertThat("Seems we have a duplicate for gymname: " + gym.getName() +
                        " \n" + gymFromRepo.toStringDetails() + " != " + gym.toStringDetails(), gymFromRepo, is(gym));
            }
        }
    }

    @Test
    public void allGymsAreReadForDalarna() {
        final Set<Gym> dalarnaGyms = repo.getAllGymsForRegion("dalarna");
        assertThat(dalarnaGyms.size(), is(115));
        assertThat(dalarnaGyms.iterator().next().isInArea("rättvik"), is(true));
    }

    @Test
    public void allGymsAreReadForLycksele() {
        assertThat(repo.getAllGymsForRegion("lycksele").size(), is(4));
    }

    @Test
    public void allGymsAreReadForUppsala() {
        assertThat(repo.getAllGymsForRegion("uppsala").size(), is(220));
    }

    @Test
    public void allGymsAreReadForAngelholm() {
        assertThat(repo.getAllGymsForRegion("ängelholm").size(), is(32));
    }

    @Test
    public void allGymsAreReadForLulea() {
        assertThat(repo.getAllGymsForRegion("luleå").size(), is(63));
    }

    @Test
    public void allGymsAreReadForUmea() {
        assertThat(repo.getAllGymsForRegion("umeå").size(), is(73));
    }

    @Test
    public void allGymsAreReadForVannas() {
        assertThat(repo.getAllGymsForRegion("vännäs").size(), is(9));
    }

    @Test
    public void allGymsAreReadForNorrkoping() {
        assertThat(repo.getAllGymsForRegion("norrköping").size(), is(98));
    }

    @Test
    public void findGymByName() throws Exception {
        assertThat(repo.findByName("Hästen", "uppsala"), is(gym));
    }

    @Test
    public void findGymByFuzzySearch() throws Exception {
        User user = Mockito.mock(User.class);
        when(user.getName()).thenReturn("Greger");
        assertThat(repo.search(user,"hosten", "uppsala"), is(gym));
    }
    
    @Test
    public void findGymById() throws Exception {
        assertThat(repo.findById("3690325", "uppsala"), is(gym));
    }

    @Test
    public void findNewGymInUppsala() throws Exception {
        assertThat(repo.findByName("U969", "uppsala").getName(), is("U969"));
    }
}
