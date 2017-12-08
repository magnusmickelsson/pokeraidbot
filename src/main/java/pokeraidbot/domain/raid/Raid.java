package pokeraidbot.domain.raid;

import pokeraidbot.Utils;
import pokeraidbot.domain.User;
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

    public Raid(Pokemon pokemon, LocalDateTime endOfRaid, Gym gym, LocaleService localeService, String region) {
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
        this.localeService = localeService;
        this.region = region;
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
        if (!(o instanceof Raid)) return false;

        Raid raid = (Raid) o;

        if (pokemon != null ? !pokemon.equals(raid.pokemon) : raid.pokemon != null) return false;
        if (endOfRaid != null ? !endOfRaid.equals(raid.endOfRaid) : raid.endOfRaid != null) return false;
        if (gym != null ? !gym.equals(raid.gym) : raid.gym != null) return false;
        if (signUps != null ? !signUps.equals(raid.signUps) : raid.signUps != null) return false;
        if (region != null ? !region.equals(raid.region) : raid.region != null) return false;
        if (creator != null ? !creator.equals(raid.creator) : raid.creator != null) return false;
        return id != null ? id.equals(raid.id) : raid.id == null;
    }

    @Override
    public int hashCode() {
        int result = pokemon != null ? pokemon.hashCode() : 0;
        result = 31 * result + (endOfRaid != null ? endOfRaid.hashCode() : 0);
        result = 31 * result + (gym != null ? gym.hashCode() : 0);
        result = 31 * result + (signUps != null ? signUps.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
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
            signUp = new SignUp(user.getName(), howManyPeople, arrivalTime, user.getNickName());
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
        return Utils.isRaidEx(this);
    }
}
