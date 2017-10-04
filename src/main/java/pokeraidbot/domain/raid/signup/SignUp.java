package pokeraidbot.domain.raid.signup;

import pokeraidbot.Utils;

import java.time.LocalTime;

import static pokeraidbot.Utils.HIGH_LIMIT_FOR_SIGNUPS;
import static pokeraidbot.Utils.printTime;

public class SignUp {
    private final String userName;
    private int howManyPeople;
    private LocalTime arrivalTime;

    public SignUp(String userName, int howManyPeople, LocalTime arrivalTime) {
        this.userName = userName;
        this.howManyPeople = howManyPeople;
        this.arrivalTime = arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignUp)) return false;

        SignUp signUp = (SignUp) o;

        if (howManyPeople != signUp.howManyPeople) return false;
        if (userName != null ? !userName.equals(signUp.userName) : signUp.userName != null) return false;
        return arrivalTime != null ? arrivalTime.equals(signUp.arrivalTime) : signUp.arrivalTime == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + howManyPeople;
        result = 31 * result + (arrivalTime != null ? arrivalTime.hashCode() : 0);
        return result;
    }

    public String getUserName() {
        return userName;
    }

    public int getHowManyPeople() {
        return howManyPeople;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return userName + ": " + howManyPeople + " ETA " + printTime(arrivalTime);
    }

    public void addPeople(int howManyPeople) {
        if (this.howManyPeople + howManyPeople > Utils.HIGH_LIMIT_FOR_SIGNUPS) {
            throw new RuntimeException("Adding " + howManyPeople + " will exceed your limit of " + HIGH_LIMIT_FOR_SIGNUPS);
        }
        this.howManyPeople += howManyPeople;
    }

    public void setEta(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
