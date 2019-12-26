package com.donat.donchess.AI;

import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.CoordinateDto;
import com.donat.donchess.dto.chessGame.FigureDto;
import com.donat.donchess.model.enums.ChessFigure;

public class StupidMaterial extends EvaluatorWeight implements Evaluator {

	public StupidMaterial(int weight) {
		super(weight);
	}

	@Override
	public int giveScore(CoordinateDto validMove, ChessTableDto chessTable) {
		return getFigureValue(validMove, chessTable) * getWeight();
	}

	private int getFigureValue(CoordinateDto validMove, ChessTableDto chessTableDto) {
		for (FigureDto figure : chessTableDto.getFigures()) {
			if (figure.getCoordX() == validMove.getToX() && figure.getCoordY() == validMove.getToY()) {
				return giveValue(figure.getFigureType());
			}
		}
		return 0;
	}

	private Integer giveValue(ChessFigure figureType) {
		int value = 0;
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

}
