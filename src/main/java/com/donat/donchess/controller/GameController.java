package com.donat.donchess.controller;

import com.donat.donchess.dto.chessGame.ChessGameDto;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.ValidMovesDto;
import com.donat.donchess.service.GameMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private GameMasterService gameMasterService;

    public GameController(GameMasterService gameMasterService) {
        this.gameMasterService = gameMasterService;
    }

    @PostMapping("/move")
    public ResponseEntity create(@RequestBody ChessMoveDto chessMoveDto) {
        gameMasterService.handleMove(chessMoveDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping
    public List<ChessGameDto> allChessGame() {
        return gameMasterService.findChessgames();
    }


    @GetMapping("/{id}")
    public ChessTableDto findChessGame(@PathVariable("id") long chessGameId) {
        return gameMasterService.giveChessTable(chessGameId);
    }

    @GetMapping("/validMoves/{id}")
    public ValidMovesDto findValidMoves(@PathVariable("id") long chessGameId) {
        return gameMasterService.giveValidMoves(chessGameId);
    }

    //TODO propose draw action + all the necessery methods to handle this

}
