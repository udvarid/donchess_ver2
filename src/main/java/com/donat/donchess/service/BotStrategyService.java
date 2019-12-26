package com.donat.donchess.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;

import com.donat.donchess.AI.Brain;
import com.donat.donchess.AI.Evaluator;
import com.donat.donchess.AI.LightOfficerDeveloper;
import com.donat.donchess.AI.RandomBoy;
import com.donat.donchess.AI.StupidMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.donat.donchess.dto.chessGame.ChessGameDto;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.CoordinateDto;
import com.donat.donchess.dto.chessGame.FigureDto;
import com.donat.donchess.dto.chessGame.ValidMovesDto;
import com.donat.donchess.model.enums.Color;

@Service
@Transactional
public class BotStrategyService {

    @Autowired
    private GameMasterService gameMasterService;

    public ChessMoveDto giveMove(ChessGameDto game) {
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        Brain brain = new Brain();
        if (getRole(game).equals("ROLE_BOT0")) {
            brain.addEvaluator(new RandomBoy(100));
            chessMoveDto = strategy(game, brain);
        }
        if (getRole(game).equals("ROLE_BOT1")) {
            brain.addEvaluator(new StupidMaterial(100));
            chessMoveDto = strategy(game, brain);
        }
        if (getRole(game).equals("ROLE_BOT2")) {
            brain.addEvaluator(new LightOfficerDeveloper(5));
            chessMoveDto = strategy(game, brain);
        }
        return chessMoveDto;
    }


    private ChessMoveDto strategy(ChessGameDto game, Brain brain) {
        ValidMovesDto validMoves = gameMasterService.giveValidMoves(game.getChessGameId());
        ChessTableDto chessTableDto = gameMasterService.giveChessTable(game.getChessGameId());

        List<Integer> scores = new ArrayList<>();
        Integer max = Integer.MIN_VALUE;
        for (CoordinateDto validMove : validMoves.getValidMoves()) {
            Integer score = 0;
            for (Evaluator evaluator : brain.getEvaluators()) {
                score += evaluator.giveScore(validMove, chessTableDto);
            }
            scores.add(score);
            if (max < score) {
                max = score;
            }
        }

        List<Integer> maxScores = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).equals(max)) {
                maxScores.add(i);
            }
        }

        Collections.shuffle(maxScores);
        CoordinateDto validMove = validMoves.getValidMoves().get(maxScores.get(0));
        return getChessMoveDto(game, chessTableDto, validMove);
    }

    private ChessMoveDto getChessMoveDto(ChessGameDto game, ChessTableDto chessTableDto, CoordinateDto validMove) {
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        chessMoveDto.setDrawOffered(false);
        chessMoveDto.setPromoteToFigure(promotionType(validMove, chessTableDto));
        chessMoveDto.setGameId(game.getChessGameId());
        chessMoveDto.setMoveId(game.getLastMoveId() + 1);
        chessMoveDto.setMoveFromX(validMove.getFromX());
        chessMoveDto.setMoveFromY(validMove.getFromY());
        chessMoveDto.setMoveToX(validMove.getToX());
        chessMoveDto.setMoveToY(validMove.getToY());
        return chessMoveDto;
    }

    private String getRole(ChessGameDto game) {
        return game.getNextMove().equals(Color.WHITE) ?
                game.getUserOne().getRole() : game.getUserTwo().getRole();
    }

    private String promotionType(CoordinateDto validMove, ChessTableDto chessTableDto) {
        FigureDto figure = findFigure(chessTableDto.getFigures(), validMove);
        if (figure.getColor().equals(Color.WHITE) && validMove.getToY() == 8 ||
                figure.getColor().equals(Color.BLACK) && validMove.getToY() == 1) {
            return "QUEEN";
        }
        return null;
    }

    private FigureDto findFigure(Set<FigureDto> figures, CoordinateDto validMove) {
        return figures.stream()
                .filter(figure -> figure.getCoordX() == validMove.getFromX() &&
                        figure.getCoordY() == validMove.getFromY())
                .findFirst().orElse(null);
    }
}
