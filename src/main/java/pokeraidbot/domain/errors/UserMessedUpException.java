package pokeraidbot.domain.errors;

import net.dv8tion.jda.api.entities.User;

public class UserMessedUpException extends RuntimeException {
    public UserMessedUpException(User user, String message) {
        super((user == null ? "" : (user.getName() + ": ")) + message);
    }

    public UserMessedUpException(String userName, String message) {
        super(userName + ": " + message);
    }
}
