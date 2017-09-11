package pokeraidbot.infrastructure.jpa;

import pokeraidbot.domain.ClockService;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class RaidEntity {
    @Id
    private String id;
    @Basic(optional = false)
    private String pokemon;
    @Basic(optional = false)
    private LocalTime endOfRaid;
    @Basic(optional = false)
    private String gym;
    @Basic(optional = false)
    private String creator;
    @ElementCollection
    private Set<RaidEntitySignUp> signUps = new HashSet<>();

    // JPA
    protected RaidEntity() {
    }

    public RaidEntity(String id, String pokemon, LocalTime endOfRaid, String gym, String creator) {
        this.id = id;
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public String getId() {
        return id;
    }

    public String getPokemon() {
        return pokemon;
    }

    public LocalTime getEndOfRaid() {
        return endOfRaid;
    }

    public String getGym() {
        return gym;
    }

    public Set<RaidEntitySignUp> getSignUps() {
        return signUps;
    }

    public boolean addSignUp(RaidEntitySignUp signUp) {
        if (signUps.contains(signUp)) {
            return false;
        } else {
            signUps.add(signUp);
            return true;
        }
    }

    public RaidEntitySignUp removeSignUp(RaidEntitySignUp signUp) {
        if (!signUps.contains(signUp)) {
            return null;
        } else {
            signUps.remove(signUp);
            return signUp;
        }
    }

    public boolean isActive(ClockService clockService) {
        return clockService.getCurrentTime().isBefore(endOfRaid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaidEntity)) return false;

        RaidEntity that = (RaidEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (pokemon != null ? !pokemon.equals(that.pokemon) : that.pokemon != null) return false;
        if (endOfRaid != null ? !endOfRaid.equals(that.endOfRaid) : that.endOfRaid != null) return false;
        if (gym != null ? !gym.equals(that.gym) : that.gym != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        return signUps != null ? signUps.equals(that.signUps) : that.signUps == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (pokemon != null ? pokemon.hashCode() : 0);
        result = 31 * result + (endOfRaid != null ? endOfRaid.hashCode() : 0);
        result = 31 * result + (gym != null ? gym.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (signUps != null ? signUps.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RaidEntity{" +
                "id='" + id + '\'' +
                ", pokemon='" + pokemon + '\'' +
                ", endOfRaid=" + endOfRaid +
                ", gym='" + gym + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}
