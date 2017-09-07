package pokeraidbot.infrastructure;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * "Number": "001",
 * "Name": "Bulbasaur",
 * "Generation": "Generation I",
 * "About": "Bulbasaur can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun's rays, the seed grows progressively larger.",
 * "Types": [
 * "Grass",
 * "Poison"
 * ],
 * "Resistant": [
 * "Water",
 * "Electric",
 * "Grass",
 * "Fighting",
 * "Fairy"
 * ],
 * "Weaknesses": [
 * "Fire",
 * "Ice",
 * "Flying",
 * "Psychic"
 * ],
 * "Fast Attack(s)": [
 * {
 * "Name": "Tackle",
 * "Type": "Normal",
 * "Damage": 12
 * },
 * {
 * "Name": "Vine Whip",
 * "Type": "Grass",
 * "Damage": 7
 * }
 * ],
 * "Special Attack(s)": [
 * {
 * "Name": "Power Whip",
 * "Type": "Grass",
 * "Damage": 70
 * },
 * {
 * "Name": "Seed Bomb",
 * "Type": "Grass",
 * "Damage": 40
 * },
 * {
 * "Name": "Sludge Bomb",
 * "Type": "Poison",
 * "Damage": 55
 * }
 * ],
 * "Weight": {
 * "Minimum": "6.04kg",
 * "Maximum": "7.76kg"
 * },
 * "Height": {
 * "Minimum": "0.61m",
 * "Maximum": "0.79m"
 * },
 * "Buddy Distance": "3km (Medium)",
 * "Base Stamina": "90 stamina points.",
 * "Base Attack": "118 attack points.",
 * "Base Defense": "118 defense points.",
 * "Base Flee Rate": "10% chance to flee.",
 * "Next Evolution Requirements": {
 * "Amount": 25,
 * "Name": "Bulbasaur candies"
 * },
 * "Next evolution(s)": [
 * {
 * "Number": 2,
 * "Name": "Ivysaur"
 * },
 * {
 * "Number": 3,
 * "Name": "Venusaur"
 * }
 * ],
 * "MaxCP": 951,
 * "MaxHP": 1071
 */
@JsonIgnoreProperties({"Fast Attack(s)", "Special Attack(s)", "Weight", "Height", "Base Stamina", "Base Attack",
        "Base Defense", "Base Flee Rate", "Next Evolution Requirements", "Next evolution(s)", "MaxCP", "MaxHP",
        "Previous evolution(s)", "Common Capture Area", "Asia", "Australia, New Zealand", "Western Europe", "North America",
        "Pokèmon Class", "LEGENDARY", "MYTHIC"})
public class JsonPokemon {
    @JsonProperty("Number")
    private Integer number;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Generation")
    private String generation;
    @JsonProperty("About")
    private String about;
    @JsonProperty("Types")
    private String[] types;
    @JsonProperty("Resistant")
    public String[] resistant;
    @JsonProperty("Weaknesses")
    public String[] weaknesses;
    @JsonProperty("Buddy Distance")
    public String buddyDistance;

    public JsonPokemon() {
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getResistant() {
        return resistant;
    }

    public void setResistant(String[] resistant) {
        this.resistant = resistant;
    }

    public String[] getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String[] weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getBuddyDistance() {
        return buddyDistance;
    }

    public void setBuddyDistance(String buddyDistance) {
        this.buddyDistance = buddyDistance;
    }
}
