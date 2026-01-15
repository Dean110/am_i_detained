package dev.benwilliams.am_i_detained.repository;

import dev.benwilliams.am_i_detained.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}
