package com.donat.donchess.chessgame;

import com.donat.donchess.AncestorAbstract;
import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.User;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.ValidMoveInspector;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import com.donat.donchess.repository.ChessGameRepository;
import com.donat.donchess.repository.UserRepository;
import com.donat.donchess.service.TableBuilderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.*;

public class ValidMovesTest extends AncestorAbstract {

    private ChessGame chessGame;
    private ChessTable chessTable;
    private User userOne;
    private User userTwo;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChessGameRepository chessGameRepository;
    @Autowired
    TableBuilderService tableBuilderService;
    @Autowired
    ValidMoveInspector validMoveInspector;

    @Before
    public void initTableMaker() {
        userOne = new User();
        userOne.setFullname("User One");
        userOne.setEmail("Email One");
        userOne.setPassword("Password One");
        userRepository.saveAndFlush(userOne);

        userTwo = new User();
        userTwo.setFullname("User Two");
        userTwo.setEmail("Email Two");
        userTwo.setPassword("Password Two");
        userRepository.saveAndFlush(userTwo);

        chessGame = new ChessGame();
        chessGame.setResult(Result.OPEN);
        chessGame.setChessGameStatus(ChessGameStatus.OPEN);
        chessGame.setNextMove(Color.WHITE);
        chessGame.setLastMoveId(0);
        chessGame.setChessGameType(ChessGameType.NORMAL);
        chessGame.setUserOne(userOne);
        chessGame.setUserTwo(userTwo);

        ChessGame chessGameSaved = chessGameRepository.saveAndFlush(this.chessGame);


        chessTable = tableBuilderService.buildTable(chessGameSaved.getId());
    }


    @Test
    public void validMovesOfPawnTest() {
        Figure pawnOne = giveFigure(5, 2);
        assertTrue(pawnOne.getColor().equals(Color.WHITE));
        assertTrue(pawnOne.getFigureType().equals(ChessFigure.PAWN));

        //Normal moves
        Set<ValidMove> validMoves = validMoveInspector.allValidMoves(chessTable, getCoordinate(pawnOne));
        assertEquals(2, validMoves.size());
        assertTrue(validMovesContains(validMoves, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves, new Coordinate(5, 4)));

        pawnOne.setMoved(true);

        Set<ValidMove> validMoves2 = validMoveInspector.allValidMoves(chessTable, getCoordinate(pawnOne));
        assertEquals(1, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(5, 3)));

        //Hitting possibilites
        Figure enemyPawn = giveFigure(4, 7);
        enemyPawn.setCoordY(3);
        Set<ValidMove> validMoves3 = validMoveInspector.allValidMoves(chessTable, getCoordinate(pawnOne));
        assertEquals(2, validMoves3.size());
        assertTrue(validMovesContains(validMoves3, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(4, 3)));

        Figure enemyPawn2 = giveFigure(6, 7);
        enemyPawn2.setCoordY(3);
        Set<ValidMove> validMoves4 = validMoveInspector.allValidMoves(chessTable, getCoordinate(pawnOne));
        assertEquals(3, validMoves4.size());
        assertTrue(validMovesContains(validMoves4, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(4, 3)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(6, 3)));

        //Blocking
        Figure enemyPawn3 = giveFigure(5, 7);
        enemyPawn3.setCoordY(3);
        Set<ValidMove> validMoves5 = validMoveInspector.allValidMoves(chessTable, getCoordinate(pawnOne));
        assertEquals(2, validMoves5.size());
        assertTrue(validMovesContains(validMoves5, new Coordinate(4, 3)));
        assertTrue(validMovesContains(validMoves5, new Coordinate(6, 3)));

        //ownFigure in hitting poisition
        enemyPawn2.setColor(Color.WHITE);
        Set<ValidMove> validMoves6 = validMoveInspector.allValidMoves(chessTable, getCoordinate(pawnOne));
        assertEquals(1, validMoves6.size());
        assertTrue(validMovesContains(validMoves6, new Coordinate(4, 3)));

    }


    @Test
    public void validMovesOfRookTest() {

    }
    @Test
    public void validMovesOfKnightTest() {

    }
    @Test
    public void validMovesOfBishopTest() {

    }
    @Test
    public void validMovesOfQueenTest() {

    }
    @Test
    public void validMovesOfKingTest() {

    }
    @Test
    public void validEnPassanTest() {

    }
    @Test
    public void validCastlingTest() {

    }
    @Test
    public void validPromotingTest() {

    }
    @Test
    public void canNotMoveintoChessTest() {

    }
    @Test
    public void canNotMoveInChessWithOtherFiguresTest() {

    }
    @Test
    public void givingChessTest() {

    }
    @Test
    public void drawFiftyMovesTest() {

    }
    @Test
    public void drawInsufficientMaterialTest() {

    }
    @Test
    public void drawNoPossibleMoveTest() {

    }
    @Test
    public void drawThreeFoldRepetitionTest() {

    }
    @Test
    public void chessMateTest() {

    }
    @Test
    public void cloneTableTest() {

    }

    private Coordinate getCoordinate(Figure figure) {
        return new Coordinate(figure.getCoordX(), figure.getCoordY());
    }

    private boolean validMovesContains(Set<ValidMove> validMoves, Coordinate coordinate) {
        return validMoves
                .stream()
                .anyMatch(vm -> vm.getCoordinate().equals(coordinate));
    }

    private Figure giveFigure(int x, int y) {
        return validMoveInspector.findFigure(chessTable.getFigures(), new Coordinate(x, y));
    }


}
