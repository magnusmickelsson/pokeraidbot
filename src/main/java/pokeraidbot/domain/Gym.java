package pokeraidbot.domain;

public class Gym {
    private String name;
    private String id;
//    private MapCoordinates coordinates;
//    private Country country;


    public Gym(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gym)) return false;

        Gym gym = (Gym) o;

        if (name != null ? !name.equals(gym.name) : gym.name != null) return false;
        return id != null ? id.equals(gym.id) : gym.id == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + "(id=" + id + ")";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
