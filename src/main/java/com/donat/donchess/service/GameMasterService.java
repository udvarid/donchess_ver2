package com.donat.donchess.service;

import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.ChessMove;
import com.donat.donchess.domain.User;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.dto.ChessMoveDto;
import com.donat.donchess.exceptions.InvalidException;
import com.donat.donchess.exceptions.NotFoundException;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.DrawJudge;
import com.donat.donchess.model.logic.MoveValidator;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import com.donat.donchess.repository.ChessGameRepository;
import com.donat.donchess.repository.ChessMoveRepository;
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
    private SecurityService securityService;
    private ChessGameRepository chessGameRepository;
    private ChessMoveRepository chessMoveRepository;

    @Autowired
    private Provider<EntityManager> entityManager;

    public GameMasterService(TableBuilderService tableBuilderService,
                             MoveValidator moveValidator,
                             DrawJudge drawJudge,
                             SecurityService securityService,
                             ChessGameRepository chessGameRepository,
                             ChessMoveRepository chessMoveRepository) {
        this.tableBuilderService = tableBuilderService;
        this.moveValidator = moveValidator;
        this.drawJudge = drawJudge;
        this.securityService = securityService;
        this.chessGameRepository = chessGameRepository;
        this.chessMoveRepository = chessMoveRepository;
    }

    public void handleMove(ChessMoveDto chessMoveDto) {

        User user = securityService.getChallenger();

        ChessGame chessGame = chessGameRepository.findById(chessMoveDto.getGameId())
                .orElseThrow(() -> new NotFoundException("Game can not be found"));

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

        ValidMove validMove = moveValidator.validmove(chessTable, chessMoveDto);

        if (validMove != null) {
            makeMove(chessGame, chessTable, chessMoveDto, validMove);
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

    public void makeMove(ChessGame chessGame, ChessTable chessTable, ChessMoveDto chessMoveDto, ValidMove validMove) {
        Figure figure = moveValidator
                .findFigure(chessTable.getFigures(),
                        new Coordinate(chessMoveDto.getMoveFromX(), chessMoveDto.getMoveFromY()));
        Figure aimFigure = moveValidator
                .findFigure(chessTable.getFigures(),
                        new Coordinate(chessMoveDto.getMoveToX(), chessMoveDto.getMoveToY()));

        chessTable.setWhoIsNext(changeColor(chessTable.getWhoIsNext()));
        chessTable.setActualMoveNumber(chessTable.getActualMoveNumber() + 1);
        if (figure.getFigureType().equals(ChessFigure.PAWN)) {
            chessTable.setLastPawnMoveNumber(chessTable.getActualMoveNumber());
            if (Math.abs(chessMoveDto.getMoveFromY() - chessMoveDto.getMoveToY()) > 1) {
                chessTable.setLastMoveWasDoublePawn(true);
                chessTable.setColumnIndexIfLastMoveWasDoublePawn(figure.getCoordY());
            }
        }

        if (aimFigure != null) {
            chessTable.getFigures().remove(aimFigure);
            chessTable.setLastHitMoveNumber(chessTable.getActualMoveNumber());
        }

        figure.setCoordX(chessMoveDto.getMoveToX());
        figure.setCoordY(chessMoveDto.getMoveToY());
        figure.setMoved(true);
        if (!chessMoveDto.getPromoteToFigure().isEmpty()) {
            figure.setFigureType(ChessFigure.valueOf(chessMoveDto.getPromoteToFigure()));
        }

        ChessMove chessMove = new ChessMove();
        chessMove.setSpecialMoveType(validMove.getSpecialMoveType());
        chessMove.setPromoteType(ChessFigure.valueOf(chessMoveDto.getPromoteToFigure()));
        chessMove.setMoveFromX(chessMoveDto.getMoveFromX());
        chessMove.setMoveFromY(chessMoveDto.getMoveFromY());
        chessMove.setMoveToX(chessMoveDto.getMoveToX());
        chessMove.setMoveToY(chessMoveDto.getMoveToY());
        chessMove.setMoveId(chessGame.getLastMoveId() + 1);
        chessMove.setChessGiven(chessGiven(chessTable, figure));

        chessMoveRepository.saveAndFlush(chessMove);

        //TODO döntetlen/győzelem kiértékelés és result ill chessGameStatus állítása ha kell
        chessGame.getChessMoves().add(chessMove);
        chessGame.setLastMoveId(chessGame.getLastMoveId() + 1);
        chessGame.setNextMove(changeColor(chessGame.getNextMove()));

        chessGameRepository.saveAndFlush(chessGame);
    }

    private Boolean chessGiven(ChessTable chessTable, Figure figure) {

        Set<ValidMove> validMoves = moveValidator.allValidMoves(chessTable,
                new Coordinate(figure.getCoordX(), figure.getCoordY()));

        Figure enemyKing = chessTable
                .getFigures()
                .stream()
                .filter(king -> king.getFigureType().equals(ChessFigure.KING) &&
                        !king.getColor().equals(figure.getColor()))
                .findFirst()
                .orElseThrow(()-> new NotFoundException("King not found!"));

        return validMoves
                .stream()
                .anyMatch(vm -> vm.getCoordinate().equals(new Coordinate(enemyKing.getCoordX(), enemyKing.getCoordY())));


    }

    private Color changeColor(Color nextMove) {
        return nextMove.equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
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
