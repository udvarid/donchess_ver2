package com.donat.donchess.AI;

import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.CoordinateDto;

public interface Evaluator {

	int giveScore(CoordinateDto validMove, ChessTableDto chessTable);


}
