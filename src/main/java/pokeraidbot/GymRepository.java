package pokeraidbot;

import pokeraidbot.domain.Gym;
import pokeraidbot.domain.errors.GymNotFoundException;

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

    public Gym findByName(String name) {
        final Gym gym = gyms.get(prepareNameForFuzzySearch(name));
        if (gym == null) {
            throw new GymNotFoundException(name);
        }
        return gym;
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
