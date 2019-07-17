package com.donat.donchess.chessgame;

import com.donat.donchess.AncestorAbstract;
import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.User;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.MoveValidator;
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
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ValidMovesTest extends AncestorAbstract {

    private ChessGame chessGame;
    private ChessTable chessTable;
    private User userOne;
    private User userTwo;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChessGameRepository chessGameRepository;
    @Autowired
    private TableBuilderService tableBuilderService;
    @Autowired
    private ValidMoveInspector validMoveInspector;
    @Autowired
    private MoveValidator moveValidator;


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
        Set<ValidMove> validMoves = giveValidMoves(pawnOne);
        assertEquals(2, validMoves.size());
        assertTrue(validMovesContains(validMoves, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves, new Coordinate(5, 4)));

        pawnOne.setMoved(true);

        Set<ValidMove> validMoves2 = giveValidMoves(pawnOne);
        assertEquals(1, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(5, 3)));

        //Hitting possibilites
        Figure enemyPawn = giveFigure(4, 7);
        enemyPawn.setCoordY(3);
        Set<ValidMove> validMoves3 = giveValidMoves(pawnOne);
        assertEquals(2, validMoves3.size());
        assertTrue(validMovesContains(validMoves3, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(4, 3)));

        Figure enemyPawn2 = giveFigure(6, 7);
        enemyPawn2.setCoordY(3);
        Set<ValidMove> validMoves4 = giveValidMoves(pawnOne);
        assertEquals(3, validMoves4.size());
        assertTrue(validMovesContains(validMoves4, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(4, 3)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(6, 3)));

        //Blocking
        Figure enemyPawn3 = giveFigure(5, 7);
        enemyPawn3.setCoordY(3);
        Set<ValidMove> validMoves5 = giveValidMoves(pawnOne);
        assertEquals(2, validMoves5.size());
        assertTrue(validMovesContains(validMoves5, new Coordinate(4, 3)));
        assertTrue(validMovesContains(validMoves5, new Coordinate(6, 3)));

        //ownFigure in hitting poisition
        enemyPawn2.setColor(Color.WHITE);
        Set<ValidMove> validMoves6 = giveValidMoves(pawnOne);
        assertEquals(1, validMoves6.size());
        assertTrue(validMovesContains(validMoves6, new Coordinate(4, 3)));

    }


    @Test
    public void validMovesOfRookTest() {
        Figure rook = giveFigure(1, 1);
        assertTrue(rook.getFigureType().equals(ChessFigure.ROOK));
        Set<ValidMove> validMoves = giveValidMoves(rook);
        assertEquals(0, validMoves.size());

        killFigure(1, 2);
        Set<ValidMove> validMoves2 = giveValidMoves(rook);
        assertEquals(6, validMoves2.size());
        for (int i = 2; i <= 7; i++) {
            assertTrue(validMovesContains(validMoves2, new Coordinate(1, i)));
        }

        Figure myQueen = giveFigure(4, 1);
        assertTrue(myQueen.getFigureType().equals(ChessFigure.QUEEN));
        myQueen.setCoordX(1);
        myQueen.setCoordY(5);
        Set<ValidMove> validMoves3 = giveValidMoves(rook);
        assertEquals(3, validMoves3.size());
        for (int i = 2; i <= 4; i++) {
            assertTrue(validMovesContains(validMoves3, new Coordinate(1, i)));
        }

        assertTrue(myQueen.getColor().equals(Color.WHITE));
        myQueen.setColor(Color.BLACK);
        Set<ValidMove> validMoves4 = giveValidMoves(rook);
        assertEquals(4, validMoves4.size());
        for (int i = 2; i <= 5; i++) {
            assertTrue(validMovesContains(validMoves4, new Coordinate(1, i)));
        }

        rook.setCoordY(6);
        rook.setCoordX(5);
        Set<ValidMove> validMoves5 = giveValidMoves(rook);
        assertEquals(11, validMoves5.size());
    }

    @Test
    public void validMovesOfKnightTest() {
        Figure knight = giveFigure(2, 1);
        assertEquals(knight.getFigureType(), ChessFigure.KNIGHT);
        Set<ValidMove> validMoves = giveValidMoves(knight);
        assertEquals(2, validMoves.size());
        assertTrue(validMovesContains(validMoves, new Coordinate(1, 3)));
        assertTrue(validMovesContains(validMoves, new Coordinate(3, 3)));

        knight.setCoordX(4);
        knight.setCoordY(4);
        Set<ValidMove> validMoves2 = giveValidMoves(knight);
        assertEquals(6, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(2, 3)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(6, 3)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(2, 5)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(6, 5)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(3, 6)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(5, 6)));

        Figure myPawnToChangeColor = giveFigure(3, 2);
        assertTrue(myPawnToChangeColor.getColor().equals(Color.WHITE));
        myPawnToChangeColor.setColor(Color.BLACK);
        Set<ValidMove> validMoves3 = giveValidMoves(knight);
        assertEquals(7, validMoves3.size());
        assertTrue(validMovesContains(validMoves3, new Coordinate(3, 2)));
    }

    @Test
    public void validMovesOfBishopTest() {
        Figure bishop = giveFigure(3, 1);
        assertTrue(bishop.getFigureType().equals(ChessFigure.BISHOP));
        Set<ValidMove> validMoves = giveValidMoves(bishop);
        assertEquals(0, validMoves.size());

        killFigure(4, 2);
        Set<ValidMove> validMoves2 = giveValidMoves(bishop);
        assertEquals(5, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(4, 2)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(6, 4)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(7, 5)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(8, 6)));

        killFigure(2, 2);
        Set<ValidMove> validMoves3 = giveValidMoves(bishop);
        assertEquals(7, validMoves3.size());
        assertTrue(validMovesContains(validMoves3, new Coordinate(2, 2)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(1, 3)));

        Figure pawn = giveFigure(3, 2);
        assertEquals(pawn.getColor(), Color.WHITE);
        assertEquals(pawn.getFigureType(), ChessFigure.PAWN);
        pawn.setCoordX(6);
        pawn.setCoordY(4);
        Set<ValidMove> validMoves4 = giveValidMoves(bishop);
        assertEquals(4, validMoves4.size());
        assertTrue(validMovesContains(validMoves4, new Coordinate(2, 2)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(1, 3)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(4, 2)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(5, 3)));

        pawn.setColor(Color.BLACK);
        Set<ValidMove> validMoves5 = giveValidMoves(bishop);
        assertEquals(5, validMoves5.size());
        assertTrue(validMovesContains(validMoves5, new Coordinate(2, 2)));
        assertTrue(validMovesContains(validMoves5, new Coordinate(1, 3)));
        assertTrue(validMovesContains(validMoves5, new Coordinate(4, 2)));
        assertTrue(validMovesContains(validMoves5, new Coordinate(5, 3)));
        assertTrue(validMovesContains(validMoves5, new Coordinate(6, 4)));
    }

    @Test
    public void validMovesOfQueenTest() {
        Figure queen = giveFigure(4, 1);
        assertTrue(queen.getFigureType().equals(ChessFigure.QUEEN));
        Set<ValidMove> validMoves = giveValidMoves(queen);
        assertEquals(0, validMoves.size());

        killFigure(4, 2);
        Set<ValidMove> validMoves2 = giveValidMoves(queen);
        assertEquals(6, validMoves2.size());
        for (int i = 1; i <= 6; i++) {
            assertTrue(validMovesContains(validMoves2, new Coordinate(4, 1 + i)));
        }

        killFigure(5, 2);
        killFigure(3, 2);
        Set<ValidMove> validMoves3 = giveValidMoves(queen);
        assertEquals(13, validMoves3.size());
        for (int i = 1; i <= 6; i++) {
            assertTrue(validMovesContains(validMoves3, new Coordinate(4, 1 + i)));
        }
        for (int i = 1; i <= 3; i++) {
            assertTrue(validMovesContains(validMoves3, new Coordinate(4 - i, 1 + i)));
        }
        for (int i = 1; i <= 4; i++) {
            assertTrue(validMovesContains(validMoves3, new Coordinate(4 + i, 1 + i)));
        }

        Figure pawn = giveFigure(4, 7);
        assertEquals(pawn.getColor(), Color.BLACK);
        assertEquals(pawn.getFigureType(), ChessFigure.PAWN);
        pawn.setCoordY(3);
        Set<ValidMove> validMoves4 = giveValidMoves(queen);
        assertEquals(9, validMoves4.size());
        for (int i = 1; i <= 2; i++) {
            assertTrue(validMovesContains(validMoves4, new Coordinate(4, 1 + i)));
        }
        for (int i = 1; i <= 3; i++) {
            assertTrue(validMovesContains(validMoves4, new Coordinate(4 - i, 1 + i)));
        }
        for (int i = 1; i <= 4; i++) {
            assertTrue(validMovesContains(validMoves4, new Coordinate(4 + i, 1 + i)));
        }

        pawn.setColor(Color.WHITE);
        Set<ValidMove> validMoves5 = giveValidMoves(queen);
        assertEquals(8, validMoves5.size());
        for (int i = 1; i <= 1; i++) {
            assertTrue(validMovesContains(validMoves5, new Coordinate(4, 1 + i)));
        }
        for (int i = 1; i <= 3; i++) {
            assertTrue(validMovesContains(validMoves5, new Coordinate(4 - i, 1 + i)));
        }
        for (int i = 1; i <= 4; i++) {
            assertTrue(validMovesContains(validMoves5, new Coordinate(4 + i, 1 + i)));
        }

    }

    @Test
    public void validMovesOfKingTest() {
        Figure king = giveFigure(5, 1);
        assertEquals(king.getFigureType(), ChessFigure.KING);
        Set<ValidMove> validMoves = giveValidMoves(king);
        assertEquals(0, validMoves.size());

        killFigure(5, 2);
        killFigure(6, 1);
        Set<ValidMove> validMoves2 = giveValidMoves(king);
        assertEquals(2, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(5, 2)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(6, 1)));

        Figure pawn = giveFigure(6, 2);
        assertEquals(pawn.getColor(), Color.WHITE);
        assertEquals(pawn.getFigureType(), ChessFigure.PAWN);
        pawn.setColor(Color.BLACK);
        Set<ValidMove> validMoves3 = giveValidMoves(king);
        assertEquals(3, validMoves3.size());
        assertTrue(validMovesContains(validMoves3, new Coordinate(5, 2)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(6, 1)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(6, 2)));


    }

    @Test
    public void validEnPassanTest() {
        Figure pawnOne = giveFigure(5, 2);
        assertTrue(pawnOne.getColor().equals(Color.WHITE));
        assertTrue(pawnOne.getFigureType().equals(ChessFigure.PAWN));

        Figure pawnTwo = giveFigure(4, 7);
        assertTrue(pawnTwo.getColor().equals(Color.BLACK));
        assertTrue(pawnTwo.getFigureType().equals(ChessFigure.PAWN));

        pawnOne.setCoordY(5);
        pawnOne.setMoved(true);
        pawnTwo.setCoordY(5);
        pawnTwo.setMoved(true);

        Set<ValidMove> validMoves = giveValidMoves(pawnOne);
        assertEquals(1, validMoves.size());
        assertTrue(validMovesContains(validMoves, new Coordinate(5, 6)));

        chessTable.setLastMoveWasDoublePawn(true);
        chessTable.setColumnIndexIfLastMoveWasDoublePawn(4);
        Set<ValidMove> validMoves2 = giveValidMoves(pawnOne);
        assertEquals(2, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(5, 6)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(4, 6)));

    }

    @Test
    public void validCastlingTest() {
        Figure king = giveFigure(5, 1);
        assertTrue(king.getColor().equals(Color.WHITE));
        assertTrue(king.getFigureType().equals(ChessFigure.KING));

        killFigure(6, 1);
        killFigure(7, 1);
        Set<ValidMove> validMoves = giveValidMoves(king);
        assertEquals(2, validMoves.size());
        assertTrue(validMovesContains(validMoves, new Coordinate(6, 1)));
        assertTrue(validMovesContains(validMoves, new Coordinate(7, 1)));

        killFigure(4, 1);
        killFigure(3, 1);
        Set<ValidMove> validMoves2 = giveValidMoves(king);
        assertEquals(3, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(6, 1)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(7, 1)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(4, 1)));

        killFigure(2, 1);
        Set<ValidMove> validMoves3 = giveValidMoves(king);
        assertEquals(4, validMoves3.size());
        assertTrue(validMovesContains(validMoves3, new Coordinate(6, 1)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(7, 1)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(4, 1)));
        assertTrue(validMovesContains(validMoves3, new Coordinate(3, 1)));

        killFigure(1, 1);
        Set<ValidMove> validMoves4 = giveValidMoves(king);
        assertEquals(3, validMoves4.size());
        assertTrue(validMovesContains(validMoves4, new Coordinate(6, 1)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(7, 1)));
        assertTrue(validMovesContains(validMoves4, new Coordinate(4, 1)));

        king.setMoved(true);
        Set<ValidMove> validMoves5 = giveValidMoves(king);
        assertEquals(2, validMoves5.size());
        assertTrue(validMovesContains(validMoves5, new Coordinate(6, 1)));
        assertTrue(validMovesContains(validMoves5, new Coordinate(4, 1)));
    }


    @Test
    public void canNotMoveintoChessTest() {
        Figure king = giveFigure(5, 1);
        assertTrue(king.getColor().equals(Color.WHITE));
        assertTrue(king.getFigureType().equals(ChessFigure.KING));

        Figure enemyRook = giveFigure(1, 8);
        assertTrue(enemyRook.getColor().equals(Color.BLACK));
        assertTrue(enemyRook.getFigureType().equals(ChessFigure.ROOK));

        killFigure(5, 2);
        killFigure(4, 2);

        Set<ValidMove> validMoves = giveValidMoves(king);
        assertEquals(2, validMoves.size());
        assertTrue(validMovesContains(validMoves, new Coordinate(5, 2)));
        assertTrue(validMovesContains(validMoves, new Coordinate(4, 2)));

        enemyRook.setCoordX(4);
        enemyRook.setCoordY(6);

        Set<ValidMove> validMovesForRook = giveValidMoves(enemyRook);
        assertTrue(validMovesContains(validMovesForRook, new Coordinate(4, 2)));

        assertEquals(1, giveMoveable(validMoves, king));
        assertNull(tryToMove(4, 2, king));
        assertNotNull(tryToMove(5, 2, king));

        Figure queen = giveFigure(4, 1);
        assertTrue(queen.getColor().equals(Color.WHITE));
        assertTrue(queen.getFigureType().equals(ChessFigure.QUEEN));

        queen.setCoordX(5);
        queen.setCoordY(2);

        enemyRook.setCoordX(5);

        Set<ValidMove> validMoves2 = giveValidMoves(king);
        assertEquals(2, validMoves2.size());
        assertTrue(validMovesContains(validMoves2, new Coordinate(4, 1)));
        assertTrue(validMovesContains(validMoves2, new Coordinate(4, 2)));

        Set<ValidMove> validMoves3 = giveValidMoves(queen);
        assertEquals(4, giveMoveable(validMoves3, queen));
        assertNotNull(tryToMove(5, 3, queen));
        assertNotNull(tryToMove(5, 4, queen));
        assertNotNull(tryToMove(5, 5, queen));
        assertNotNull(tryToMove(5, 6, queen));

    }

    @Test
    public void canNotMoveInChessWithOtherFiguresTest() {
        Figure king = giveFigure(5, 1);
        assertTrue(king.getColor().equals(Color.WHITE));
        assertTrue(king.getFigureType().equals(ChessFigure.KING));

        Figure queen = giveFigure(4, 1);
        assertTrue(queen.getColor().equals(Color.WHITE));
        assertTrue(queen.getFigureType().equals(ChessFigure.QUEEN));

        Figure knight = giveFigure(7, 1);
        assertTrue(knight.getColor().equals(Color.WHITE));
        assertTrue(knight.getFigureType().equals(ChessFigure.KNIGHT));


        Figure enemyRook = giveFigure(1, 8);
        assertTrue(enemyRook.getColor().equals(Color.BLACK));
        assertTrue(enemyRook.getFigureType().equals(ChessFigure.ROOK));

        killFigure(5, 2);//gyalog leszedése
        killFigure(4, 2);//gyalog leszedése
        killFigure(3, 1);//futó leszedése
        killFigure(6, 1);//futó leszedése

        enemyRook.setCoordX(5);
        enemyRook.setCoordY(6);

        Set<ValidMove> validMoves = giveValidMoves(king);

        assertEquals(2, giveMoveable(validMoves, king));
        assertNull(tryToMove(5, 2, king));
        assertNotNull(tryToMove(4, 2, king));
        assertNotNull(tryToMove(6, 1, king));

        //végig megyünk az összes világos bábun és elkérjük a leléphető lépésket
        //Csak a királynő és a ló tud a lépni, a király elé lépve, hogy megakadályozza a sakkot
        for (Figure figure : chessTable.getFigures()) {
            if (figure.getColor().equals(Color.WHITE) && !figure.equals(king)) {
                if (figure.equals(queen)) {
                    assertEquals(1, giveMoveable(giveValidMoves(figure), figure));
                    assertNotNull(tryToMove(5, 2, figure));
                } else if (figure.equals(knight)) {
                    assertEquals(1, giveMoveable(giveValidMoves(figure), figure));
                    assertNotNull(tryToMove(5, 2, figure));
                } else {
                    assertEquals(0, giveMoveable(giveValidMoves(figure), figure));
                }
            }
        }

    }

    @Test
    public void givingChessTest() {

    }

    @Test
    public void validPromotingTest() {


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

    private Set<ValidMove> giveValidMoves(Figure figure) {
        return validMoveInspector.allValidMoves(chessTable, getCoordinate(figure));
    }

    private void killFigure(int x, int y) {
        Figure figureToKill = giveFigure(x, y);
        if (figureToKill != null) {
            chessTable.getFigures().remove(figureToKill);
        }
    }


    private ValidMove tryToMove(int x, int y, Figure figure) {
        return moveValidator.validmove(chessTable, giveChessMoveDto(x, y, figure));
    }

    private int giveMoveable(Set<ValidMove> validMoves, Figure figure) {
        int result = 0;
        for (ValidMove validMove : validMoves) {
            ChessMoveDto chessMoveDto = giveChessMoveDtoVer2(figure, validMove);
            ValidMove validMoveResult = moveValidator.validmove(chessTable, chessMoveDto);
            if (validMoveResult != null) {
                result++;
            }
        }
        return result;
    }


    private ChessMoveDto giveChessMoveDtoVer2(Figure figure, ValidMove validMove) {
        return giveChessMoveDto(validMove.getCoordinate().getX(),
                validMove.getCoordinate().getY(), figure);
    }

    private ChessMoveDto giveChessMoveDto(int x, int y, Figure figure) {
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        chessMoveDto.setGameId(chessTable.getChessGameId());
        chessMoveDto.setMoveId(chessTable.getActualMoveNumber());
        chessMoveDto.setMoveFromX(figure.getCoordX());
        chessMoveDto.setMoveFromY(figure.getCoordY());
        chessMoveDto.setMoveToX(x);
        chessMoveDto.setMoveToY(y);
        return chessMoveDto;
    }


}
