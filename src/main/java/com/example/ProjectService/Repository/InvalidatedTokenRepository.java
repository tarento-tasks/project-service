
package com.example.ProjectService.Repository;

import com.example.ProjectService.Model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    Optional<InvalidatedToken> findByToken(String token);
}