package pokeraidbot;

import org.apache.commons.lang3.StringUtils;
import pokeraidbot.domain.Gym;
import pokeraidbot.domain.errors.GymNotFoundException;
import pokeraidbot.domain.errors.UserMessedUpException;

import java.util.*;

public class GymRepository {
    private Map<String, Gym> gyms = new HashMap<>();

    public GymRepository(Set<Gym> gyms) {
        for (Gym gym : gyms) {
            this.gyms.put(prepareNameForFuzzySearch(gym.getName()), gym);
        }
    }

    public static String prepareNameForFuzzySearch(String name) {
        return name.toUpperCase().replaceAll("Ä", "A").replaceAll("Ö", "O").replaceAll("Ä", "A").replaceAll("É", "E");
        // todo: add more replacements
    }

    public Gym search(String userName, String query) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(query)) {
            throw new UserMessedUpException(userName, "Empty input for gym name, try giving me a proper name to search for. :(");
        }
        final String queryFuzzySearch = prepareNameForFuzzySearch(query);
        final Gym gym = get(query);
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
                return findByName(candidates.iterator().next());
            } else if (candidates.size() < 1) {
                throw new GymNotFoundException(query);
            } else {
                if (candidates.size() < 5) {
                    throw new UserMessedUpException(userName, "Could not find one unique gym/pokestop. Did you want any of these? " + candidates);
                } else {
                    throw new UserMessedUpException(userName, "Could not find one unique gym/pokestop, your query returned 5+ results. Try refine your search.");
                }
            }
        }
    }

    public Gym findByName(String name) {
        final Gym gym = get(name);
        if (gym == null) {
            throw new GymNotFoundException(name);
        }
        return gym;
    }

    private Gym get(String name) {
        return gyms.get(prepareNameForFuzzySearch(name));
    }

    public Gym findById(String id) {
        for (Gym gym : gyms.values()) {
            if (gym.getId().equals(id))
                return gym;
        }
        throw new GymNotFoundException();
    }

    public Collection<Gym> getAllGyms() {
        return Collections.unmodifiableCollection(gyms.values());
    }
}
