package com.donat.donchess.service;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.donat.donchess.model.enums.ChessFigure;
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

    private Random random = new Random();

    public ChessMoveDto giveMove(ChessGameDto game) {
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        if (getRole(game).equals("ROLE_BOT0")) {
            chessMoveDto = simpleStrategy(game);
        }
        if (getRole(game).equals("ROLE_BOT1")) {
            chessMoveDto = materialStrategy(game);
        }
        return chessMoveDto;
    }

    private ChessMoveDto materialStrategy(ChessGameDto game) {
        ValidMovesDto validMoves = gameMasterService.giveValidMoves(game.getChessGameId());
        ChessTableDto chessTableDto = gameMasterService.giveChessTable(game.getChessGameId());
        int initScore = calculateTableValue(chessTableDto);
        int maxScore = initScore;
		CoordinateDto choosenCoordinate = validMoves.getValidMoves().get(0);
        for (CoordinateDto validMove : validMoves.getValidMoves()) {
			int killedFigureValue = getFigureValue(validMove, chessTableDto);
			if (initScore + killedFigureValue > maxScore) {
				choosenCoordinate = validMove;
				maxScore = initScore + killedFigureValue;
			}
        }
        return getChessMoveDto(game, chessTableDto,choosenCoordinate);
    }

	private int getFigureValue(CoordinateDto validMove, ChessTableDto chessTableDto) {
		for (FigureDto figure : chessTableDto.getFigures()) {
			if (figure.getCoordX() == validMove.getToX() && figure.getCoordY() == validMove.getToY()) {
				return giveValue(figure.getFigureType());
			}
		}
    	return 0;
	}


	private int calculateTableValue(ChessTableDto chessTable) {
        int whiteValue = chessTable.getFigures()
                .stream()
                .filter(figureDto -> figureDto.getColor().equals(Color.WHITE))
                .map(figureDto -> giveValue(figureDto.getFigureType()))
                .mapToInt(Integer::intValue)
                .sum();
        int blackValue = chessTable.getFigures()
                .stream()
                .filter(figureDto -> figureDto.getColor().equals(Color.BLACK))
                .map(figureDto -> giveValue(figureDto.getFigureType()))
                .mapToInt(Integer::intValue)
                .sum();
        return chessTable.getNextMove().equals(Color.WHITE) ? whiteValue - blackValue : blackValue - whiteValue;
    }

    private Integer giveValue(ChessFigure figureType) {
        Integer value = 0;
        if (figureType.equals(ChessFigure.PAWN)) {
            value = 1;
        } else if (figureType.equals(ChessFigure.ROOK)) {
            value = 5;
        } else if (figureType.equals(ChessFigure.KNIGHT)) {
            value = 3;
        } else if (figureType.equals(ChessFigure.BISHOP)) {
            value = 3;
        } else if (figureType.equals(ChessFigure.QUEEN)) {
            value = 8;
        }
        return value;
    }


    private ChessMoveDto simpleStrategy(ChessGameDto game) {
        ValidMovesDto validMoves = gameMasterService.giveValidMoves(game.getChessGameId());
        ChessTableDto chessTableDto = gameMasterService.giveChessTable(game.getChessGameId());
        int index = random.nextInt(validMoves.getValidMoves().size());
        CoordinateDto validMove = validMoves.getValidMoves().get(index);
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
