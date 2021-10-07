package org.example;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {

    private int gameSize;
    private GameSquare[] rectangles;
    private boolean isWhitesTurn;
    private boolean isMove;
    private GameSquare lastMove;
    private Label lblInfo;

    public GameInfo(int gameSize, Label lblInfo) {
        this.gameSize = gameSize;
        this.lblInfo = lblInfo;

        rectangles = new GameSquare[gameSize * gameSize];
        isMove = true;
        isWhitesTurn = true;

        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");
    }

    public boolean getIsMove() { return isMove; }

    public boolean getIsWhitesTurn() { return isWhitesTurn; }

    public void goToMove () { isMove = true; }

    public void goToArrow() {
        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to shoot an arrow");
        isMove = false;
    }

    public void switchTurns() {
        goToMove();
        isWhitesTurn = !isWhitesTurn;
        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        if (isGameOver()) {
            lblInfo.setText((!isWhitesTurn ? "White's" : "Black's") + " wins!");
        }
    }

    public GameSquare getLastMove() { return lastMove; }

    public void setLastMove(GameSquare lastMove) { this.lastMove = lastMove; }

    public void addSquare(GameSquare square) {
        SquareInfo info = square.getSquareInfo();
        rectangles[gameSize * info.getRow() + info.getColumn()] = square;
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

    public boolean isGameOver() {
        for(GameSquare square: rectangles) {
            SquareInfo info = square.getSquareInfo();
            if (info.getSquareType() == SquareType.Amazon && info.isWhite() == isWhitesTurn) {
                ArrayList<GameSquare> list = getValidSquares(square);
                if (list.size() > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public void resetGame() {

        isMove = true;
        isWhitesTurn = true;

        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        for(GameSquare square: rectangles) {
            square.removePiece();
        }
    }


    public ArrayList<GameSquare> getValidSquares(GameSquare square) {
        ArrayList<GameSquare> list = new ArrayList<>();

        SquareInfo info = square.getSquareInfo();

        int startRow = info.getRow();
        int startColumn = info.getColumn();

        boolean done = true;

        checkAbove(list, startRow, startColumn);
        checkBelow(list, startRow, startColumn);
        checkLeft(list, startRow, startColumn);
        checkRight(list, startRow, startColumn);
        checkLeftUp(list, startRow, startColumn);
        checkLeftDown(list, startRow, startColumn);
        checkRightUp(list, startRow, startColumn);
        checkRightDown(list, startRow, startColumn);

        return list;
    }

    private void checkAbove(List<GameSquare> list, int row, int column) {

        row--;

        while(row >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row--;
            }

            else {
                return;
            }
        }
    }

    private void checkBelow(List<GameSquare> list, int row, int column) {
        row++;

        while(row < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row++;
            }

            else {
                return;
            }
        }
    }

    private void checkLeft(List<GameSquare> list, int row, int column) {
        column--;

        while(column >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                column--;
            }

            else {
                return;
            }
        }
    }

    private void checkRight(List<GameSquare> list, int row, int column) {
        column++;

        while(column < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                column++;
            }

            else {
                return;
            }
        }
    }

    private void checkLeftUp(List<GameSquare> list, int row, int column) {
        row--;
        column--;
        while(row >= 0 && column >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row--;
                column--;
            }

            else {
                return;
            }
        }
    }

    private void checkLeftDown(List<GameSquare> list, int row, int column) {
        row++;
        column--;

        while(row < gameSize && column >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row++;
                column--;
            }

            else {
                return;
            }
        }
    }

    private void checkRightUp(List<GameSquare> list, int row, int column) {
        row--;
        column++;

        while(row >= 0 && column < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row--;
                column++;
            }

            else {
                return;
            }
        }
    }

    private void checkRightDown(List<GameSquare> list, int row, int column) {
        row++;
        column++;

        while(row < gameSize && column < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row++;
                column++;
            }

            else {
                return;
            }
        }
    }
}
