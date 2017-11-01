package pokeraidbot.infrastructure.jpa.raid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaidEntityRepository extends JpaRepository<RaidEntity, String> {
    List<RaidEntity> findByGymAndRegionOrderByEndOfRaidAsc(String gym, String region);
    List<RaidEntity> findByPokemonAndRegionOrderByEndOfRaidAsc(String pokemon, String region);
    List<RaidEntity> findByRegionOrderByPokemonAscEndOfRaidAsc(String region);
    List<RaidEntity> findByRegion(String region);
}
