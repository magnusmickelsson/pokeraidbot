package pokeraidbot.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaidEntityRepository extends JpaRepository<RaidEntity, String> {
    RaidEntity findDistinctFirstByGymAndRegion(String gym, String region);
    List<RaidEntity> findByPokemonAndRegion(String pokemon, String region);
    List<RaidEntity> findByRegion(String region);
}
