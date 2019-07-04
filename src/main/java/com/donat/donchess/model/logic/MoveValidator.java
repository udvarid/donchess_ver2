package com.donat.donchess.model.logic;

import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MoveValidator {

    private ChessAndMateJudge chessAndMateJudge;
    private ValidMoveInspector validMoveInspector;

    public MoveValidator(ChessAndMateJudge chessAndMateJudge,
    ValidMoveInspector  validMoveInspector) {
        this.chessAndMateJudge = chessAndMateJudge;
        this.validMoveInspector = validMoveInspector;
    }

    public ValidMove validmove(ChessTable chessTable, ChessMoveDto chessMoveDto) {

        Coordinate coordinateOfFigure = new Coordinate(chessMoveDto.getMoveFromX(), chessMoveDto.getMoveFromY());
        Coordinate aimCoordinate = new Coordinate(chessMoveDto.getMoveToX(), chessMoveDto.getMoveToY());

        Set<ValidMove> validMoves = validMoveInspector.allValidMoves(chessTable, coordinateOfFigure);

        ValidMove validMove = validMoves
                .stream()
                .filter(vm -> vm.getCoordinate().equals(aimCoordinate))
                .findFirst()
                .orElse(null);

        if (validMove != null && !chessAndMateJudge
                .inChessSituation(cloneTableAndMakeMove(chessTable, chessMoveDto))) {
            return validMove;
        }

        return null;

    }

    public ChessTable cloneTableAndMakeMove(ChessTable chessTable, ChessMoveDto chessMoveDto) {

        ChessTable cloneChessTable = new ChessTable();

        for (Figure figure : chessTable.getFigures()) {
            Figure cloneFigure = new Figure(figure.getFigureType(), figure.getColor(),
                    figure.getCoordX(), figure.getCoordY());
            cloneFigure.setMoved(figure.isMoved());
            cloneChessTable.getFigures().add(cloneFigure);
        }

        cloneChessTable
                .setWhoIsNext(chessTable.getWhoIsNext().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);


        Figure figureToMove = validMoveInspector.findFigure(cloneChessTable.getFigures(),
                new Coordinate(chessMoveDto.getMoveFromX(), chessMoveDto.getMoveFromY()));
        Figure figureToKill = validMoveInspector.findFigure(cloneChessTable.getFigures(),
                new Coordinate(chessMoveDto.getMoveToX(), chessMoveDto.getMoveToY()));

        if (figureToKill != null) {
            cloneChessTable.getFigures().remove(figureToKill);
        }
        figureToMove.setCoordX(chessMoveDto.getMoveFromX());
        figureToMove.setCoordY(chessMoveDto.getMoveFromY());
        figureToMove.setMoved(true);
        cloneChessTable.setLastMoveWasDoublePawn(false);

        if (figureToMove.getFigureType().equals(ChessFigure.PAWN) &&
                Math.abs(chessMoveDto.getMoveFromY() - chessMoveDto.getMoveToY()) == 2) {
            cloneChessTable.setColumnIndexIfLastMoveWasDoublePawn(chessMoveDto.getMoveFromX());
            cloneChessTable.setLastMoveWasDoublePawn(true);
        }

        return cloneChessTable;
    }

}
