package com.donat.donchess.service;

import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.QChessGame;
import com.donat.donchess.domain.User;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.SpecialMoveType;
import com.donat.donchess.dto.ChessMoveDto;
import com.donat.donchess.exceptions.InvalidException;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.ChessAndMateJudge;
import com.donat.donchess.model.logic.DrawJudge;
import com.donat.donchess.model.logic.MoveValidator;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Set;

@Service
@Transactional
public class GameMasterService {

    private TableBuilderService tableBuilderService;
    private MoveValidator moveValidator;
    private DrawJudge drawJudge;
    private ChessAndMateJudge chessAndMateJudge;
    private SecurityService securityService;

    @Autowired
    private Provider<EntityManager> entityManager;

    public GameMasterService(TableBuilderService tableBuilderService,
                             MoveValidator moveValidator,
                             DrawJudge drawJudge,
                             ChessAndMateJudge chessAndMateJudge,
                             SecurityService securityService) {
        this.tableBuilderService = tableBuilderService;
        this.moveValidator = moveValidator;
        this.drawJudge = drawJudge;
        this.chessAndMateJudge = chessAndMateJudge;
        this.securityService = securityService;
    }

    public boolean validateMove(ChessMoveDto chessMoveDto) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChessGame chessGameFromQ = QChessGame.chessGame;

        User user = securityService.getChallenger();
        ChessGame chessGame = query
                .selectFrom(chessGameFromQ)
                .where(chessGameFromQ.id.eq(chessMoveDto.getGameId()))
                .fetchOne();
        if (!userIsPlaying(user, chessGame)) {
            throw new InvalidException("Player is not playing in this game");
        }

        if (chessGame.getChessGameStatus().equals(ChessGameStatus.CLOSED)) {
            throw new InvalidException("The game is not active any more");
        }
        if (!userIsNextPlayer(user, chessGame)) {
            throw new InvalidException("The next move is expected from the other player!");
        }

        if (chessMoveDto.getMoveId() != chessGame.getLastMoveId() + 1) {
            throw new InvalidException("This is not the proper move id!");
        }

        if (!validSpecialMoveType(chessMoveDto)) {
            throw new InvalidException("Not valid special move!");
        }

        if (!validPromoteType(chessMoveDto)) {
            throw new InvalidException("Not valid promotion type!");
        }

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

    private boolean userIsNextPlayer(User user, ChessGame chessGame) {
        return chessGame.getNextMove().equals(Color.WHITE) &&
                user.getId().equals(chessGame.getUserOne().getId())
                ||
                chessGame.getNextMove().equals(Color.BLACK) &&
                        user.getId().equals(chessGame.getUserTwo().getId());
    }

    private boolean userIsPlaying(User user, ChessGame chessGame) {
        return chessGame.getUserOne().getId().equals(user.getId()) ||
                chessGame.getUserTwo().getId().equals(user.getId());
    }

    private boolean validPromoteType(ChessMoveDto chessMoveDto) {
        return chessMoveDto.getPromoteToFigure().isEmpty() ||
                EnumUtils.isValidEnum(ChessFigure.class, chessMoveDto.getPromoteToFigure());
    }

    private boolean validSpecialMoveType(ChessMoveDto chessMoveDto) {
        return EnumUtils.isValidEnum(SpecialMoveType.class, chessMoveDto.getSpecialMoveType());
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
