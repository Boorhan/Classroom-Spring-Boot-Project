package com.demo.classroom.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}
