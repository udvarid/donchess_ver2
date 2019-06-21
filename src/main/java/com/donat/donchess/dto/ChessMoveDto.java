package com.donat.donchess.dto;

public class ChessMoveDto {

    private Long gameId;

    private Long moveId;

    private int moveFromX;

    private int moveFromY;

    private int moveToX;

    private int moveToY;

    private String specialMoveType;

    private String promoteToFigure;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getMoveId() {
        return moveId;
    }

    public void setMoveId(Long moveId) {
        this.moveId = moveId;
    }

    public int getMoveFromX() {
        return moveFromX;
    }

    public void setMoveFromX(int moveFromX) {
        this.moveFromX = moveFromX;
    }

    public int getMoveFromY() {
        return moveFromY;
    }

    public void setMoveFromY(int moveFromY) {
        this.moveFromY = moveFromY;
    }

    public int getMoveToX() {
        return moveToX;
    }

    public void setMoveToX(int moveToX) {
        this.moveToX = moveToX;
    }

    public int getMoveToY() {
        return moveToY;
    }

    public void setMoveToY(int moveToY) {
        this.moveToY = moveToY;
    }

    public String getSpecialMoveType() {
        return specialMoveType;
    }

    public void setSpecialMoveType(String specialMoveType) {
        this.specialMoveType = specialMoveType;
    }

    public String getPromoteToFigure() {
        return promoteToFigure;
    }

    public void setPromoteToFigure(String promoteToFigure) {
        this.promoteToFigure = promoteToFigure;
    }
}
