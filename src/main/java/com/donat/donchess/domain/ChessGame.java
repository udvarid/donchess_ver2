package com.donat.donchess.domain;

import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.NextMove;
import com.donat.donchess.domain.enums.Result;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "chess_games")
public class ChessGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private User userOne;

    @ManyToOne
    @NotNull
    private User userTwo;

    @Enumerated(EnumType.STRING)
    private ChessGameType chessGameType;

    @Enumerated(EnumType.STRING)
    private ChessGameStatus chessGameStatus;

    @Enumerated(EnumType.STRING)
    private NextMove nextMove;

    @Enumerated(EnumType.STRING)
    private Result result;

    private Long lastMoveId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ChessMove> chessMoves;

    //TODO később bevezetni az időperiódust és számon tartani, az egyes felhasználók rendelkezésre álló idejét


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUserOne() {
        return userOne;
    }

    public void setUserOne(User userOne) {
        this.userOne = userOne;
    }

    public User getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(User userTwo) {
        this.userTwo = userTwo;
    }

    public ChessGameType getChessGameType() {
        return chessGameType;
    }

    public void setChessGameType(ChessGameType chessGameType) {
        this.chessGameType = chessGameType;
    }

    public ChessGameStatus getChessGameStatus() {
        return chessGameStatus;
    }

    public void setChessGameStatus(ChessGameStatus chessGameStatus) {
        this.chessGameStatus = chessGameStatus;
    }

    public NextMove getNextMove() {
        return nextMove;
    }

    public void setNextMove(NextMove nextMove) {
        this.nextMove = nextMove;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Long getLastMoveId() {
        return lastMoveId;
    }

    public void setLastMoveId(Long lastMoveId) {
        this.lastMoveId = lastMoveId;
    }

    public List<ChessMove> getChessMoves() {
        return chessMoves;
    }

    public void setChessMoves(List<ChessMove> chessMoves) {
        this.chessMoves = chessMoves;
    }
}
