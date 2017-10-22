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
//@Table(indexes = @Index(columnList = "default,server,region"), name="config")
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

    public Boolean getPinGroups() {
        return pinGroups;
    }

    public void setPinGroups(Boolean pinGroups) {
        this.pinGroups = pinGroups;
    }

    public Locale getLocale() {
        return new Locale(locale);
    }

    public void setLocale(Locale locale) {
        Validate.notNull(locale);
        this.locale = locale.getLanguage();
    }

    public String getId() {
        return id;
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
        return pinGroups != null ? pinGroups.equals(config.pinGroups) : config.pinGroups == null;
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
        return result;
    }

    @Override
    public String toString() {
        return "Config{" +
                "region='" + region + '\'' +
                ", replyInDmWhenPossible=" + replyInDmWhenPossible +
                ", locale='" + locale + '\'' +
                ", giveHelp=" + (giveHelp == null ? String.valueOf(false) : giveHelp) +
                ", pinGroups=" + (pinGroups == null ? String.valueOf(true) : pinGroups) +
                '}';
    }
}
