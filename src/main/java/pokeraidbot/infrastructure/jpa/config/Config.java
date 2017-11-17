package pokeraidbot.infrastructure.jpa.config;

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

    // For JPA
    protected Config() {
    }

    public Config(String region, Boolean replyInDmWhenPossible, Locale locale, String server) {
        id = UUID.randomUUID().toString();
        Validate.notEmpty(region);
        Validate.notEmpty(server);
        this.region = region;
        this.replyInDmWhenPossible = replyInDmWhenPossible;
        setLocale(locale);
        this.server = server;
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

    @Override
    public String toString() {
        return "Config{" +
                "server='" + server + '\'' +
                ", region='" + region + '\'' +
                ", replyInDmWhenPossible=" + replyInDmWhenPossible +
                ", locale='" + locale + '\'' +
                ", giveHelp=" + giveHelp +
                ", pinGroups=" + pinGroups +
                ", modGroup=" + modPermissionGroup +
                ", feedbackStrategy=" + feedbackStrategy +
                '}';
    }

    public enum FeedbackStrategy {
        DEFAULT, KEEP_ALL, REMOVE_ALL_EXCEPT_MAP
    }

    public enum RaidGroupCreationStrategy {
        SAME_CHANNEL, PER_LEVEL, NAMED_CHANNEL
    }
}
