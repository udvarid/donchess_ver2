package com.donat.donchess.repository;

import com.donat.donchess.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

    public Optional<User> findByAuthenticationToken(String token);
}
