package pokeraidbot.infrastructure.jpa.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface UserConfigRepository extends JpaRepository<UserConfig, String> {
}
