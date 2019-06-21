package com.donat.donchess.service;

import com.donat.donchess.dto.ChessMoveDto;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.ChessAndMateJudge;
import com.donat.donchess.model.logic.DrawJudge;
import com.donat.donchess.model.logic.MoveValidator;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@Transactional
public class GameMasterService {

    private TableBuilderService tableBuilderService;
    private MoveValidator moveValidator;
    private DrawJudge drawJudge;
    private ChessAndMateJudge chessAndMateJudge;

    public GameMasterService(TableBuilderService tableBuilderService,
                             MoveValidator moveValidator,
                             DrawJudge drawJudge,
                             ChessAndMateJudge chessAndMateJudge) {
        this.tableBuilderService = tableBuilderService;
        this.moveValidator = moveValidator;
        this.drawJudge = drawJudge;
        this.chessAndMateJudge = chessAndMateJudge;
    }

    public boolean validateMove(ChessMoveDto chessMoveDto) {

        //TODO User validálása: 1, egyáltalán játszik e a játékban
        //TODO User validálása: 2, aktív e a játék
        //TODO User validálása: 3, ő jön e a

        //TODO moveId validálása: ez e a következő lépés
        //TODO alap validálások: X, Y 1 és 8 között van e
        //TODO speciális jelölők okésak e

        ChessTable chessTable = tableBuilderService.buildTable(chessMoveDto.getGameId());

        Coordinate coordinateOfFigure = new Coordinate();
        coordinateOfFigure.setX(chessMoveDto.getMoveFromX());
        coordinateOfFigure.setY(chessMoveDto.getMoveFromY());

        Set<Coordinate> validMoves = moveValidator.validateMove(chessTable, coordinateOfFigure);

        //TODO a visszakapott halmaz alapján megnézzük, hogy valid e a lépés

        //TODO sakktábla tükrözése, de itt lđeléptetjük a bábut
        ChessTable chessTableForCheck = new ChessTable();

        //a színt kiszedni a ChessGame objectből
        boolean inChess = chessAndMateJudge.inChessSituation(Color.WHITE, chessTableForCheck);


        return false;
    }

    public void makeMove(ChessMoveDto chessMoveDto) {
        //TODO makemove method
    }

    public boolean drawCheck(Long chessGameid) {
        return drawJudge.checkDraw(chessGameid);
    }

    public boolean checkMateCheck() {
        //TODO checkMate checking - no idea how
        return false;
    }

}
