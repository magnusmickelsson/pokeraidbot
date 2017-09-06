package pokeraidbot.domain.errors;

public class GymNotFoundException extends RuntimeException {
    public GymNotFoundException() {
        super("Could not find a Gym from your input. Please try again.");
    }

    public GymNotFoundException(String name) {
        super("Could not find Gym with name \"" + name + "\"");
    }
}
