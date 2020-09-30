package pokeraidbot.domain.gym;

import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.commands.PotentialExRaidListCommand;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.GymNotFoundException;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static me.xdrop.fuzzywuzzy.FuzzySearch.extractTop;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class GymRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymRepository.class);

    private Map<String, Set<Gym>> gymsPerRegion = new ConcurrentHashMap<>();
    private Map<String, Set<String>> exGymsPerRegion = new ConcurrentHashMap<>();
    private final ServerConfigRepository serverConfigRepository;
    private final LocaleService localeService;

    public GymRepository(ServerConfigRepository serverConfigRepository, LocaleService localeService) {
        this.serverConfigRepository = serverConfigRepository;
        this.localeService = localeService;
    }

    // Only used for testing!
    public GymRepository(Map<String, Set<Gym>> gyms, LocaleService localeService) {
        this.localeService = localeService;
        this.serverConfigRepository = null;
        for (String region : gyms.keySet()) {
            Set<Gym> gymsForRegion = gyms.get(region);
            this.gymsPerRegion.put(region, gymsForRegion);
        }
    }

    public void reloadGymData() {
        if (serverConfigRepository != null) {
            Map<String, Config> configMap = serverConfigRepository.getAllConfig();
            Map<String, Set<Gym>> gymsPerRegion = new HashMap<>();
            LOGGER.info("Config has following servers: " + configMap.keySet());
            for (String server : configMap.keySet()) {
                final Config config = serverConfigRepository.getConfigForServer(server);
                final String region = config.getRegion();
                final Set<Gym> existingGyms = gymsPerRegion.get(region);
                if (existingGyms == null) {
                    try {
                        final Set<Gym> gymsInRegion = new CSVGymDataReader("/gyms_" + region + ".csv").readAll();
                        gymsPerRegion.put(region, gymsInRegion);
                        LOGGER.info("Loaded " + gymsInRegion.size() + " gyms for region " + region + ".");
                    } catch (Throwable t) {
                        LOGGER.warn("Could not load data for region " + region + ", skipping.");
                    }
                }
            }

            for (String region : gymsPerRegion.keySet()) {
                Set<Gym> gymsForRegion = gymsPerRegion.get(region);
                this.gymsPerRegion.put(region, gymsForRegion);
            }
        }
    }

    public Gym search(User user, String query, String region) {
        if (region == null || StringUtils.isEmpty(query)) {
            throw new UserMessedUpException(user, localeService.getMessageFor(LocaleService.GYM_SEARCH,
                    LocaleService.DEFAULT));
        }

        final Set<Gym> gyms = getAllGymsForRegion(region);

        final Locale localeForUser = localeService.getLocaleForUser(user);
        final Optional<Gym> gym = get(query, region);
        if (gym.isPresent()) {
            return gym.get();
        } else {
            //70 seems like a reasonable cutoff here...
            List<ExtractedResult> candidates = extractTop(query, gyms.stream().map(
                    s -> s.getName()).collect(Collectors.toList()), 6, 70);
            if (candidates.size() == 1) {
                return findByName(candidates.iterator().next().getString(), region);
            } else if (candidates.size() < 1) {
                throw new GymNotFoundException(query, localeService, LocaleService.SWEDISH, region);
            } else {
                List<Gym> matchingPartial = getMatchingPartial(query, region, candidates);
                if (matchingPartial.size() == 1) {
                    return matchingPartial.get(0);
                }
                if (candidates.size() <= 5) {
                    String possibleMatches = candidates.stream().map(s -> findByName(s.getString(), region)
                            .getName()).collect(Collectors.joining(", "));
                    throw new UserMessedUpException(user,
                            localeService.getMessageFor(LocaleService.GYM_SEARCH_OPTIONS, localeForUser,
                                    possibleMatches));
                } else {
                    throw new UserMessedUpException(user,
                            localeService.getMessageFor(LocaleService.GYM_SEARCH_MANY_RESULTS, localeForUser));
                }
            }
        }
    }

    public Gym findByName(String name, String region) {
        final Optional<Gym> gym = get(name, region);
        if (!gym.isPresent()) {
            throw new GymNotFoundException(name, localeService, LocaleService.SWEDISH, region);
        }
        return gym.get();
    }

    // Temporary method to work around problem with gym data updates from the map site - will be removed when we move
    // gym data to database and do an administrative UI to maintain them
    public void addTemporary(User user, Gym gym, String region) {
        if (get(gym.getName(), region).isPresent()) {
            throw new UserMessedUpException(user, localeService.getMessageFor(
                    LocaleService.COULD_NOT_ADD_GYM,
                    localeService.getLocaleForUser(user)));
        }
        final Set<Gym> gymsForRegion = new HashSet<>(gymsPerRegion.get(region));
        gymsForRegion.add(gym);
        gymsPerRegion.put(region, gymsForRegion);
    }

    public Gym findById(String id, String region) {
        for (Gym gym : getAllGymsForRegion(region)) {
            if (gym.getId().equals(id))
                return gym;
        }
        throw new GymNotFoundException("[No entry]", localeService, LocaleService.SWEDISH, region);
    }

    public Set<Gym> getAllGymsForRegion(String region) {
        final Set<Gym> gyms = gymsPerRegion.get(region);
        if (gyms == null || gyms.size() < 1) {
            throw new RuntimeException(localeService.getMessageFor(LocaleService.GYM_CONFIG_ERROR, LocaleService.DEFAULT));
        }
        return gyms;
    }

    private Optional<Gym> get(String name, String region) {
        return getAllGymsForRegion(region).stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
    }

    private List<Gym> getMatchingPartial(String query, String region, List<ExtractedResult> candidates) {
        String cleanQuery = query.trim().replaceAll(" +", " ");
        List<Gym> mathingGyms = new ArrayList<>();
        for (ExtractedResult result : candidates) {
            if (containsIgnoreCase(result.getString(), cleanQuery)) {
                mathingGyms.add(findByName(result.getString(), region));
            }
        }
        return mathingGyms;
    }

    public Map<String, Set<Gym>> getAllGymData() {
        return Collections.unmodifiableMap(gymsPerRegion);
    }

    public Set<String> getExGyms(String region) {
        Set<String> exGyms = exGymsPerRegion.get(region);
        if (exGyms == null) {
            exGyms = loadExGyms(region);
            exGymsPerRegion.put(region, exGyms);
        }
        return exGyms;
    }

    private Set<String> loadExGyms(String region) {
        Set<String> exGymNamesForRegion;
        final String fileName = "/gyms_" + region.toLowerCase() + ".csv.ex.txt";
        final InputStream inputStreamEx = PotentialExRaidListCommand.class.getResourceAsStream(fileName);
        exGymNamesForRegion = CSVGymDataReader.readExGymListIfExists(inputStreamEx, fileName);
        return exGymNamesForRegion;
    }
}
