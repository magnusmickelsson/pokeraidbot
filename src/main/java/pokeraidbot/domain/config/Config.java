package pokeraidbot.domain.config;

import java.util.Locale;

public class Config {
    public final String region;
    public Boolean replyInDmWhenPossible = false;
    public Locale locale;

    public Config(String region, Boolean replyInDmWhenPossible, Locale locale) {
        this.region = region;
        this.replyInDmWhenPossible = replyInDmWhenPossible;
        this.locale = locale;
    }

    public Config(String region, Boolean replyInDmWhenPossible) {
        this(region, replyInDmWhenPossible, LocaleService.DEFAULT);
    }

    public Config(String region) {
        this(region, false);
    }

    @Override
    public String toString() {
        return "Config{" +
                "region='" + region + '\'' +
                ", replyInDmWhenPossible=" + replyInDmWhenPossible +
                ", locale=" + locale +
                '}';
    }
}
