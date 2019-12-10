package com.donat.donchess.service;

import java.util.Random;
import java.util.Set;
import javax.transaction.Transactional;
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
		return chessMoveDto;
	}

	private ChessMoveDto simpleStrategy(ChessGameDto game) {
		ValidMovesDto validMoves = gameMasterService.giveValidMoves(game.getChessGameId());
		ChessTableDto chessTableDto = gameMasterService.giveChessTable(game.getChessGameId());
		int index = random.nextInt(validMoves.getValidMoves().size());
		CoordinateDto validMove = validMoves.getValidMoves().get(index);
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
