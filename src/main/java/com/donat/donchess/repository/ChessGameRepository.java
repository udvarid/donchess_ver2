package com.donat.donchess.repository;

import com.donat.donchess.domain.ChessGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChessGameRepository extends JpaRepository<ChessGame, Long> {
}
