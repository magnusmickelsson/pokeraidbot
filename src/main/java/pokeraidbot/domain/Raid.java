package pokeraidbot.domain;

import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static pokeraidbot.Utils.printTime;

public class Raid {
    private final Pokemon pokemon;
    private final LocalTime endOfRaid;
    private final Gym gym;
    private final Map<String, SignUp> signUps = new ConcurrentHashMap<>();

    public Raid(Pokemon pokemon, LocalTime endOfRaid, Gym gym) {
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public LocalTime getEndOfRaid() {
        return endOfRaid;
    }

    public Gym getGym() {
        return gym;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Raid)) return false;

        Raid raid = (Raid) o;

        if (pokemon != null ? !pokemon.equals(raid.pokemon) : raid.pokemon != null) return false;
        if (endOfRaid != null ? !endOfRaid.equals(raid.endOfRaid) : raid.endOfRaid != null) return false;
        if (gym != null ? !gym.equals(raid.gym) : raid.gym != null) return false;
        return signUps != null ? signUps.equals(raid.signUps) : raid.signUps == null;
    }

    @Override
    public int hashCode() {
        int result = pokemon != null ? pokemon.hashCode() : 0;
        result = 31 * result + (endOfRaid != null ? endOfRaid.hashCode() : 0);
        result = 31 * result + (gym != null ? gym.hashCode() : 0);
        result = 31 * result + (signUps != null ? signUps.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Raid for " + pokemon + " at gym " + gym + ", ends at " + printTime(endOfRaid);
    }

    public void signUp(String userName, int howManyPeople, LocalTime arrivalTime) {
        final SignUp signUp = signUps.get(userName);
        if (signUp != null) {
            throw new UserMessedUpException(userName, "You're already signed up for: " + this + " ... " +
                    signUp + " - remove your current signup then signup again.");
        }
        signUps.put(userName, new SignUp(userName, howManyPeople, arrivalTime));
    }

    public Set<SignUp> getSignUps() {
        return Collections.unmodifiableSet(new HashSet<>(signUps.values()));
    }

    public int getNumberOfPeopleSignedUp() {
        return signUps.values().stream().mapToInt(signup -> signup.getHowManyPeople()).sum();
    }

    public SignUp remove(String userName) {
        final SignUp removed = signUps.remove(userName);
        return removed;
    }
}
