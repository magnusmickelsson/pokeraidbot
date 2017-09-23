package pokeraidbot.domain;

public class Config {
    public final String region;
    public Boolean replyInDmWhenPossible = false;

    public Config(String region, Boolean replyInDmWhenPossible) {
        this.region = region;
        this.replyInDmWhenPossible = replyInDmWhenPossible;
    }

    public Config(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Config{" +
                "region='" + region + '\'' +
                ", replyInDmWhenPossible=" + replyInDmWhenPossible +
                '}';
    }
}
