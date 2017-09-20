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
    /**
     {
     "poke_id": "118412",
     "poke_clicks": "43",
     "fav_count": "0",
     "been_count": "0",
     "del_count": "0",
     "gym_count": "0",
     "poke_type": "2",
     "gym_team": "1",
     "comments": "0",
     "poke_image": "https:\/\/lh4.ggpht.com\/Rtn4zt0q6Wp3bXln1ncrXTkMkzQIOimp3eKpNI8l-HPLfpQMmggAHCK3-mkvD1c3nanZc8tOAk_mvFYONxc",
     "confirm": 2,
     "lure_timer": 0,
     "markerlat": 59.909227,
     "markerlng": 17.212883,
     "description": "",
     "guard_id": "126",
     "gym_prestige": "0",
     "pokemon_data": "Snorlax##1146##Souvlaki0--Flareon##2203##Egina00--Nidorino##293##Pepounaki--Magmar##1251##Spion33a--",
     "guard_name": "Magmar",
     "gym_last": "1 month ago",
     "gym_change": "1 month ago",
     "pmon_status": 0,
     "pmon_data": "",
     "raid_status": 0,
     "raid_timer": 0,
     "raid_level": 0,
     "raid_boss": "",
     "raid_boss_cp": 0
     }
     */
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
