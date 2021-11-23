package org.amazons.mainui;

public class Amazon extends SquareInfo
{
    public Amazon(byte row, byte column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;

        if (isWhite) {
            this.imageFileName = "white_piece.png";
        }

        else {
            this.imageFileName = "black_piece.png";
        }
    }

    @Override
    public SquareInfo makeCopy() {
        return new Amazon(row, column, isWhite);
    }
}
