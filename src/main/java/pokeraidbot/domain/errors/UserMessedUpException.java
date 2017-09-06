package pokeraidbot.domain.errors;

public class UserMessedUpException extends RuntimeException {
    public UserMessedUpException(String userName, String message) {
        super(userName + ": " + message);
    }
}
