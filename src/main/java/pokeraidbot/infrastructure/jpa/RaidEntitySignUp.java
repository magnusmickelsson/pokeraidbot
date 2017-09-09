package pokeraidbot.infrastructure.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class RaidEntitySignUp {
    private String user;
    private Integer numberOfPeople;
    private String eta;

    // JPA
    protected RaidEntitySignUp() {
    }

    public RaidEntitySignUp(String user, Integer numberOfPeople, String eta) {
        this.user = user;
        this.numberOfPeople = numberOfPeople;
        this.eta = eta;
    }

    public String getUser() {
        return user;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public String getEta() {
        return eta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaidEntitySignUp)) return false;

        RaidEntitySignUp that = (RaidEntitySignUp) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (numberOfPeople != null ? !numberOfPeople.equals(that.numberOfPeople) : that.numberOfPeople != null)
            return false;
        return eta != null ? eta.equals(that.eta) : that.eta == null;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (numberOfPeople != null ? numberOfPeople.hashCode() : 0);
        result = 31 * result + (eta != null ? eta.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RaidEntitySignUp{" +
                "user='" + user + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", eta='" + eta + '\'' +
                '}';
    }
}
