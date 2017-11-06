package pokeraidbot.infrastructure.jpa.raid;

import org.apache.commons.lang3.Validate;
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
    private String server;
    @Column(nullable = false)
    private String channel;
    @Column(nullable = false)
    private String infoMessageId;
    @Column(nullable = false)
    private String emoteMessageId;
    @Column(nullable = false)
    private String creator;
    @Column(name = "raidid")
    private String raidId;
    @Column(nullable = false)
    private String startsAt;

    // JPA
    protected RaidGroup() {
    }

    public RaidGroup(String server, String channel, String infoMessageId, String emoteMessageId, String creator,
                     LocalDateTime startsAt) {
        id = UUID.randomUUID().toString();
        setServer(server);
        setChannel(channel);
        setInfoMessageId(infoMessageId);
        setEmoteMessageId(emoteMessageId);
        setCreator(creator);
        setStartsAt(startsAt);
    }

    public String getId() {
        return id;
    }

    public String getInfoMessageId() {
        return infoMessageId;
    }

    public void setInfoMessageId(String infoMessageId) {
        Validate.notEmpty(infoMessageId, "Info message id is empty");
        this.infoMessageId = infoMessageId;
    }

    public String getEmoteMessageId() {
        return emoteMessageId;
    }

    public void setEmoteMessageId(String emoteMessageId) {
        Validate.notEmpty(emoteMessageId, "Emote message id is empty");

        this.emoteMessageId = emoteMessageId;
    }

    public String getCreatorId() {
        return creator;
    }

    public void setCreator(String creator) {
        Validate.notEmpty(creator, "Creator is empty");
        this.creator = creator;
    }

    public LocalDateTime getStartsAt() {
        return LocalDateTime.parse(startsAt, Utils.dateAndTimeParseFormatter);
    }

    public void setStartsAt(LocalDateTime startsAt) {
        Validate.notNull(startsAt, "Start at datetime");
        this.startsAt = Utils.printDateTime(startsAt);
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        Validate.notEmpty(server, "Server is empty");
        this.server = server;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        Validate.notEmpty(channel, "Channel is empty");
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaidGroup)) return false;

        RaidGroup raidGroup = (RaidGroup) o;

        if (id != null ? !id.equals(raidGroup.id) : raidGroup.id != null) return false;
        if (server != null ? !server.equals(raidGroup.server) : raidGroup.server != null) return false;
        if (channel != null ? !channel.equals(raidGroup.channel) : raidGroup.channel != null) return false;
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
        result = 31 * result + (server != null ? server.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        result = 31 * result + (infoMessageId != null ? infoMessageId.hashCode() : 0);
        result = 31 * result + (emoteMessageId != null ? emoteMessageId.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (startsAt != null ? startsAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RaidGroup{" +
                "id='" + id + '\'' +
                ", server='" + server + '\'' +
                ", channel='" + channel + '\'' +
                ", infoMessageId='" + infoMessageId + '\'' +
                ", emoteMessageId='" + emoteMessageId + '\'' +
                ", creator='" + creator + '\'' +
                ", startsAt='" + startsAt + '\'' +
                '}';
    }

    public String getRaidId() {
        return raidId;
    }
}
