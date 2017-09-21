package pokeraidbot.domain;

import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.errors.GymNotFoundException;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.util.*;
import java.util.stream.Collectors;

import static me.xdrop.fuzzywuzzy.FuzzySearch.extractTop;

public class GymRepository {
    private Map<String, Set<Gym>> gymsPerRegion = new HashMap<>();
    private final LocaleService localeService;

    public GymRepository(Map<String, Set<Gym>> gyms, LocaleService localeService) {
        this.localeService = localeService;
        for (String region : gyms.keySet()) {
            Set<Gym> gymsForRegion = gyms.get(region);
            this.gymsPerRegion.put(region, gymsForRegion);
        }
    }

    public Gym search(String userName, String query, String region) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(query)) {
            throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.GYM_SEARCH,
                    LocaleService.DEFAULT));
        }

        final Set<Gym> gyms = getAllGymsForRegion(region);

        final Locale localeForUser = localeService.getLocaleForUser(userName);
        final Optional<Gym> gym = get(query, region);
        if (gym.isPresent()) {
            return gym.get();
        } else {
            //70 seems like a reasonable cutoff here...
            List<ExtractedResult> candidates = extractTop(query, gyms.stream().map(s -> s.getName()).collect(Collectors.toList()), 5, 70);
            if (candidates.size() == 1) {
                return findByName(candidates.iterator().next().getString(), region);
            } else if (candidates.size() < 1) {
                throw new GymNotFoundException(query, localeService, LocaleService.SWEDISH);
            } else {
                if (candidates.size() < 5) {
                    String possibleMatches = candidates.stream().map(s -> findByName(s.getString(), region).getName()).collect(Collectors.joining(", "));
                    throw new UserMessedUpException(userName,
                            localeService.getMessageFor(LocaleService.GYM_SEARCH_OPTIONS, localeForUser, possibleMatches));
                } else {
                    throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.GYM_SEARCH_MANY_RESULTS, localeForUser));
                }
            }
        }
    }

    public Gym findByName(String name, String region) {
        final Optional<Gym> gym = get(name, region);
        if (!gym.isPresent()) {
            throw new GymNotFoundException(name, localeService, LocaleService.SWEDISH);
        }
        return gym.get();
    }

    public Gym findById(String id, String region) {
        for (Gym gym : getAllGymsForRegion(region)) {
            if (gym.getId().equals(id))
                return gym;
        }
        throw new GymNotFoundException("[No entry]", localeService, LocaleService.SWEDISH);
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
}
