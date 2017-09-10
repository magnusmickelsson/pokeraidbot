package pokeraidbot.infrastructure.jpa;

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
    @ElementCollection
    private Set<RaidEntitySignUp> signUps = new HashSet<>();

    // JPA
    protected RaidEntity() {
    }

    public RaidEntity(String id, String pokemon, LocalTime endOfRaid, String gym) {
        this.id = id;
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
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
}
