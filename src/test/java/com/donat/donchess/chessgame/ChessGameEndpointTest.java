package com.donat.donchess.chessgame;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.FigureDto;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;


public class ChessGameEndpointTest extends AbstractApiTest {

    @Test
    public void makeMoveTest() {
        loginAsDonat1();
        ChessTableDto initStanding = gameApi.getOneChessGame(1);
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        chessMoveDto.setGameId(initStanding.getChessGameId());
        chessMoveDto.setMoveId(initStanding.getLastMoveId() + 1);
        chessMoveDto.setMoveFromX(6);
        chessMoveDto.setMoveFromY(2);
        chessMoveDto.setMoveToX(6);
        chessMoveDto.setMoveToY(3);
        gameApi.chessMove(chessMoveDto);
        ChessTableDto afterStanding = gameApi.getOneChessGame(1);

        assertEquals(initStanding.getChessGameId(), afterStanding.getChessGameId());
        assertEquals(initStanding.getLastMoveId() + 1, afterStanding.getLastMoveId());
        assertNotEquals(initStanding.getNextMove(), afterStanding.getNextMove());
        assertEquals(afterStanding.getResult(), Result.OPEN);
        assertEquals(initStanding.getFigures().size(), afterStanding.getFigures().size());
        for (FigureDto figure : afterStanding.getFigures()) {
            if (figure.getCoordX() == 6 && figure.getCoordY() == 3) {
                assertFalse(theSameFigure(figure, initStanding.getFigures()));
            } else {
                assertTrue(theSameFigure(figure, initStanding.getFigures()));
            }
        }
    }

    @Test
    public void listOfGamesTest() {

        //Leszedni az alap listát (alapban 1 db van)
        //2 teszt user játékot indít (egyik kihívja a másikat), majd leszedjük az új listát
        //teszt: nőt 1 db-al a lista mérete és a paraméterek megfelelőek

    }

    @Test
    public void chessTableTest() {
        //2 teszt user játékot indít (egyik kihívja a másikat), majd leszedjük a konkrét játékot
        //teszt: a paraméterek megfelelőek
    }

    @Test
    public void validMovesTest() {
        //2 teszt user játékot indít (egyik kihívja a másikat), majd leszedjük a konkrét játékról a valid lépéseket
        //ezt elemezzük, hogy megfelelő e
    }

    @Test
    public void validPromotingTest() {
        //2 teszt user játékot indít (egyik kihívja a másikat)
        //lépések sorozatával az egyik fehér gyalogot előre visszük a 7. sorig
        //teszt: megpróbáljuk promóció nélkül előre vinni
        //teszt: megpróbáljuk PAWN-ként előre vinni
        //teszt: megpróbáljuk KING-ként előre vinni
        //teszt: megpróbáljuk tisztként előre vinni és utána kérünk egy chesstable-t
    }

    @Test
    public void drawThreeFoldRepetitionTest() {
        //2 teszt user játékot indít (egyik kihívja a másikat)
        //csinálunk 3 repetatív állást (lovasok léptetésével)
        //teszt: lezárul e a 3. után döntetlennel
    }

    @Test
    public void chessMateTest() {
        //2 teszt user játékot indít (egyik kihívja a másikat)
        //teszt: bemattoljuk az egyiket, megnézzük, hogy véget ér e a játék megfelelően
    }

    private boolean theSameFigure(FigureDto figureToFind, Set<FigureDto> figures) {
        for (FigureDto figure : figures) {
            if (sameFigure(figure, figureToFind)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameFigure(FigureDto figure, FigureDto figureToFind) {

        return figure.getColor().equals(figureToFind.getColor()) &&
                figure.getFigureType().equals(figureToFind.getFigureType()) &&
                figure.getCoordX() == figureToFind.getCoordX() &&
                figure.getCoordY() == figureToFind.getCoordY();
    }


}
