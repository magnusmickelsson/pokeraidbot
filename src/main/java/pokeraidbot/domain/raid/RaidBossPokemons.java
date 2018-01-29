package pokeraidbot.domain.raid;

import org.apache.commons.lang3.Validate;

// Replace this with having bosses in the database, cached.
public enum RaidBossPokemons {
    // Tier 1
    Bayleef(1),
    Croconaw(1),
    Magikarp(1),
    Quilava(1),
    Ivysaur(1),
    Metapod(1),
    Charmeleon(1),
    Wartortle(1),
    Egg1(1),

    // Tier 2
    Electabuzz(2),
    Exeggutor(2),
    Magmar(2),
    Muk(2),
    Weezing(2),
    Magneton(2),
    Sableye(2),
    Sandslash(2),
    Tentacruel(2),
    Marowak(2),
    Cloyster(2),
    Egg2(2),

    //Tier 3
    Alakazam(3),
    Arcanine(3),
    Flareon(3),
    Gengar(3),
    Jolteon(3),
    Machamp(3),
    Vaporeon(3),
    Ninetales(3),
    Scyther(3),
    Omastar(3),
    Porygon(3),
    Egg3(3),

    //Tier 4
    Blastoise(4),
    Charizard(4),
    Lapras(4),
    Rhydon(4),
    Snorlax(4),
    Tyranitar(4),
    Venusaur(4),
    Poliwrath(4),
    Victreebel(4),
    Golem(4),
    Nidoking(4),
    Nidoqueen(4),
    Egg4(4),

    // Tier 5 (legendary and EX)
    Entei(5),
    Lugia(5),
    Articuno(5),
    Moltres(5),
    Zapdos(5),
    Suicune(5),
    Raikou(5),
    Egg5(5),
    Ho_Oh(5),
    // EX
    Mewtwo(5),

    // ==== Generation 3 ====
    // Tier 1
    Wailmer(1),
    // Tier 2
    Mawile(2),
    // Tier 4
    Absol(4),
    Aggron(4),
    Salamence(4),
    // Tier 5
    Groudon(5),
    Registeel(5),
    Latios(5),
    Rayquaza(5),
    Latias(5),
    Kyogre(5);

    private final int tier;

    RaidBossPokemons(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public boolean isPokemonRaidBoss(String name) {
        Validate.notEmpty(name, "Pokemon name is null!");
        for (RaidBossPokemons mon : RaidBossPokemons.values()) {
            if (name.equalsIgnoreCase(mon.name().replaceAll("_", "-"))) {
                return true;
            }
        }
        return false;
    }
}
