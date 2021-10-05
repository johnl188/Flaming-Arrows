package org.example;

public class Amazon extends SquareInfo
{
    public Amazon(int row, int column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;
        this.squareType = SquareType.Amazon;

        if (isWhite) {
            this.imageFileName = "white_queen.png";
        }

        else {
            this.imageFileName = "black_queen.png";
        }
    }
}
