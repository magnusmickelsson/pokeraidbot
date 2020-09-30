package pokeraidbot.infrastructure.jpa.config;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;

import javax.persistence.*;
import java.util.Locale;
import java.util.UUID;

/**
 * Server configuration entity
 */
@Entity
@Table(indexes = @Index(columnList = "server"), name="config")
public class Config {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    @Column(nullable = false, unique = true)
    private String server;
    @Column(nullable = false)
    private String region;
    @Column(nullable = false)
    private Boolean replyInDmWhenPossible = false;
    @Column(nullable = false)
    private String locale;
    @Column
    private Boolean giveHelp = false;
    @Column
    private Boolean pinGroups = true;
    @Column
    private String overviewMessageId;
    @Column
    private String modPermissionGroup;
    @Column
    private FeedbackStrategy feedbackStrategy = FeedbackStrategy.DEFAULT;
    @Column
    private RaidGroupCreationStrategy groupCreationStrategy = RaidGroupCreationStrategy.SAME_CHANNEL;
    @Column
    private String groupCreationChannel;
    @Column
    private Boolean useBotIntegration = false;

    // For JPA
    protected Config() {
    }

    public Config(String region, Boolean replyInDmWhenPossible, Locale locale, String server) {
        id = UUID.randomUUID().toString();
        Validate.notEmpty(region);
        Validate.notEmpty(server);
        this.region = region.toLowerCase();
        this.replyInDmWhenPossible = replyInDmWhenPossible;
        setLocale(locale);
        this.server = server.toLowerCase();
    }

    public Config(String region, Boolean replyInDmWhenPossible, String server) {
        this(region, replyInDmWhenPossible, LocaleService.DEFAULT, server);
    }

    public Config(String region, String server) {
        this(region, false, server);
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Boolean getReplyInDmWhenPossible() {
        return replyInDmWhenPossible;
    }

    public void setReplyInDmWhenPossible(Boolean replyInDmWhenPossible) {
        this.replyInDmWhenPossible = replyInDmWhenPossible;
    }

    public Boolean getGiveHelp() {
        return giveHelp;
    }

    public void setGiveHelp(Boolean giveHelp) {
        this.giveHelp = giveHelp;
    }

    public Boolean isPinGroups() {
        return pinGroups;
    }

    public void setPinGroups(Boolean pinGroups) {
        this.pinGroups = pinGroups;
    }

    public Boolean useBotIntegration() {
        if (useBotIntegration != null) {
            return useBotIntegration;
        } else {
            return false;
        }
    }

    public void setUseBotIntegration(Boolean useBotIntegration) {
        this.useBotIntegration = useBotIntegration;
    }

    public Locale getLocale() {
        if (locale != null) {
            return new Locale(locale);
        } else {
            return LocaleService.DEFAULT;
        }
    }

    public void setLocale(Locale locale) {
        Validate.notNull(locale);
        this.locale = locale.getLanguage();
    }

    public String getId() {
        return id;
    }

    public void setOverviewMessageId(String overviewMessageId) {
        this.overviewMessageId = overviewMessageId;
    }

    public String getOverviewMessageId() {
        return overviewMessageId;
    }

    public String getModPermissionGroup() {
        return modPermissionGroup;
    }

    public void setModPermissionGroup(String modPermissionGroup) {
        this.modPermissionGroup = modPermissionGroup;
    }

    public FeedbackStrategy getFeedbackStrategy() {
        return feedbackStrategy == null ? FeedbackStrategy.DEFAULT : feedbackStrategy;
    }

    public void setFeedbackStrategy(FeedbackStrategy feedbackStrategy) {
        this.feedbackStrategy = feedbackStrategy;
    }

    public RaidGroupCreationStrategy getGroupCreationStrategy() {
        return groupCreationStrategy;
    }

    public void setGroupCreationStrategy(RaidGroupCreationStrategy groupCreationStrategy) {
        this.groupCreationStrategy = groupCreationStrategy;
    }

    public String getGroupCreationChannel() {
        return groupCreationChannel;
    }

    public void setGroupCreationChannel(String groupCreationChannel) {
        this.groupCreationChannel = groupCreationChannel;
    }

    public MessageChannel getGroupCreationChannel(Guild guild) {
        if (guild == null) {
            return null;
        }
        for (MessageChannel c : guild.getTextChannels()) {
            if (c.getName().equalsIgnoreCase(groupCreationChannel)) {
                return c;
            }
        }
        return null;
    }

    public enum FeedbackStrategy {
        DEFAULT, KEEP_ALL, REMOVE_ALL_EXCEPT_MAP // This actually also removes maps, but after some time (after wishes from users)
    }

    public enum RaidGroupCreationStrategy {
        SAME_CHANNEL, NAMED_CHANNEL // Create group in named group, not necessarily the channel where command originated
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;

        Config config = (Config) o;

        if (id != null ? !id.equals(config.id) : config.id != null) return false;
        if (server != null ? !server.equals(config.server) : config.server != null) return false;
        if (region != null ? !region.equals(config.region) : config.region != null) return false;
        if (replyInDmWhenPossible != null ? !replyInDmWhenPossible.equals(config.replyInDmWhenPossible) : config.replyInDmWhenPossible != null)
            return false;
        if (locale != null ? !locale.equals(config.locale) : config.locale != null) return false;
        if (giveHelp != null ? !giveHelp.equals(config.giveHelp) : config.giveHelp != null) return false;
        if (pinGroups != null ? !pinGroups.equals(config.pinGroups) : config.pinGroups != null) return false;
        if (overviewMessageId != null ? !overviewMessageId.equals(config.overviewMessageId) : config.overviewMessageId != null)
            return false;
        if (modPermissionGroup != null ? !modPermissionGroup.equals(config.modPermissionGroup) : config.modPermissionGroup != null)
            return false;
        if (feedbackStrategy != config.feedbackStrategy) return false;
        if (groupCreationStrategy != config.groupCreationStrategy) return false;
        if (groupCreationChannel != null ? !groupCreationChannel.equals(config.groupCreationChannel) : config.groupCreationChannel != null)
            return false;
        return useBotIntegration != null ? useBotIntegration.equals(config.useBotIntegration) : config.useBotIntegration == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (server != null ? server.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (replyInDmWhenPossible != null ? replyInDmWhenPossible.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (giveHelp != null ? giveHelp.hashCode() : 0);
        result = 31 * result + (pinGroups != null ? pinGroups.hashCode() : 0);
        result = 31 * result + (overviewMessageId != null ? overviewMessageId.hashCode() : 0);
        result = 31 * result + (modPermissionGroup != null ? modPermissionGroup.hashCode() : 0);
        result = 31 * result + (feedbackStrategy != null ? feedbackStrategy.hashCode() : 0);
        result = 31 * result + (groupCreationStrategy != null ? groupCreationStrategy.hashCode() : 0);
        result = 31 * result + (groupCreationChannel != null ? groupCreationChannel.hashCode() : 0);
        result = 31 * result + (useBotIntegration != null ? useBotIntegration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Config{" +
                "server='" + server + '\'' +
                ", region='" + region + '\'' +
                ", replyInDm=" + replyInDmWhenPossible +
                ", locale='" + locale + '\'' +
                ", giveHelp=" + giveHelp +
                ", pinGroups=" + pinGroups +
                ", overview='" + overviewMessageId + '\'' +
                ", modGroup='" + modPermissionGroup + '\'' +
                ", feedback=" + feedbackStrategy +
                ", groupCreationStrategy=" + groupCreationStrategy +
                ", groupCreationChannel='" + groupCreationChannel + '\'' +
                ", useBotIntegration=" + useBotIntegration +
                '}';
    }
}
