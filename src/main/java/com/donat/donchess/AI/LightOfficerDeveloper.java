package com.donat.donchess.AI;

import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.CoordinateDto;
import com.donat.donchess.dto.chessGame.FigureDto;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;

public class LightOfficerDeveloper extends EvaluatorWeight implements Evaluator {

	public LightOfficerDeveloper(int weight) {
		super(weight);
	}

	@Override
	public int giveScore(CoordinateDto validMove, ChessTableDto chessTable) {
		int score = calculateInitScore(chessTable) + currentMoveScoreIfLightOfficer(validMove, chessTable);
		return score * getWeight();
	}

	private int currentMoveScoreIfLightOfficer(CoordinateDto validMove, ChessTableDto chessTable) {
		FigureDto figure = findFigure(validMove, chessTable);
		if (figureIsLightOfficer(figure) && !figure.isMoved()) {
			return 250;
		}
		return 0;
	}

	private FigureDto findFigure(CoordinateDto validMove, ChessTableDto chessTable) {
		return chessTable.getFigures()
			.stream()
			.filter(f -> f.getCoordX() == validMove.getFromX() &&
				f.getCoordY() == validMove.getFromY())
			.findFirst()
			.orElse(null);
	}

	private int calculateInitScore(ChessTableDto chessTable) {
		int blackScore = 0;
		int whiteScore = 0;
		for (FigureDto figure : chessTable.getFigures()) {
			if (figureIsLightOfficer(figure) && figure.isMoved()) {
				if (figure.getClass().equals(Color.BLACK)) {
					blackScore += 250;
				} else {
					whiteScore += 250;
				}
			}
		}
		return chessTable.getNextMove().equals(Color.WHITE) ?
			whiteScore - blackScore : blackScore - whiteScore;
	}

	private boolean figureIsLightOfficer(FigureDto figure) {
		return figure.getFigureType().equals(ChessFigure.KNIGHT) || figure.getFigureType().equals(ChessFigure.BISHOP);
	}
}
