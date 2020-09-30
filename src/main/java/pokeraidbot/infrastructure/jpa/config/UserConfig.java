package pokeraidbot.infrastructure.jpa.config;

import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Formatter;
import java.util.Locale;

@Entity
public class UserConfig {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    @Basic
    private String tracking1;
    @Basic
    private String tracking2;
    @Basic
    private String tracking3;
    @Basic
    private String locale;
    @Basic
    private String nick;

    // For JPA
    protected UserConfig() {
    }

    public UserConfig(String id, Pokemon tracking1, Pokemon tracking2, Pokemon tracking3, Locale locale) {
        this.id = id;
        setTracking1(tracking1);
        setTracking2(tracking2);
        setTracking3(tracking3);
        setLocale(locale);
    }

    public String getId() {
        return id;
    }

    public String getTracking1() {
        return tracking1;
    }

    public String getTracking2() {
        return tracking2;
    }

    public String getTracking3() {
        return tracking3;
    }

    public Locale getLocale() {
        if (locale == null) {
            return null;
        } else {
            return Locale.forLanguageTag(locale);
        }
    }

    public void setTracking1(Pokemon tracking1) {
        if (tracking1 != null) {
            this.tracking1 = tracking1.getName();
        }
    }

    public void setTracking2(Pokemon tracking2) {
        if (tracking2 != null) {
            this.tracking2 = tracking2.getName();
        }
    }

    public void setTracking3(Pokemon tracking3) {
        if (tracking3 != null) {
            this.tracking3 = tracking3.getName();
        }
    }

    public String getNick(User user) {
        return nick == null ? user.getName() : nick;
    }

    public void setNick(String nick) {
        Validate.notEmpty(nick, "Nickname may not be empty!");
        this.nick = nick;
    }

    public void setLocale(Locale locale) {
        if (locale != null) {
            if (!LocaleService.isSupportedLocale(locale)) {
                throw new RuntimeException("Not a supported locale: " + locale);
            }
            this.locale = locale.getLanguage();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserConfig)) return false;

        UserConfig config = (UserConfig) o;

        if (id != null ? !id.equals(config.id) : config.id != null) return false;
        if (tracking1 != null ? !tracking1.equals(config.tracking1) : config.tracking1 != null) return false;
        if (tracking2 != null ? !tracking2.equals(config.tracking2) : config.tracking2 != null) return false;
        if (tracking3 != null ? !tracking3.equals(config.tracking3) : config.tracking3 != null) return false;
        if (locale != null ? !locale.equals(config.locale) : config.locale != null) return false;
        return nick != null ? nick.equals(config.nick) : config.nick == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tracking1 != null ? tracking1.hashCode() : 0);
        result = 31 * result + (tracking2 != null ? tracking2.hashCode() : 0);
        result = 31 * result + (tracking3 != null ? tracking3.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (nick != null ? nick.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserConfig{" +
                "id='" + id + '\'' +
                ", tracking1='" + tracking1 + '\'' +
                ", tracking2='" + tracking2 + '\'' +
                ", tracking3='" + tracking3 + '\'' +
                ", locale='" + locale + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }

    public boolean hasFreeTrackingSpot() {
        return tracking1 == null || tracking2 == null || tracking3 == null;
    }

    // Yes, having a set number of tracked pokemons is yucky.
    // However, we want to keep down the number of rows and size of the database due to Heroku limitations.
    public void setNextTrackingSpot(Pokemon pokemon) {
        if (tracking1 == null) {
            setTracking1(pokemon);
        } else if (tracking2 == null) {
            setTracking2(pokemon);
        } else if (tracking3 == null) {
            setTracking3(pokemon);
        } else {
            throw new RuntimeException("No free pokemon tracking spots (3 spots available in total).");
        }
    }

    public void removeTrackingFor(Pokemon pokemon) {
        if (tracking1 != null && tracking1.equalsIgnoreCase(pokemon.getName())) {
            tracking1 = null;
        } else if (tracking2 != null && tracking2.equalsIgnoreCase(pokemon.getName())) {
            tracking2 = null;
        } else if (tracking3 != null && tracking3.equalsIgnoreCase(pokemon.getName())) {
            tracking3 = null;
        }
    }
}
