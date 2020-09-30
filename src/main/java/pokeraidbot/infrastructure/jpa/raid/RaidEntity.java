package pokeraidbot.infrastructure.jpa.raid;

import net.dv8tion.jda.api.entities.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.FluentIterable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
    @JoinColumn(name = "raid", referencedColumnName = "id")
    @BatchSize(size = 20)
    private Set<RaidEntitySignUp> signUps = new HashSet<>();
    @Basic(optional = false)
    @Column(nullable = false)
    private String region;
    @Basic
    @Column
    private Boolean ex;
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "raidid", referencedColumnName = "id")
    @BatchSize(size = 5)
    private Set<RaidGroup> groups = new HashSet<>();

    // JPA
    protected RaidEntity() {
    }

    public RaidEntity(String id, String pokemon, LocalDateTime endOfRaid, String gym, String creator, String region, Boolean ex) {
        this.id = id;
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
        this.creator = creator;
        this.region = region;
        this.ex = ex;
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

        if (o == null || getClass() != o.getClass()) return false;

        RaidEntity that = (RaidEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(pokemon, that.pokemon)
                .append(endOfRaid, that.endOfRaid)
                .append(gym, that.gym)
                .append(creator, that.creator)
                .append(signUps, that.signUps)
                .append(region, that.region)
                .append(ex, that.ex)
                .append(groups, that.groups)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(pokemon)
                .append(endOfRaid)
                .append(gym)
                .append(creator)
                .append(signUps)
                .append(region)
                .append(ex)
                .append(groups)
                .toHashCode();
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
                ", ex=" + ex +
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
        final TreeSet<RaidGroup> sortedGroups = new TreeSet<>(groups);
        return sortedGroups;
    }

    // Backwards compatible to already saved EX raids that last over the day
    public boolean isExRaid() {
        return ex == null ? true : ex;
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

    public boolean userHasGroup(User user) {
        Validate.notNull(user, "User");
        for (RaidGroup group : groups) {
            final boolean isUsersGroup = group.getCreatorId().equals(user.getId());
            if (isUsersGroup) {
                return true;
            }
        }
        return false;
    }

    public boolean hasGroupAt(User user, LocalDateTime startAt) {
        Validate.notNull(user, "User");
        Validate.notNull(startAt, "StartAt");
        for (RaidGroup group : groups) {
            final boolean isUsersGroup = group.getCreatorId().equals(user.getId());
            final boolean isMatchingStartTime = group.getStartsAt().equals(startAt);
            if (isUsersGroup
                    && isMatchingStartTime) {
                return true;
            }
        }
        return false;
    }

    public boolean hasManyGroupsBy(User user) {
        int count = 0;
        for (RaidGroup group : groups) {
            if (group.getCreatorId().equals(user.getId())) {
                count++;
            }
        }
        return count > 1;
    }

    public RaidGroup getGroupByCreatorAndStart(String groupCreatorId, LocalDateTime currentStartAt) {
        for (RaidGroup group : groups) {
            if (group.getCreatorId().equals(groupCreatorId) && group.getStartsAt().equals(currentStartAt)) {
                return group;
            }
        }
        return null;
    }

    public boolean existsGroupAt(LocalDateTime startAt) {
        Validate.notNull(startAt, "StartAt");
        for (RaidGroup group : groups) {
            final boolean isMatchingStartTimeIfProvided = group.getStartsAt().equals(startAt);
            if (isMatchingStartTimeIfProvided) {
                return true;
            }
        }
        return false;
    }
}
