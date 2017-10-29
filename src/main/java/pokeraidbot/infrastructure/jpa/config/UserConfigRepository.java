package pokeraidbot.infrastructure.jpa.config;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, String> {
}
