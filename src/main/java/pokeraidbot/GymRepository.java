package pokeraidbot;

import pokeraidbot.domain.Gym;
import pokeraidbot.domain.errors.GymNotFoundException;
import pokeraidbot.domain.Gyms;

import java.util.HashMap;
import java.util.Map;

public class GymRepository {
    private Map<String, Gym> gyms = new HashMap<>();
    public GymRepository() {
        gyms.put(Gyms.HÄSTEN.getName(), Gyms.HÄSTEN);
    }

    public Gym findByName(String name) {
        final Gym gym = gyms.get(name.toUpperCase());
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
}
