package pokeraidbot.domain.errors;

import pokeraidbot.domain.config.LocaleService;

public class WrongNumberOfArgumentsException extends UserMessedUpException {
    public WrongNumberOfArgumentsException(String userName, LocaleService localeService, int expectedArguments,
                                           int actualArguments, String helpTextForCommand) {
        super(userName, localeService.getMessageFor(LocaleService.WRONG_NUMBER_OF_ARGUMENTS,
                localeService.getLocaleForUser(userName),
                "" + expectedArguments,
                "" + actualArguments) + "\n\nHjälptext:\n" + helpTextForCommand);
    }
}
