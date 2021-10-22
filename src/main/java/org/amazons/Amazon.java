package org.amazons;

public class Amazon extends SquareInfo
{
    public Amazon(int row, int column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;

        if (isWhite) {
            this.imageFileName = "white_queen.png";
        }

        else {
            this.imageFileName = "black_queen.png";
        }
    }

    @Override
    public SquareInfo makeCopy() {
        return new Amazon(row, column, isWhite);
    }
}
