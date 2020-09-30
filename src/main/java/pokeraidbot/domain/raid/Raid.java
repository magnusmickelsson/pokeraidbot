package pokeraidbot.domain.raid;

import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.raid.signup.SignUp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static pokeraidbot.Utils.printDateTime;
import static pokeraidbot.Utils.printTime;

public class Raid {
    private final Pokemon pokemon;
    private final LocalDateTime endOfRaid;
    private final Gym gym;
    private final LocaleService localeService;
    private Map<String, SignUp> signUps = new ConcurrentHashMap<>();
    private String region;
    private String creator;
    private String id;
    private Boolean ex;

    public Raid(Pokemon pokemon, LocalDateTime endOfRaid, Gym gym, LocaleService localeService, String region, boolean ex) {
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
        this.localeService = localeService;
        this.region = region;
        this.ex = ex;
    }

    void setId(String id) {
        this.id = id;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public LocalDateTime getEndOfRaid() {
        return endOfRaid;
    }

    public Gym getGym() {
        return gym;
    }

    public String getCreator() {
        return creator;
    }

    void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Raid raid = (Raid) o;

        return new EqualsBuilder()
                .append(pokemon, raid.pokemon)
                .append(endOfRaid, raid.endOfRaid)
                .append(gym, raid.gym)
                .append(localeService, raid.localeService)
                .append(signUps, raid.signUps)
                .append(region, raid.region)
                .append(creator, raid.creator)
                .append(id, raid.id)
                .append(ex, raid.ex)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pokemon)
                .append(endOfRaid)
                .append(gym)
                .append(localeService)
                .append(signUps)
                .append(region)
                .append(creator)
                .append(id)
                .append(ex)
                .toHashCode();
    }

    @Override
    public String toString() {
        return localeService.getMessageFor(LocaleService.RAID_TOSTRING, LocaleService.DEFAULT, pokemon.toString(),
                gym.toString(), printDateTime(Utils.getStartOfRaid(endOfRaid, isExRaid())),
                printTime(endOfRaid.toLocalTime()));
    }

    public String toString(Locale locale) {
        return localeService.getMessageFor(LocaleService.RAID_TOSTRING, locale, pokemon.toString(),
                gym.toString(), Utils.printTimeIfSameDay(Utils.getStartOfRaid(endOfRaid, isExRaid())),
                Utils.printTimeIfSameDay(endOfRaid));
    }

    public void signUp(User user, int howManyPeople, LocalTime arrivalTime, RaidRepository repository) {
        SignUp signUp = signUps.get(user.getName());
        if (signUp != null) {
            int numberOfPeopleInSignup = signUp.getHowManyPeople();
            if (arrivalTime.equals(signUp.getArrivalTime())) {
                numberOfPeopleInSignup += howManyPeople;
            } else {
                numberOfPeopleInSignup = howManyPeople;
                signUp.setEta(arrivalTime);
            }
            Utils.assertNotTooManyOrNoNumber(user, localeService, String.valueOf(numberOfPeopleInSignup));
            signUp.setHowManyPeople(numberOfPeopleInSignup);
        } else {
            signUp = new SignUp(user.getName(), howManyPeople, arrivalTime);
            signUps.put(user.getName(), signUp);
        }
        repository.addSignUp(user, this, signUp);
    }

    public Set<SignUp> getSignUps() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(signUps.values()));
    }

    public int getNumberOfPeopleSignedUp() {
        return signUps.values().stream().mapToInt(signup -> signup.getHowManyPeople()).sum();
    }

    public SignUp remove(User user, RaidRepository raidRepository) {
        final SignUp removed = signUps.remove(user.getName());
        if (removed != null) {
            raidRepository.removeSignUp(user, this, removed);
        }
        return removed;
    }

    public void setSignUps(Map<String, SignUp> signUps) {
        this.signUps = signUps;
    }

    public String getRegion() {
        return region;
    }

    public String getId() {
        return id;
    }

    public int getNumberOfPeopleArrivingAt(LocalTime eta) {
        return signUps.values().stream().mapToInt(signup ->
                signup.getArrivalTime().equals(eta) ? signup.getHowManyPeople() : 0)
                .sum();
    }

    public Set<SignUp> getSignUpsAt(LocalTime localTime) {
        return signUps.values().stream().filter(s -> s.getArrivalTime().equals(localTime)).collect(Collectors.toSet());
    }

    public boolean isExRaid() {
        return ex;
    }
}
