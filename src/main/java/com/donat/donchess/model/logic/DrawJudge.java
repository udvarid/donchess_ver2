package com.donat.donchess.model.logic;

import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Figure;
import org.springframework.stereotype.Service;

@Service
public class DrawJudge {
    public boolean checkDraw(ChessTable chessTable) {

        if (fiftyMoveRule(chessTable)) {
            return true;
        }

        if (inSufficientMaterials(chessTable)) {
            return true;
        }

        if (noPossibleMove(chessTable)) {
            return true;
        }

        if (threeFoldRepetition(chessTable)) {
            return true;
        }

        return false;
    }

    private boolean threeFoldRepetition(ChessTable chessTable) {
        //TODO leprogramozni

        //elkérni a chessmove history-t és az elejétől kezdve felépíteni a táblát

        //ha a mostani állással megegyező állást (darabszám, koordináták, típus egyezősége) találunk 2szer, akkor true


        return false;
    }

    private boolean noPossibleMove(ChessTable chessTable) {
        for (Figure figure : chessTable.getFigures()) {
            if (figure.getColor().equals(chessTable.getWhoIsNext())) {
                //TODO leprogramozni
                //elkérjük az adott figurára az összes lehetséges lépést

                //lelépjük és megnézzük, hogy sakkmentes e a lépés

                //ha csak egy ilyet is találunk, akkor örülünk és false a válasz
            }
        }
        return true;
    }

    private boolean inSufficientMaterials(ChessTable chessTable) {
        return maxTwoPiecesOfEachSide(chessTable) &&
                onlyKingOrBishopOrKnightFiguresCanBeFound(chessTable);
    }

    private boolean onlyKingOrBishopOrKnightFiguresCanBeFound(ChessTable chessTable) {
        for (Figure figure : chessTable.getFigures()) {
            if (!figure.getFigureType().equals(ChessFigure.KING) &&
                    !figure.getFigureType().equals(ChessFigure.BISHOP) &&
                    !figure.getFigureType().equals(ChessFigure.KNIGHT)) {
                return false;
            }
        }


        return true;
    }

    private boolean maxTwoPiecesOfEachSide(ChessTable chessTable) {
        int numberOfWhite = 0;
        int numberOfBlack = 0;
        for (Figure figure : chessTable.getFigures()) {
            if (figure.getColor().equals(Color.WHITE)) {
                numberOfWhite++;
            } else {
                numberOfBlack++;
            }
            if (numberOfBlack > 2 || numberOfWhite > 2) {
                return false;
            }
        }
        return true;
    }

    private boolean fiftyMoveRule(ChessTable chessTable) {
        return chessTable.getActualMoveNumber() - chessTable.getLastPawnMoveNumber() >= 50 &&
                chessTable.getActualMoveNumber() - chessTable.getLastHitMoveNumber() >= 50;
    }
}
