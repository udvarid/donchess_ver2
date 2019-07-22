package com.donat.donchess.chessgame;

import com.donat.donchess.AbstractApiTest;
import org.junit.Test;

public class ChessGameEndpointTest extends AbstractApiTest {

    @Test
    public void makeMoveTest() {

        //Teszt adatokon keresztül felvitt játékban lépni, eredményt csekkolni
        //teszt: lépés megtörtént e
        //teszt: lépés számláló, moved flag, ütés, gyalog mozgás, etc flag átállítódott e

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


}
