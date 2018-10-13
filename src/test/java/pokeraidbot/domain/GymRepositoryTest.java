package pokeraidbot.domain;

import net.dv8tion.jda.core.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pokeraidbot.TestServerMain;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.GymNotFoundException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;
import pokeraidbot.infrastructure.jpa.config.UserConfigRepository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GymRepositoryTest {
    public static final ServerConfigRepository SERVER_CONFIG_REPOSITORY = mock(ServerConfigRepository.class);
    GymRepository repo;
    private final Gym gym = new Gym("Hästen", "3690325", "59.844542", "17.63993",
            "Uppsala");
    private LocaleService localeService;
    private Map<String, Config> configMap;

    @Before
    public void setUp() throws Exception {
        UserConfigRepository userConfigRepository = mock(UserConfigRepository.class);
        when(userConfigRepository.findOne(any(String.class))).thenReturn(null);
        localeService = new LocaleService("sv", userConfigRepository);
        final Config staffordConfig = new Config("stafford uk", "stafford uk");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("stafford uk")).thenReturn(staffordConfig);
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
        final Config vindelnConfig = new Config("vindeln", "vindeln");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("vindeln")).thenReturn(vindelnConfig);
        final Config lyckseleConfig = new Config("lycksele", "lycksele");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("lycksele")).thenReturn(lyckseleConfig);
        final Config norrkopingConfig = new Config("norrköping", "norrköping");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("norrköping")).thenReturn(norrkopingConfig);
        final Config trollhattanConfig = new Config("trollhättan", "trollhättan");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("trollhättan")).thenReturn(trollhattanConfig);
        final Config helsingborgConfig = new Config("helsingborg", "helsingborg");
        when(SERVER_CONFIG_REPOSITORY.getConfigForServer("helsingborg")).thenReturn(helsingborgConfig);
        configMap = new HashMap<>();
        configMap.put("stafford uk", staffordConfig);
        configMap.put("dalarna", dalarnaConfig);
        configMap.put("uppsala", uppsalaConfig);
        configMap.put("ängelholm", angelholmConfig);
        configMap.put("luleå", luleConfig);
        configMap.put("umeå", umeConfig);
        configMap.put("vännäs", vannasConfig);
        configMap.put("vindeln", vindelnConfig);
        configMap.put("norrköping", norrkopingConfig);
        configMap.put("trollhättan", trollhattanConfig);
        configMap.put("helsingborg", helsingborgConfig);
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
    public void allGymsAreReadForStafford() {
        assertThat(repo.getAllGymsForRegion("stafford uk").size(), is(148));
    }

    @Test
    public void allGymsAreReadForDalarna() {
        final Set<Gym> dalarnaGyms = repo.getAllGymsForRegion("dalarna");
        assertThat(dalarnaGyms.size(), is(115));
        assertThat(dalarnaGyms.iterator().next().isInArea("rättvik"), is(true));
    }

    @Test
    public void allGymsAreReadForLycksele() {
        assertThat(repo.getAllGymsForRegion("lycksele").size(), is(11));
    }

    @Test
    public void allGymsAreReadForUppsala() {
        assertThat(repo.getAllGymsForRegion("uppsala").size(), is(232));
    }

    @Test
    public void allGymsAreReadForAngelholm() {
        assertThat(repo.getAllGymsForRegion("ängelholm").size(), is(32));
    }

    @Test
    public void allGymsAreReadForLulea() {
        assertThat(repo.getAllGymsForRegion("luleå").size(), is(93));
    }

    @Test
    public void allGymsAreReadForUmea() {
        assertThat(repo.getAllGymsForRegion("umeå").size(), is(153));
    }

    @Test
    public void allGymsAreReadForTrollhattan() {
        assertThat(repo.getAllGymsForRegion("trollhättan").size(), is(80));
    }

    @Test
    public void allGymsAreReadForVindeln() {
        assertThat(repo.getAllGymsForRegion("vindeln").size(), is(6));
    }

    @Test
    public void allGymsAreReadForVannas() {
        assertThat(repo.getAllGymsForRegion("vännäs").size(), is(14));
    }

    @Test
    public void allGymsAreReadForNorrkoping() {
        assertThat(repo.getAllGymsForRegion("norrköping").size(), is(104));
    }

    @Test
    public void allGymsAreReadForHelsingborg() {
        assertThat(repo.getAllGymsForRegion("helsingborg").size(), is(219));
    }

    @Test
    public void findGymByName() throws Exception {
        assertThat(repo.findByName("Hästen", "uppsala"), is(gym));
    }

    @Test
    public void findNonExGym() throws Exception {
        final Gym gym = repo.findByName("Sköldpaddorna", "norrköping");
        assertThat(gym.isExGym(), is(false));
        assertThat(gym.getName(), is("Sköldpaddorna"));
    }

    @Test
    public void feather360IsExGym() throws Exception {
        final Gym feather360 = repo.findByName("Feather Sculpture 360", "uppsala");
        assertThat(feather360.isInArea("Uppsala"), is(true));
        assertThat(feather360.isExGym(), is(true));
    }

    @Test
    public void malakIsExGym() throws Exception {
        final Gym gym = repo.findByName("Malak", "vännäs");
        assertThat(gym.isInArea("Vännäs"), is(true));
        assertThat(gym.isExGym(), is(true));
    }

    @Test
    public void findGymByFuzzySearch() throws Exception {
        User user = mock(User.class);
        when(user.getName()).thenReturn("Greger");
        assertThat(repo.search(user,"hosten", "uppsala"), is(gym));
    }
    
    @Test
    public void findGymById() throws Exception {
        assertThat(repo.findById("3690325", "uppsala"), is(gym));
    }

    @Test
    public void findNewGymInUppsala() throws Exception {
        final Gym u969 = repo.findByName("U969", "uppsala");
        assertThat(u969.getName(), is("U969"));
        assertThat(u969.isExGym(), is(false));
    }

    @Test
    public void addTemporaryGymToUppsala() {
        Gym gym;
        try {
            gym = repo.findByName("Mongo", "uppsala");
            fail("Gym should not exist yet.");
        } catch (GymNotFoundException e) {
            // Expected
        }

        final User userMock = mock(User.class);
        when(userMock.getName()).thenReturn("User");
        repo.addTemporary(userMock, new Gym("Mongo", "66666666", "50.0001", "25.00001",
                "Uppsala", false), "uppsala");

        gym = repo.findByName("Mongo", "uppsala");
        assertThat(gym.getName(), is("Mongo"));
        assertThat(gym.isExGym(), is(false));
    }

    @Test
    public void checkThatAllExGymsAreInGymRepos() {
        for (String region : configMap.keySet()) {
            assertAllExGymsInRegion(region);
        }
    }

    private void assertAllExGymsInRegion(String region) {
        Set<String> exGymNamesForRegion;
        final String fileName = "/gyms_" + region.toLowerCase() + ".csv.ex.txt";
        final InputStream inputStreamEx = GymRepositoryTest.class.getResourceAsStream(fileName);
        exGymNamesForRegion = CSVGymDataReader.readExGymListIfExists(inputStreamEx, fileName);

        for (String gymName : exGymNamesForRegion) {
            final Gym gym = repo.findByName(gymName, region);
            assertThat(gym != null, is(true));
            assertThat(gym.getName(), is(gymName));
        }
    }
}
