package com.donat.donchess.user;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.ChessMove;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.SpecialMoveType;
import com.donat.donchess.model.ChessTable;
import com.donat.donchess.model.Figure;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.service.TableBuilderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.Set;

import static org.junit.Assert.*;

public class chessTableBuilderTest extends AbstractApiTest {

    @Autowired
    TableBuilderService tableBuilderService;

    @Test
    public void initSetIsOkTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        assertEquals(32, chessTable.getFigures().size());
        for (Figure figure : chessTable.getFigures()) {
            assertTrue(singleCoordinate(figure, chessTable.getFigures()));
        }

    }

    private boolean singleCoordinate(Figure figure, Set<Figure> figures) {

        for (Figure otherFigure : figures) {
            if (!otherFigure.equals(figure) &&
                    otherFigure.getCoordY() == figure.getCoordY() &&
                    otherFigure.getCoordX() == figure.getCoordX()) {
                return false;
            }
        }
        return true;
    }


    @Test
    public void findFigureTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        Figure figureTofound1 = tableBuilderService.findFigure(chessTable.getFigures(), 5, 2);
        Figure figureTofound2 = tableBuilderService.findFigure(chessTable.getFigures(), 2, 1);
        Figure figureTofound3 = tableBuilderService.findFigure(chessTable.getFigures(), 5, 1);
        Figure figureTofound4 = tableBuilderService.findFigure(chessTable.getFigures(), 8, 8);

        assertTrue(figureTofound1.getFigureType().equals(ChessFigure.PAWN));
        assertTrue(figureTofound1.getCoordX() == 5);
        assertTrue(figureTofound2.getFigureType().equals(ChessFigure.KNIGHT));
        assertTrue(figureTofound2.getColor().equals(Color.WHITE));
        assertTrue(figureTofound3.getFigureType().equals(ChessFigure.KING));
        assertTrue(figureTofound3.getCoordY() == 1);
        assertTrue(figureTofound4.getFigureType().equals(ChessFigure.ROOK));
        assertTrue(figureTofound4.getColor().equals(Color.BLACK));

    }

    @Test
    public void normalMoveTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1l);
        chessMove.setSpecialMoveType(SpecialMoveType.NORMAL);
        chessMove.setMoveFromX(5);
        chessMove.setMoveFromY(2);
        chessMove.setMoveToX(5);
        chessMove.setMoveToY(4);

        shouldFail(()-> tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveToX(), chessMove.getMoveToY()));

        tableBuilderService.makeMove(chessTable, chessMove);

        Figure movedFigure = tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveToX(), chessMove.getMoveToY());

        assertTrue(movedFigure.getCoordX() == chessMove.getMoveToX());
        assertTrue(movedFigure.getCoordY() == chessMove.getMoveToY());


    }

    @Test
    public void moveWithHittingTest() {

    }

    @Test
    public void promotingTest() {

    }

    @Test
    public void enPassanTest() {

    }

    @Test
    public void castlingTest() {

    }


}
