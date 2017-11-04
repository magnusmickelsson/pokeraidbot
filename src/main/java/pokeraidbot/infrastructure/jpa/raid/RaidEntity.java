package pokeraidbot.infrastructure.jpa.raid;

import org.apache.commons.lang3.Validate;
import org.springframework.format.annotation.DateTimeFormat;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(indexes = {@Index(name = "id", columnList = "id"), @Index(name = "region", columnList = "region"),
        @Index(name = "pokemon", columnList = "pokemon,region")}
        ) // todo: uniqueconstraint that creator can only have one signup per id
public class RaidEntity implements Serializable {
    @Id
    private String id;
    @Basic(optional = false)
    @Column(nullable = false)
    private String pokemon;
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endOfRaid;
    @Basic(optional = false)
    @Column(nullable = false)
    private String gym;
    @Basic(optional = false)
    @Column(nullable = false)
    private String creator;
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="raid", referencedColumnName="id")
    private Set<RaidEntitySignUp> signUps = new HashSet<>();
    @Basic(optional = false)
    @Column(nullable = false)
    private String region;

    // JPA
    protected RaidEntity() {
    }

    public RaidEntity(String id, String pokemon, LocalDateTime endOfRaid, String gym, String creator, String region) {
        this.id = id;
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
        this.creator = creator;
        this.region = region;
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

    public LocalDateTime getEndOfRaid() {
        return endOfRaid;
    }

    public String getGym() {
        return gym;
    }

    public String getRegion() {
        return region;
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

    public boolean isExpired(ClockService clockService) {
        final LocalDateTime currentDateTime = clockService.getCurrentDateTime();
        return currentDateTime.isAfter(endOfRaid);
    }

    public boolean isActive(ClockService clockService) {
        final LocalDateTime currentDateTime = clockService.getCurrentDateTime();
        return currentDateTime.isBefore(endOfRaid) && endOfRaid.minusHours(1).isBefore(currentDateTime);
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
        if (signUps != null ? !signUps.equals(that.signUps) : that.signUps != null) return false;
        return region != null ? region.equals(that.region) : that.region == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (pokemon != null ? pokemon.hashCode() : 0);
        result = 31 * result + (endOfRaid != null ? endOfRaid.hashCode() : 0);
        result = 31 * result + (gym != null ? gym.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (signUps != null ? signUps.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
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
                ", region='" + region + '\'' +
                '}';
    }

    public void setPokemon(String pokemon) {
        Validate.notEmpty(pokemon);
        this.pokemon = pokemon;
    }

    public void setEndOfRaid(LocalDateTime endOfRaid) {
        this.endOfRaid = endOfRaid;
    }

    public RaidEntitySignUp getSignUp(String userName) {
        for (RaidEntitySignUp signUp : signUps) {
            if (signUp.getResponsible().equalsIgnoreCase(userName)) {
                return signUp;
            }
        }
        return null;
    }

    public Set<RaidEntitySignUp> getSignUpsAsSet() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(signUps));
    }

    public boolean isExRaid() {
        return Utils.isRaidExPokemon(pokemon);
    }
}
