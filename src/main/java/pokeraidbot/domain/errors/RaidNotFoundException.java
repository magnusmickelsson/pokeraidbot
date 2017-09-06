package pokeraidbot.domain.errors;

import pokeraidbot.domain.Gym;

public class RaidNotFoundException extends RuntimeException {
    public RaidNotFoundException(Gym gym) {
        super("Could not find an active raid for this gym: " + gym.getName());
    }
}
