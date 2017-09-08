package pokeraidbot.domain.errors;

import pokeraidbot.domain.Raid;

public class RaidExistsException extends RuntimeException {
    public RaidExistsException(String raidCreatorName, Raid existingRaid) {
        super("Sorry, " + raidCreatorName + ", a raid at gym " +
                existingRaid.getGym().getName() + " already exists (for " + existingRaid.getPokemon().getName() + "). Sign up for it!");
    }
}
