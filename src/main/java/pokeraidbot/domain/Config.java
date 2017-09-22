package pokeraidbot.domain;

public class Config {
    public final String region;

    public Config(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Config{" +
                "region='" + region + '\'' +
                '}';
    }
}
