package pokeraidbot.domain.gym;

public class Gym {
    private String name;
    private String id;
    private String x;
    private String y;
    private String imageLink;

    public Gym(String name, String id, String x, String y, String imageLink) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.imageLink = imageLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gym)) return false;

        Gym gym = (Gym) o;

        if (name != null ? !name.equals(gym.name) : gym.name != null) return false;
        if (id != null ? !id.equals(gym.id) : gym.id != null) return false;
        if (x != null ? !x.equals(gym.x) : gym.x != null) return false;
        if (y != null ? !y.equals(gym.y) : gym.y != null) return false;
        return imageLink != null ? imageLink.equals(gym.imageLink) : gym.imageLink == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (x != null ? x.hashCode() : 0);
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (imageLink != null ? imageLink.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getImageLink() {
        return imageLink;
    }
}
