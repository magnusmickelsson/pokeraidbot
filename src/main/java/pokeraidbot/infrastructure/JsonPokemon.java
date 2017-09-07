package pokeraidbot.infrastructure;

/**

 "Number": "001",
 "Name": "Bulbasaur",
 "Generation": "Generation I",
 "About": "Bulbasaur can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun's rays, the seed grows progressively larger.",
 "Types": [
 "Grass",
 "Poison"
 ],
 "Resistant": [
 "Water",
 "Electric",
 "Grass",
 "Fighting",
 "Fairy"
 ],
 "Weaknesses": [
 "Fire",
 "Ice",
 "Flying",
 "Psychic"
 ],
 "Fast Attack(s)": [
 {
 "Name": "Tackle",
 "Type": "Normal",
 "Damage": 12
 },
 {
 "Name": "Vine Whip",
 "Type": "Grass",
 "Damage": 7
 }
 ],
 "Special Attack(s)": [
 {
 "Name": "Power Whip",
 "Type": "Grass",
 "Damage": 70
 },
 {
 "Name": "Seed Bomb",
 "Type": "Grass",
 "Damage": 40
 },
 {
 "Name": "Sludge Bomb",
 "Type": "Poison",
 "Damage": 55
 }
 ],
 "Weight": {
 "Minimum": "6.04kg",
 "Maximum": "7.76kg"
 },
 "Height": {
 "Minimum": "0.61m",
 "Maximum": "0.79m"
 },
 "Buddy Distance": "3km (Medium)",
 "Base Stamina": "90 stamina points.",
 "Base Attack": "118 attack points.",
 "Base Defense": "118 defense points.",
 "Base Flee Rate": "10% chance to flee.",
 "Next Evolution Requirements": {
 "Amount": 25,
 "Name": "Bulbasaur candies"
 },
 "Next evolution(s)": [
 {
 "Number": 2,
 "Name": "Ivysaur"
 },
 {
 "Number": 3,
 "Name": "Venusaur"
 }
 ],
 "MaxCP": 951,
 "MaxHP": 1071
 */
public class JsonPokemon {
    private String Number;
    private String Name;
    private String Generation;
    private String About;
}
