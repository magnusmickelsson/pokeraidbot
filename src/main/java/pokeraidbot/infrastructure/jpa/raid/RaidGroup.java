package pokeraidbot.infrastructure.jpa.raid;

import pokeraidbot.Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

// todo: constraints that we can only have one raid group for a certain time for a certain raid, and that
// the message id's are not duplicated?
@Entity // todo: indexing
public class RaidGroup implements Serializable {
    @Id
    @Column(nullable = false)
    private String id;
    @Column(nullable = false)
    private String infoMessageId;
    @Column(nullable = false)
    private String emoteMessageId;
    @Column(nullable = false)
    private String creator;
    @Column
    private String startsAt;

    // JPA
    protected RaidGroup() {
    }

    public RaidGroup(String infoMessageId, String emoteMessageId, String creator, String startsAt) {
        id = UUID.randomUUID().toString();
        this.infoMessageId = infoMessageId;
        this.emoteMessageId = emoteMessageId;
        this.creator = creator;
        this.startsAt = startsAt;
    }

    public String getId() {
        return id;
    }

    public String getInfoMessageId() {
        return infoMessageId;
    }

    public void setInfoMessageId(String infoMessageId) {
        this.infoMessageId = infoMessageId;
    }

    public String getEmoteMessageId() {
        return emoteMessageId;
    }

    public void setEmoteMessageId(String emoteMessageId) {
        this.emoteMessageId = emoteMessageId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getStartsAt() {
        return LocalDateTime.parse(startsAt, Utils.dateAndTimeParseFormatter);
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = Utils.printDateTime(startsAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaidGroup)) return false;

        RaidGroup raidGroup = (RaidGroup) o;

        if (id != null ? !id.equals(raidGroup.id) : raidGroup.id != null) return false;
        if (infoMessageId != null ? !infoMessageId.equals(raidGroup.infoMessageId) : raidGroup.infoMessageId != null)
            return false;
        if (emoteMessageId != null ? !emoteMessageId.equals(raidGroup.emoteMessageId) : raidGroup.emoteMessageId != null)
            return false;
        if (creator != null ? !creator.equals(raidGroup.creator) : raidGroup.creator != null) return false;
        return startsAt != null ? startsAt.equals(raidGroup.startsAt) : raidGroup.startsAt == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (infoMessageId != null ? infoMessageId.hashCode() : 0);
        result = 31 * result + (emoteMessageId != null ? emoteMessageId.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (startsAt != null ? startsAt.hashCode() : 0);
        return result;
    }
}
