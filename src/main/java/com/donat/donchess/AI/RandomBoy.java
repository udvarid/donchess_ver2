package com.donat.donchess.AI;

import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.CoordinateDto;

public class RandomBoy extends EvaluatorWeight implements Evaluator {

	public RandomBoy(int weight) {
		super(weight);
	}

	@Override
	public int giveScore(CoordinateDto validMove, ChessTableDto chessTable) {
		return 0;
	}
}
