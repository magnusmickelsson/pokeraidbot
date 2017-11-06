package pokeraidbot.infrastructure.jpa.raid;

import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.BatchSize;
import org.springframework.format.annotation.DateTimeFormat;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pokeraidbot.Utils.getStartOfRaid;

@Entity
@Table(indexes = {@Index(name = "id", columnList = "id"), @Index(name = "region", columnList = "region"),
        @Index(name = "pokemon", columnList = "pokemon,region")}
        ) // todo: uniqueconstraint that creator can only have one signup per id
public class RaidEntity implements Serializable {
    @Id
    @Column(nullable = false)
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
    @BatchSize(size = 20)
    private Set<RaidEntitySignUp> signUps = new HashSet<>();
    @Basic(optional = false)
    @Column(nullable = false)
    private String region;
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="raidid", referencedColumnName="id")
    @BatchSize(size = 5)
    private Set<RaidGroup> groups = new HashSet<>();

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

    public boolean addGroup(RaidGroup group) {
        if (groups.contains(group)) {
            return false;
        } else {
            groups.add(group);
            return true;
        }
    }

    public RaidGroup removeSignUp(RaidGroup group) {
        if (!groups.contains(group)) {
            return null;
        } else {
            groups.remove(group);
            return group;
        }
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
        return currentDateTime.isBefore(endOfRaid) &&
                getStartOfRaid(endOfRaid, isExRaid()).isBefore(currentDateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaidEntity)) return false;

        RaidEntity entity = (RaidEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;
        if (pokemon != null ? !pokemon.equals(entity.pokemon) : entity.pokemon != null) return false;
        if (endOfRaid != null ? !endOfRaid.equals(entity.endOfRaid) : entity.endOfRaid != null) return false;
        if (gym != null ? !gym.equals(entity.gym) : entity.gym != null) return false;
        if (creator != null ? !creator.equals(entity.creator) : entity.creator != null) return false;
        if (signUps != null ? !signUps.equals(entity.signUps) : entity.signUps != null) return false;
        if (region != null ? !region.equals(entity.region) : entity.region != null) return false;
        return groups != null ? groups.equals(entity.groups) : entity.groups == null;
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
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
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

    public List<RaidGroup> getGroupByCreator(String userId) {
        List<RaidGroup> groups = new LinkedList<>();
        for (RaidGroup group : groups) {
            if (group.getCreatorId().equalsIgnoreCase(userId)) {
                groups.add(group);
            }
        }
        return groups;
    }

    public Set<RaidGroup> getGroupsAsSet() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(groups).stream().sorted(
                Comparator.comparing(RaidGroup::getStartsAt)
        ).collect(Collectors.toSet()));
    }

    public boolean isExRaid() {
        return Utils.isRaidExPokemon(pokemon);
    }

    public RaidGroup removeGroup(String groupId) {
        for (RaidGroup group : groups) {
            if (group.getId().equals(groupId)) {
                groups.remove(group);
                return group;
            }
        }
        return null;
    }

    public boolean hasGroup(User user, LocalDateTime startAt) {
        for (RaidGroup group : groups) {
            if (group.getCreatorId().equals(user.getId()) && group.getStartsAt().equals(startAt)) {
                return true;
            }
        }
        return false;
    }
}
