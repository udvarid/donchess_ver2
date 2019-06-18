package com.donat.donchess.model;

import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;

public class Figure {

    private ChessFigure figureType;

    private Color color;

    private int coordX;

    private int coordY;

    private boolean moved;

    public Figure(ChessFigure figureType, Color color, int coordX, int coordY) {
        this.figureType = figureType;
        this.color = color;
        this.coordX = coordX;
        this.coordY = coordY;
        this.moved = false;
    }

    public ChessFigure getFigureType() {
        return figureType;
    }

    public void setFigureType(ChessFigure figureType) {
        this.figureType = figureType;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
