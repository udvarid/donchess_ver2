package com.donat.donchess.model.logic;

import com.donat.donchess.dto.ChessMoveDto;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MoveValidator {
    public Set<Coordinate> validateMove(ChessTable chessTable, Coordinate coordinateOfFigure) {

        Set<Coordinate> validMoves = new HashSet<>();

        //TODO find figure to move

        //TODO find all possible moves of this figure - first filter

        //TODO check the blocked cells / 1. is there blocked cells in the way / 2. destination cell is empty or an enemy is on there - second filter

        //TODO special move 1: en passan check

        //TODO special move 2: castling check

        return validMoves;
    }
}
