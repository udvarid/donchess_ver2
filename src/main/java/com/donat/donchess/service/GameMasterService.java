package com.donat.donchess.service;

import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.QChessGame;
import com.donat.donchess.domain.User;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.dto.ChessMoveDto;
import com.donat.donchess.exceptions.InvalidException;
import com.donat.donchess.exceptions.NotFoundException;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.ChessAndMateJudge;
import com.donat.donchess.model.logic.DrawJudge;
import com.donat.donchess.model.logic.MoveValidator;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import com.donat.donchess.repository.ChessGameRepository;
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
    private ChessGameRepository chessGameRepository;

    @Autowired
    private Provider<EntityManager> entityManager;

    public GameMasterService(TableBuilderService tableBuilderService,
                             MoveValidator moveValidator,
                             DrawJudge drawJudge,
                             ChessAndMateJudge chessAndMateJudge,
                             SecurityService securityService,
                             ChessGameRepository chessGameRepository) {
        this.tableBuilderService = tableBuilderService;
        this.moveValidator = moveValidator;
        this.drawJudge = drawJudge;
        this.chessAndMateJudge = chessAndMateJudge;
        this.securityService = securityService;
        this.chessGameRepository = chessGameRepository;
    }

    public void handleMove(ChessMoveDto chessMoveDto) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChessGame chessGameFromQ = QChessGame.chessGame;

        User user = securityService.getChallenger();
        //TODO ezt a repoból vegyük le, hogy persistence legyen

        ChessGame chessGame = chessGameRepository.findById(chessMoveDto.getGameId())
                .orElseThrow(()-> new NotFoundException("Game can not be found"));

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

        if (!validPromoteType(chessMoveDto)) {
            throw new InvalidException("Not valid promotion type!");
        }

        ChessTable chessTable = tableBuilderService.buildTable(chessMoveDto.getGameId());


        if (moveValidator.validmove(chessTable, chessMoveDto)) {
            makeMove(chessGame, chessTable, chessMoveDto);
        }

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

    public void makeMove(ChessGame chessGame, ChessTable chessTable, ChessMoveDto chessMoveDto) {
        //TODO makemove method

        //mozgatandó figura keresése

        //célfigura keresése

        //mozgás ill. ütés

        //chessgame ill. chesstable esetén változtatás végzése

        //chessgame mentése

        //chessmove létrehozása és mentése

    }

    public boolean drawCheck(Long chessGameid) {
        //ezt áttenni a sakk/matt figyelő szervízbe
        return drawJudge.checkDraw(chessGameid);
    }

    public boolean checkMateCheck() {
        //TODO checkMate checking - no idea how
        return false;
    }

}
