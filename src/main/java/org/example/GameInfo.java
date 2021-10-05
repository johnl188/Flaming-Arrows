package org.example;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class GameInfo {

    private int gameSize;
    private GameSquare[] rectangles;

    public GameInfo(int gameSize) {
        this.gameSize = gameSize;

        rectangles = new GameSquare[gameSize * gameSize];
    }


    public void addSquare(int row, int column, GameSquare square) {
        rectangles[gameSize * row + column] = square;
    }

    public GameSquare getSquare(int row, int column) {
        return rectangles[gameSize * row + column];
    }

    public void movePiece (SquareInfo from, SquareInfo to) {

        GameSquare fromSquare = getSquare(from.getRow(), from.getColumn());
        GameSquare toSquare = getSquare(to.getRow(), to.getColumn());

        fromSquare.removePiece();
        toSquare.addAmazon(from.isWhite);
    }
}
