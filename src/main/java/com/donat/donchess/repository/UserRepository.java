package com.donat.donchess.repository;

import com.donat.donchess.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

    public Optional<User> findByAuthenticationToken(String token);
}
