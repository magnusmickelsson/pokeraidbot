package pokeraidbot.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaidEntityRepository extends JpaRepository<RaidEntity, String> {
    RaidEntity findDistinctFirstByGym(String gym);
    List<RaidEntity> findByPokemon(String pokemon);
}
