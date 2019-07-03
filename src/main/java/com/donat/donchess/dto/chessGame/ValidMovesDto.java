package com.donat.donchess.dto.chessGame;

import java.util.HashSet;
import java.util.Set;

public class ValidMovesDto {

    private Long chessGameId;

    private Set<CoordinateDto> validMoves = new HashSet<>();

    public Long getChessGameId() {
        return chessGameId;
    }

    public void setChessGameId(Long chessGameId) {
        this.chessGameId = chessGameId;
    }

    public Set<CoordinateDto> getValidMoves() {
        return validMoves;
    }

    public void setValidMoves(Set<CoordinateDto> validMoves) {
        this.validMoves = validMoves;
    }
}
