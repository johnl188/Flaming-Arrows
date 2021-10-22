package org.amazons;

public class BoardState {

    private SquareInfo[] squares;
    private int gameSize;

    public BoardState(SquareInfo[] squares, int gameSize) {
        this.squares = squares;
        this.gameSize = gameSize;
    }

    public SquareInfo getSquareInfo(int row, int column) {

        return squares[row * gameSize + column];
    }

    public SquareInfo setSquareInfo(SquareInfo info) {

        return squares[info.getRow() * gameSize + info.getColumn()] = info;
    }

    public int getGameSize() { return gameSize; }
}
