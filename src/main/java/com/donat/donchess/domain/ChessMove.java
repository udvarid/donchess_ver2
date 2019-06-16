package com.donat.donchess.domain;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "chess_moves")
public class ChessMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long moveId;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveFromX;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveFromY;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveToX;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveToY;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMoveId() {
        return moveId;
    }

    public void setMoveId(Long moveId) {
        this.moveId = moveId;
    }

    public Integer getMoveFromX() {
        return moveFromX;
    }

    public void setMoveFromX(Integer moveFromX) {
        this.moveFromX = moveFromX;
    }

    public Integer getMoveFromY() {
        return moveFromY;
    }

    public void setMoveFromY(Integer moveFromY) {
        this.moveFromY = moveFromY;
    }

    public Integer getMoveToX() {
        return moveToX;
    }

    public void setMoveToX(Integer moveToX) {
        this.moveToX = moveToX;
    }

    public Integer getMoveToY() {
        return moveToY;
    }

    public void setMoveToY(Integer moveToY) {
        this.moveToY = moveToY;
    }
}
