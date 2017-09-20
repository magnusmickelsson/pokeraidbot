package pokeraidbot.domain;

import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.errors.GymNotFoundException;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.util.*;

public class GymRepository {
    private Map<String, Map<String, Gym>> gymsPerRegion = new HashMap<>();
//    private Map<String, Gym> gyms = new HashMap<>();
    private final LocaleService localeService;

    public GymRepository(Map<String, Set<Gym>> gyms, LocaleService localeService) {
        this.localeService = localeService;
        for (String region : gyms.keySet()) {
            Map<String, Gym> gymsForRegion = new HashMap<>();
            for (Gym gym : gyms.get(region)) {
                String gymName = prepareNameForFuzzySearch(gym.getName());
                if (gymsForRegion.get(gymName) != null) {
                    throw new RuntimeException("There are duplicate gymnames in the data for region " + region + ": \"" +
                            gym.getName() + "\"! Fix this manually as you want it (you can't have good name searching without it)!");
                }
                gymsForRegion.put(gymName, gym);
            }
            this.gymsPerRegion.put(region, gymsForRegion);
        }
    }

    public static String prepareNameForFuzzySearch(String name) {
        return name.toUpperCase().replaceAll("Ä", "A").replaceAll("Ö", "O").replaceAll("Ä", "A").replaceAll("É", "E");
        // todo: add more replacements?
    }

    public Gym search(String userName, String query, String region) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(query)) {
            throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.GYM_SEARCH,
                    LocaleService.DEFAULT));
        }

        final Map<String, Gym> gyms = getGymMapForRegion(region);

        final Locale localeForUser = localeService.getLocaleForUser(userName);
        final String queryFuzzySearch = prepareNameForFuzzySearch(query);
        final Gym gym = get(query, region);
        if (gym != null) {
            return gym;
        } else {
            Set<String> candidates = new HashSet<>();
            for (String gymName : gyms.keySet()) {
                String fuzzyGymName = prepareNameForFuzzySearch(gymName);
                if (fuzzyGymName.contains(queryFuzzySearch)) {
                    candidates.add(gyms.get(gymName).getName());
                }
            }
            if (candidates.size() == 1) {
                return findByName(candidates.iterator().next(), region);
            } else if (candidates.size() < 1) {
                throw new GymNotFoundException(query, localeService, LocaleService.SWEDISH);
            } else {
                if (candidates.size() < 5) {
                    throw new UserMessedUpException(userName,
                            localeService.getMessageFor(LocaleService.GYM_SEARCH_OPTIONS, localeForUser, String.valueOf(candidates)));
                } else {
                    throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.GYM_SEARCH_MANY_RESULTS, localeForUser));
                }
            }
        }
    }

    public Gym findByName(String name, String region) {
        final Gym gym = get(name, region);
        if (gym == null) {
            throw new GymNotFoundException(name, localeService, LocaleService.SWEDISH);
        }
        return gym;
    }

    private Gym get(String name, String region) {
        return getGymMapForRegion(region).get(prepareNameForFuzzySearch(name));
    }

    private Map<String, Gym> getGymMapForRegion(String region) {
        final Map<String, Gym> gyms = gymsPerRegion.get(region);
        if (gyms == null || gyms.size() < 1) {
            throw new RuntimeException(localeService.getMessageFor(LocaleService.GYM_CONFIG_ERROR, LocaleService.DEFAULT));
        }
        return gyms;
    }

    public Gym findById(String id, String region) {
        for (Gym gym : getGymMapForRegion(region).values()) {
            if (gym.getId().equals(id))
                return gym;
        }
        throw new GymNotFoundException("[No entry]", localeService, LocaleService.SWEDISH);
    }

    public Collection<Gym> getAllGymsForRegion(String region) {
        return Collections.unmodifiableCollection(getGymMapForRegion(region).values());
    }

    public Collection<Gym> getAllUniqueGyms() {
        Set<Gym> gyms = new HashSet<>();
        gymsPerRegion.values().stream().map(e -> gyms.addAll(e.values()));
        return Collections.unmodifiableSet(gyms);
    }
}
