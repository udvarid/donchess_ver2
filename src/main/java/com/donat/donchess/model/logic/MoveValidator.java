package com.donat.donchess.model.logic;

import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class MoveValidator {


    public Set<Coordinate> validateMove(ChessTable chessTable, Coordinate coordinateOfFigure) {
        Figure figureToMove = findFigure(chessTable.getFigures(), coordinateOfFigure);
        Set<Coordinate> validMoves = new HashSet<>();

        if (figureToMove.getFigureType().equals(ChessFigure.PAWN)) {
            fillValidMovesForPawn(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.ROOK)) {
            fillValidMovesForRook(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.BISHOP)) {
            fillValidMovesForBishop(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.KNIGHT)) {
            fillValidMovesForKnight(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.QUEEN)) {
            fillValidMovesForRook(validMoves, figureToMove, chessTable);
            fillValidMovesForBishop(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.KING)) {
            fillValidMovesForKing(validMoves, figureToMove, chessTable);
        }

        return validMoves;
    }

    private void fillValidMovesForKing(Set<Coordinate> validMoves, Figure figureToMove, ChessTable chessTable) {
        for (int i = -1; i <= +1; i++) {
            for (int j = -1; j <= +1; j++) {
                if (i != 0 || j != 0) {
                    Coordinate coordinate =
                            new Coordinate(figureToMove.getCoordX() + i, figureToMove.getCoordY() + j);
                    Figure figureToCheck = findFigure(chessTable.getFigures(), coordinate);
                    validGoalAndAdd(validMoves, coordinate, figureToMove, figureToCheck);
                }
            }
        }

        //TODO - checking the castling case
    }

    private void fillValidMovesForKnight(Set<Coordinate> validMoves, Figure figureToMove, ChessTable chessTable) {
        //TODO - move into 8 different cells separatly, checking the validation (end of table, own figure)C
        Coordinate knightMove1 = new Coordinate(figureToMove.getCoordX() + 1, figureToMove.getCoordY() + 2);
        Coordinate knightMove2 = new Coordinate(figureToMove.getCoordX() + 2, figureToMove.getCoordY() + 1);
        Coordinate knightMove3 = new Coordinate(figureToMove.getCoordX() - 1, figureToMove.getCoordY() - 2);
        Coordinate knightMove4 = new Coordinate(figureToMove.getCoordX() - 2, figureToMove.getCoordY() - 1);
        Coordinate knightMove5 = new Coordinate(figureToMove.getCoordX() + 1, figureToMove.getCoordY() - 2);
        Coordinate knightMove6 = new Coordinate(figureToMove.getCoordX() + 2, figureToMove.getCoordY() - 1);
        Coordinate knightMove7 = new Coordinate(figureToMove.getCoordX() - 1, figureToMove.getCoordY() + 2);
        Coordinate knightMove8 = new Coordinate(figureToMove.getCoordX() - 2, figureToMove.getCoordY() + 1);
        Figure goalFigure1 = findFigure(chessTable.getFigures(), knightMove1);
    }

    private void fillValidMovesForBishop(Set<Coordinate> validMoves, Figure figureToMove, ChessTable chessTable) {
        //TODO - move into 4 different direction separatly.
        //TODO - in case of block (e.g.: end of table, own figure), break the loop
    }

    private void fillValidMovesForRook(Set<Coordinate> validMoves, Figure figureToMove, ChessTable chessTable) {
        //TODO - move into 4 different direction separatly.
        //TODO - in case of block (e.g.: end of table, own figure), break the loop
    }

    private void fillValidMovesForPawn(Set<Coordinate> validMoves, Figure figureToMove, ChessTable chessTable) {
        int mirror = figureToMove.getColor().equals(Color.WHITE) ? 1 : -1;
        Figure figureToCheck = null;

        Coordinate oneStepfw = new Coordinate(figureToMove.getCoordX(), figureToMove.getCoordY() + 1 * mirror);
        figureToCheck = findFigure(chessTable.getFigures(), oneStepfw);
        validGoalForPawnMovingAndAdd(validMoves, oneStepfw, figureToMove, figureToCheck);

        Coordinate twoStepfw = new Coordinate(figureToMove.getCoordX(), figureToMove.getCoordY() + 2 * mirror);
        figureToCheck = findFigure(chessTable.getFigures(), twoStepfw);
        if (movingFromStartingPosition(figureToMove)) {
            validGoalForPawnMovingAndAdd(validMoves, twoStepfw, figureToMove, figureToCheck);
        }

        Coordinate attackLeft = new Coordinate(figureToMove.getCoordX() - 1, figureToMove.getCoordY() + 1 * mirror);
        Coordinate attackLeftEnPassan =
                new Coordinate(figureToMove.getCoordX() - 1, figureToMove.getCoordY());
        pawnHitting(validMoves, figureToMove, chessTable, attackLeft, attackLeftEnPassan);


        Coordinate attackRight = new Coordinate(figureToMove.getCoordX() + 1, figureToMove.getCoordY() + 1 * mirror);
        Coordinate attackRightEnPassan =
                new Coordinate(figureToMove.getCoordX() + 1, figureToMove.getCoordY());
        pawnHitting(validMoves, figureToMove, chessTable, attackRight, attackRightEnPassan);

    }

    private void pawnHitting(Set<Coordinate> validMoves, Figure figureToMove, ChessTable chessTable, Coordinate attackLeft, Coordinate attackLeftEnPassan) {
        Figure figureToCheck;
        Figure enPassanFigureToCheck;
        figureToCheck = findFigure(chessTable.getFigures(), attackLeft);
        enPassanFigureToCheck = findFigure(chessTable.getFigures(), attackLeftEnPassan);

        if (figureToCheck != null) {
            validGoalForPawnHittingAndAdd(validMoves, attackLeft, figureToMove, figureToCheck);
        } else if (chessTable.isLastMoveWasDoublePawn() &&
                enPassanFigureToCheck != null &&
                enPassanFigureToCheck.getFigureType().equals(ChessFigure.PAWN) &&
                chessTable.getColumnIndexIfLastMoveWasDoublePawn() == enPassanFigureToCheck.getCoordX()) {
            validGoalForPawnMovingAndAdd(validMoves, attackLeft, figureToMove, null);
        }
    }

    private boolean movingFromStartingPosition(Figure figureToMove) {
        return figureToMove.getColor().equals(Color.WHITE) && figureToMove.getCoordY() == 2 ||
                figureToMove.getColor().equals(Color.BLACK) && figureToMove.getCoordY() == 7;
    }

    private void validGoalForPawnMovingAndAdd(Set<Coordinate> validMoves, Coordinate aimCoord, Figure figureToMove, Figure figureToCheck) {
        if (validCoordinate(aimCoord) && figureToCheck == null) {
            validMoves.add(aimCoord);
        }
    }

    private void validGoalForPawnHittingAndAdd(Set<Coordinate> validMoves, Coordinate aimCoord, Figure figureToMove, Figure figureToCheck) {
        if (validCoordinate(aimCoord) &&
                figureToCheck != null &&
                !figureToCheck.getColor().equals(figureToMove.getColor())) {
            validMoves.add(aimCoord);
        }
    }


    private boolean validGoalAndAdd(Set<Coordinate> validMoves, Coordinate aimCoord, Figure figureToMove, Figure figureToCheck) {
        if (validCoordinate(aimCoord) &&
                (figureToCheck == null || !figureToCheck.getColor().equals(figureToMove.getColor()))) {
            validMoves.add(aimCoord);
            return true;
        }
        return false;
    }

    private boolean validCoordinate(Coordinate aimCoord) {
        return aimCoord.getY() >= 1 &&
                aimCoord.getY() <= 8 &&
                aimCoord.getX() >= 1 &&
                aimCoord.getX() <= 8;
    }


    private Figure findFigure(Set<Figure> figures, Coordinate coordinateOfFigure) {
        return figures
                .stream()
                .filter(figure -> figure.getCoordX() == coordinateOfFigure.getX() &&
                        figure.getCoordY() == coordinateOfFigure.getY())
                .findAny()
                .orElse(null);
    }
}
