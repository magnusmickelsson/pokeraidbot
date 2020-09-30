package pokeraidbot.infrastructure.jpa.raid;

import net.dv8tion.jda.api.entities.User;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

@Entity
public class RaidEntitySignUp implements Serializable {
    @Id
    @Column(nullable = false)
    private String id;
    @Column(nullable = false)
    private String responsible;
    @Column(nullable = false)
    private Integer numberOfPeople;
    @Column(nullable = false)
    private String eta;

    // JPA
    protected RaidEntitySignUp() {
    }

    public RaidEntitySignUp(String responsible, Integer numberOfPeople, String eta) {
        id = UUID.randomUUID().toString();
        this.responsible = responsible;
        this.numberOfPeople = numberOfPeople;
        this.eta = eta;
    }

    public String getResponsible() {
        return responsible;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public String getEta() {
        return eta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaidEntitySignUp)) return false;

        RaidEntitySignUp that = (RaidEntitySignUp) o;

        if (responsible != null ? !responsible.equals(that.responsible) : that.responsible != null) return false;
        if (numberOfPeople != null ? !numberOfPeople.equals(that.numberOfPeople) : that.numberOfPeople != null)
            return false;
        return eta != null ? eta.equals(that.eta) : that.eta == null;
    }

    @Override
    public int hashCode() {
        int result = responsible != null ? responsible.hashCode() : 0;
        result = 31 * result + (numberOfPeople != null ? numberOfPeople.hashCode() : 0);
        result = 31 * result + (eta != null ? eta.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RaidEntitySignUp{" +
                "user='" + responsible + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", eta='" + eta + '\'' +
                '}';
    }

    public void setNumberOfPeople(int numberOfPeople, LocaleService localeService, User user) {
        if (numberOfPeople < 0 || numberOfPeople > Utils.HIGH_LIMIT_FOR_SIGNUPS) {
            throw new UserMessedUpException((User)null, localeService.getMessageFor(LocaleService.SIGNUP_BAD_NUMBER,
                    localeService.getLocaleForUser(user), String.valueOf(this.numberOfPeople),
                    String.valueOf(numberOfPeople)));
        }
        this.numberOfPeople = numberOfPeople;
    }

    public void setEta(String arrivalTime) {
        this.eta = arrivalTime;
    }

    public LocalTime getArrivalTime() {
        return LocalTime.parse(eta, Utils.timeParseFormatter);
    }
}
