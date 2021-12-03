package org.amazons.mainui;

/**
 * Class to hold the SquareInfos of a game
 */
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

    public SquareInfo[] getSquares() { return squares; }

    public int getGameSize() { return gameSize; }
}
