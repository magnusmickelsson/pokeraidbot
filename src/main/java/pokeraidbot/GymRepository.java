package pokeraidbot;

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
        final Gym gym = get(query);
        if (gym != null) {
            return gym;
        } else {
            Set<String> candidates = new HashSet<>();
            for (String gymName : gyms.keySet()) {
                if (gymName.toLowerCase().contains(query.toLowerCase())) {
                    candidates.add(gymName);
                }
            }
            if (candidates.size() == 1) {
                return findByName(candidates.iterator().next());
            } else if (candidates.size() < 1) {
                throw new GymNotFoundException(query);
            } else {
                throw new UserMessedUpException(userName, "Could not find one unique gym/pokestop. Did you want any of these? " + candidates);
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
