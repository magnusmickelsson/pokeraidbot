package pokeraidbot.domain.errors;

import pokeraidbot.domain.Raid;

public class RaidExistsException extends RuntimeException {
    public RaidExistsException(String raidCreatorName, Raid raid) {
        super("Sorry, " + raidCreatorName + ", the raid for " + raid.getPokemon().getName() + " at gym " +
                raid.getGym().getName() + " already exists. Try signing up for it!");
    }
}
