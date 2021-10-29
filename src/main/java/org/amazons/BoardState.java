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

    public SquareInfo[] getSquares() { return squares; }

    public int getGameSize() { return gameSize; }

    public void printBoardState() {

        System.out.println();

        for(int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {
                SquareInfo info = squares[i * gameSize + j];

                if (info instanceof Fire) {
                    System.out.print("f ");
                }

                else if (info instanceof Amazon) {

                    if (info.getIsWhite()) {
                        System.out.print("w ");
                    }

                    else {
                        System.out.print("b ");

                    }
                }

                else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }


    }
}
