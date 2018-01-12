package dataimport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(value = {
        "poke_clicks", "fav_count", "been_count", "del_count",
        "gym_count", "poke_type", "gym_team", "comments",
        "confirm", "lure_timer", "description", "guard_id",
        "gym_prestige", "pokemon_data", "gym_last", "gym_change",
        "pmon_status", "pmon_data", "raid_status", "raid_timer",
        "raid_level", "raid_boss", "raid_boss_cp", "cluster_rating"
})
public class GymData {
    @JsonProperty("poke_id")
    private Integer id;
    @JsonProperty("poke_image")
    private String pokeImage;
    @JsonProperty("markerlat")
    private String lat;
    @JsonProperty("markerlng")
    private String lng;

    public GymData() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPokeImage() {
        return pokeImage;
    }

    public void setPokeImage(String pokeImage) {
        this.pokeImage = pokeImage;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
