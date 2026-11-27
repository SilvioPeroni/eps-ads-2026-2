package br.edu.fatecfranca.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecfranca.api.entities.User;

import java.util.Optional;

public interface UserRepository
       extends JpaRepository<User, Long> {
       Optional<User> findByUsernameOrEmail(String username, String email);
       boolean existsByUsername(String username);
       boolean existsByUsernameAndIdNot(String username, Long id);
       boolean existsByEmail(String email);
       boolean existsByEmailAndIdNot(String email, Long id);
}
