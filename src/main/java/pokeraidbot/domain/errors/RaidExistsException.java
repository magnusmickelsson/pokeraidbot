package pokeraidbot.domain.errors;

import pokeraidbot.domain.Raid;

public class RaidExistsException extends RuntimeException {
    public RaidExistsException(String raidCreatorName, Raid raid) {
        super("Sorry, " + raidCreatorName + ", a raid at gym " +
                raid.getGym().getName() + " already exists (for " + raid.getPokemon().getName() + "). Sign up for it!");
    }
}
