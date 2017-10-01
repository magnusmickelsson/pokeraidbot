package pokeraidbot.infrastructure.jpa.config;

import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Locale;
import java.util.UUID;

@Entity

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

    public Config() {
        id = UUID.randomUUID().toString();
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
        return locale != null ? locale.equals(config.locale) : config.locale == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (server != null ? server.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (replyInDmWhenPossible != null ? replyInDmWhenPossible.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Config{" +
                "server='" + server + '\'' +
                ", region='" + region + '\'' +
                ", replyInDmWhenPossible=" + replyInDmWhenPossible +
                ", locale=" + locale +
                '}';
    }
}
